package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.gilesecosystem.carolus.core.linnaeus.ILinnaeusService;
import edu.asu.diging.gilesecosystem.carolus.core.linnaeus.IFileService;
import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.service.ICompletionNotifier;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import uk.ac.man.documentparser.dataholders.Document;
import uk.ac.man.entitytagger.Mention;
import uk.ac.man.entitytagger.doc.TaggedDocument;
import uk.ac.man.entitytagger.matching.MatchOperations;
import uk.ac.man.entitytagger.matching.Matcher;
import uk.ac.man.entitytagger.matching.Matcher.Disambiguation;
import uk.ac.man.entitytagger.matching.Postprocessor;
import uk.ac.man.entitytagger.matching.matchers.MatchPostProcessor;
import uk.ac.man.entitytagger.matching.matchers.VariantDictionaryMatcher;

@Service
public class LinnaeusService implements ILinnaeusService {

    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @Autowired
    private IFileService pathService;
    
    @Autowired
    private ICompletionNotifier completionNotifier;

    private Resource speciesDictionary;
    private Matcher speciesMatcher;

    @PostConstruct
    public void init() throws IOException {
        speciesDictionary = appContext
                .getResource("classpath:resources-linnaeus/species-light.tsv");
        speciesMatcher = VariantDictionaryMatcher.load(speciesDictionary.getFile(), true);
        Disambiguation disambiguation = Disambiguation.ON_WHOLE;
        speciesMatcher = new MatchPostProcessor(speciesMatcher, disambiguation, true,
                null, getPostprocessor(new HashMap<String, String>(), ""));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl.ILinnaeusService
     * #runLinnaeus(edu.asu.diging.gilesecosystem.requests.impl.StorageRequest)
     */
    @Override
    public void runLinnaeus(ICompletedStorageRequest request) {

        byte[] text = downloadFile(request.getDownloadUrl());
        String contentType = new Tika().detect(text);
        if (!contentType.equals("text/plain") || request.getFileType() != FileType.TEXT) {
            // if file is not a text file, ignore
            return;
        }
        
        Document doc = new Document(null, null, null, null, new String(text),
                Document.Text_raw_type.TEXT, null, null, Document.Type.OTHER, null, "", "",
                "", "", null);
        TaggedDocument tagged = MatchOperations.matchDocument(speciesMatcher, doc);
        List<Mention> species = tagged.getAllMatches();

        List<SpeciesMention> speciesMentions = new ArrayList<>();
        for (Mention s : species) {
            speciesMentions.add(new SpeciesMention(s.getStart(), s.getEnd(),
                    s.getMostProbableID(), s.getText()));
        }
        
        RequestStatus status = RequestStatus.COMPLETE;
        try {
            writeResults(speciesMentions, request);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not write found species to CSV file.", e, MessageType.ERROR);
            status = RequestStatus.FAILED;
        }
        
        completionNotifier.sendNotification(request, status);
    }

    private Postprocessor getPostprocessor(Map<String, String> comments, String tag)
            throws IOException {
        InputStream stop = appContext
                .getResource("classpath:resources-linnaeus/stoplist.tsv")
                .getInputStream();
        InputStream acr = appContext
                .getResource("classpath:resources-linnaeus/synonyms-acronyms.tsv")
                .getInputStream();
        InputStream spf = appContext
                .getResource("classpath:resources-linnaeus/species-frequency.tsv")
                .getInputStream();

        Postprocessor res = new Postprocessor(new InputStream[] { stop },
                new InputStream[] { acr }, new InputStream[] { spf }, comments, null);

        if (stop != null)
            stop.close();
        if (acr != null)
            acr.close();
        if (spf != null)
            spf.close();

        return res;
    }
    
    private void writeResults(List<SpeciesMention> mentions, ICompletedStorageRequest request) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(pathService.getStoragePath(request)));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Found text", "ID", "start", "end"));
   
        for (SpeciesMention mention : mentions) {
            csvPrinter.printRecord(mention.getFoundText(), mention.getId(), mention.getStart(), mention.getEnd());
        }
        
        csvPrinter.flush();            
        csvPrinter.close();
    }

    /**
     * Download file from Nepomuk.
     * 
     * @param url
     * @return
     */
    private byte[] downloadFile(String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.TEXT_XML));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET,
                entity, byte[].class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        }
        return null;
    }
}
