package edu.asu.diging.gilesecosystem.carolus.core.linnaeus;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IFileService {

    String getStoragePath(ICompletedStorageRequest request);

    byte[] getFileContent(String requestId, String documentId, String filename);

    void deleteFile(String requestId, String documentId, String filename);

    String getCSVFilename(ICompletedStorageRequest request);

}