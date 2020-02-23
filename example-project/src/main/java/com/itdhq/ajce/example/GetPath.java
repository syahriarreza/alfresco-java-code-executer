package com.itdhq.ajce.example;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itdhq.ajce.Job;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

public class GetPath implements Job {

	public Serializable run(ServiceRegistry services, Repository repository) {
		String nodeRefStr = "workspace://SpacesStore/b9dba3bd-3de3-43e1-8e37-239365dcc588";

		NodeService nodeService = services.getNodeService();
		ContentService contentService = services.getContentService();
		FileFolderService fileFolderService = services.getFileFolderService();
		PermissionService permissionService = services.getPermissionService();
		MutableAuthenticationService authenticationService = services.getAuthenticationService();
		MimetypeService mimetypeService = services.getMimetypeService();

		NodeRef nodeRef = new NodeRef(nodeRefStr);
		System.out.println("USER: " + authenticationService.getCurrentUserName() + " | nodeRef: " + nodeRefStr + " | " + nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString());

		try {
			//Path: Indesso/Folder A/Folder B
			String path = "";
			List<FileInfo> fileInfos = fileFolderService.getNamePath(null, nodeRef);
			for (FileInfo fileInfo : fileInfos) {
				if (fileInfo.isFolder() && !fileInfo.getName().equalsIgnoreCase("company home")) {
					path += ("/" + fileInfo.getName());
				}
			}
			System.out.println("path: " + path);
		} catch (Exception ex) {
			Logger.getLogger(GetPath.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("ERROR:\n" + ex.getMessage());
		}

		System.out.println("\n\n\n");
		return "Success";
	}
}
