package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

import java.io.File;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.carolus.core.linnaeus.IFileService;
import edu.asu.diging.gilesecosystem.carolus.core.util.Properties;
import edu.asu.diging.gilesecosystem.carolus.rest.DownloadFileController;
import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.service.impl.ACompletionNotifier;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class CompletionNotifier extends ACompletionNotifier {

    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private IFileService fileService;
    
    @Override
    public void fillRequest(ICompletionNotificationRequest completionRequest,
            IRequest request) {
        completionRequest
                .setNotifier(propertiesManager.getProperty(Properties.NOTIFIER_ID));
        completionRequest.setDocumentId(request.getDocumentId());
        completionRequest.setFileId(request.getFileId());
        String filename = fileService.getCSVFilename((ICompletedStorageRequest) request);
        completionRequest.setDownloadUrl(getRestEndpoint() + DownloadFileController.GET_FILE_URL
                .replace(DownloadFileController.DOCUMENT_ID_PLACEHOLDER,
                        request.getDocumentId())
                .replace(DownloadFileController.REQUEST_ID_PLACEHOLDER,
                        request.getRequestId())
                .replace(DownloadFileController.FILENAME_PLACEHOLDER, filename));
        completionRequest.setFilename(filename);
        
        completionRequest.setContentType("text/csv");
        String filepath = fileService.getStoragePath((ICompletedStorageRequest)request);
        completionRequest.setSize(new File(filepath).length());
    }

    @Override
    public String getRequestPrefix() {
        return "CAROLUS_";
    }
    
    protected String getRestEndpoint() {
        String restEndpoint = propertiesManager.getProperty(Properties.BASE_URL);
        if (restEndpoint.endsWith("/")) {
            restEndpoint = restEndpoint.substring(0, restEndpoint.length()-1);
        }
        return restEndpoint;
    }

}
