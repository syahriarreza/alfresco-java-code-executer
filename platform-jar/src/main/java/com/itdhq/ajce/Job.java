package com.itdhq.ajce;

import org.alfresco.service.ServiceRegistry;

import java.io.Serializable;
import org.alfresco.repo.model.Repository;

public interface Job {
    Serializable run(ServiceRegistry services, Repository repository);
}
