package edu.asu.diging.gilesecosystem.carolus.core.linnaeus.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.carolus.core.util.Properties;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.service.impl.ACompletionNotifier;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;

@Service
public class CompletionNotifier extends ACompletionNotifier {
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @Override
    public void fillRequest(ICompletionNotificationRequest completionRequest, IRequest request) {
        completionRequest.setNotifier(propertiesManager.getProperty(Properties.NOTIFIER_ID));
        completionRequest.setDocumentId(request.getDocumentId());
        completionRequest.setFileId(request.getFileId());
    }

    @Override
    public String getRequestPrefix() {
        return "CAROLUS_";
    }

    
}
