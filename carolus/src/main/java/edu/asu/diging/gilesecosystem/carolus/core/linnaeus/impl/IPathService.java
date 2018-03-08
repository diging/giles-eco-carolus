package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;

public interface IPathService {

    String getStoragePath(ICompletedStorageRequest request);

}