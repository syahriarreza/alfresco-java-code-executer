package com.itdhq.ajce.example;

import java.util.HashMap;
import java.util.Map;
import org.springframework.extensions.webscripts.Cache;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class WebscriptTemplate extends DeclarativeWebScript {
    private static final Logger logger = LoggerFactory.getLogger(WebscriptDownloadTemplate.class);
	private PermissionService permissionService;
	private ContentService contentService;
	private Repository repository;
	private FileFolderService fileFolderService;
	
	private String hostname;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		//--Get Params
        String verboseArg = req.getParameter("verbose");
        Boolean verbose = Boolean.parseBoolean(verboseArg);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("verbose", verbose);
        return model;
    }
	
	// ### Setters ###
	
	public void setContentService(final ContentService contentService) {
		this.contentService = contentService;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}