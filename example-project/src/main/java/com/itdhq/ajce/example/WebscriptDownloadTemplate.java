package com.itdhq.ajce.example;

import java.io.IOException;
import java.util.Locale;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.web.scripts.content.StreamContent;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class WebscriptDownloadTemplate extends StreamContent {

	private static final Logger logger = LoggerFactory.getLogger(WebscriptDownloadTemplate.class);
	private PermissionService permissionService;
	private ContentService contentService;
	private Repository repository;
	private FileFolderService fileFolderService;

	private String hostname;

	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		try {

			final NodeRef nodeRef = getParameterAsNodeRef(request, "nodeRef");
			final boolean attach = Boolean.valueOf(request.getParameter("attach")); //--true: will preview the node, false: will download the node

			processDownload(request, response, nodeRef, attach);

		} catch (IOException | AlfrescoRuntimeException | InvalidNodeRefException excp) {
			logger.error("Exception occurred while downloading content", excp);
			throw new WebScriptException(Status.STATUS_INTERNAL_SERVER_ERROR, excp.getMessage(), excp);
		}
	}

	/**
	 * Process download will process the nodeRef given to streamContent.
	 *
	 * @param request the request
	 * @param response the response
	 * @param nodeRef the node ref
	 * @param attach the attach
	 * @throws IOException the IO exception
	 */
	private void processDownload(final WebScriptRequest request, final WebScriptResponse response, final NodeRef nodeRef, final boolean attach) throws IOException {
		String userAgent = request.getHeader("User-Agent");
		userAgent = StringUtils.isNotBlank(userAgent) ? userAgent.toLowerCase(Locale.ENGLISH) : StringUtils.EMPTY;
		final boolean isClientSupported = userAgent.contains("msie") || userAgent.contains(" trident/") || userAgent.contains(" chrome/") || userAgent.contains(" firefox/");

		if (attach && isClientSupported) {
			String fileName = (String) this.nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			if (userAgent.contains("msie") || userAgent.contains(" trident/")) {
				final String mimeType = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT).getMimetype();
				if (!(this.mimetypeService.getMimetypes(FilenameUtils.getExtension(fileName)).contains(mimeType))) {
					fileName = FilenameUtils.removeExtension(fileName) + FilenameUtils.EXTENSION_SEPARATOR_STR + this.mimetypeService.getExtension(mimeType);
				}
			}
			streamContent(request, response, nodeRef, ContentModel.PROP_CONTENT, attach, fileName, null);
		} else {
			streamContent(request, response, nodeRef, ContentModel.PROP_CONTENT, attach, null, null);
		}
	}

	/**
	 * Create NodeRef instance from a WebScriptRequest parameter.
	 *
	 * @param req the req
	 * @param paramName the param name
	 * @return the parameter as node ref
	 */
	private NodeRef getParameterAsNodeRef(final WebScriptRequest req, final String paramName) {
		final String nodeRefStr = StringUtils.trimToNull(req.getParameter(paramName));
		if (StringUtils.isBlank(nodeRefStr)) {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing " + paramName + " parameter");
		}
		if (!NodeRef.isNodeRef(nodeRefStr)) {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Incorrect format for " + paramName + " paramater");
		}
		final NodeRef nodeRef = new NodeRef(nodeRefStr);
		if (!nodeService.exists(nodeRef)) {
			throw new WebScriptException(Status.STATUS_BAD_REQUEST, paramName + " not found");
		}
		return nodeRef;
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
