package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.carolus.core.util.Properties;
import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class PathService implements IPathService {

    @Autowired
    private IFileStorageManager fileStorageManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl.IPathService#getStoragePath(edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest)
     */
    @Override
    public String getStoragePath(ICompletedStorageRequest request) {
        File storageFolder = fileStorageManager.createFolder(request.getUsername(), null, null, request.getRequestId());
        return storageFolder.getAbsolutePath() + File.separator + request.getFilename() + propertiesManager.getProperty(Properties.CSV_FILENAME_APPENDAGE);
    }
}
