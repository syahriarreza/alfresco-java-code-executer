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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;

import java.io.Serializable;
import java.util.Set;
import javax.imageio.ImageIO;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;

public class SimpleJava implements Job {
    public Serializable run(ServiceRegistry services, Repository repository) {
        String nodeRefStr = "workspace://SpacesStore/0bfb888e-0352-4c84-9a57-6fc6caa7f1b6";
        
		NodeService nodeService = services.getNodeService();
		ContentService contentService = services.getContentService();
		FileFolderService fileFolderService = services.getFileFolderService();
        PermissionService permissionService = services.getPermissionService();
        MutableAuthenticationService authenticationService = services.getAuthenticationService();
		MimetypeService mimetypeService = services.getMimetypeService();
        
		NodeRef nodeRef = new NodeRef(nodeRefStr);
        System.out.println("USER: "+authenticationService.getCurrentUserName()+" | nodeRef: "+nodeRefStr+" | "+nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString());
        
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		for (QName aspect : aspects) {
			System.out.println(aspect.toString());
		}
		
		QName CUSTOM_ASPECT_QNAME = QName.createQName("custdown-indesso.co.id", "showCustomDownloadWatermark");
		System.out.println("Has "+CUSTOM_ASPECT_QNAME.toString()+"? "+nodeService.hasAspect(nodeRef, CUSTOM_ASPECT_QNAME));
		
		Serializable prop = nodeService.getProperty(nodeRef, QName.createQName("custdown-indesso.co.id", "imagePath"));
		if (prop == null) {
			System.out.println("prop is null !!!");
		} else {
			System.out.println("prop: "+prop.toString());
		}
		
		System.out.println("\n\n\n");
        return "Success";
    }
}
