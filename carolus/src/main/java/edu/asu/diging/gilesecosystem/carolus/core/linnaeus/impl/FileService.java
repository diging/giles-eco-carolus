package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.carolus.core.linnaeus.IFileService;
import edu.asu.diging.gilesecosystem.carolus.core.util.Properties;
import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class FileService implements IFileService {

    @Autowired
    private IFileStorageManager fileStorageManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl.IPathService#getStoragePath(edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public String getStoragePath(ICompletedStorageRequest request) {
        File storageFolder = fileStorageManager.createFolder(request.getRequestId(), null, null, request.getDocumentId());
        return storageFolder.getAbsolutePath() + File.separator + getCSVFilename(request);
    }
    
    @Override
    public byte[] getFileContent(String requestId, String documentId, String filename) {
        return fileStorageManager.getFileContent(requestId, documentId, null, filename);
    }
    
    @Override
    public void deleteFile(String requestId, String documentId, String filename) {
        fileStorageManager.deleteFile(requestId, documentId, null, filename, true);
    }
    
    @Override
    public String getCSVFilename(ICompletedStorageRequest request) {
        return request.getFilename() + propertiesManager.getProperty(Properties.CSV_FILENAME_APPENDAGE);
    }
}
