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

import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;

public class GetDocumentList implements Job {
	private String publicUsername = "public";
	private String publicPassword = "public";
	private String hostname = "http://localhost:8080";

	public Serializable run(ServiceRegistry services, Repository repository) {
		//Parameterized
		String nodeRefStr = "workspace://SpacesStore/f47954ee-395b-424a-9c67-45f13a8570b2";
		
		NodeService nodeService = services.getNodeService();
		ContentService contentService = services.getContentService();
		FileFolderService fileFolderService = services.getFileFolderService();
		PermissionService permissionService = services.getPermissionService();
		MutableAuthenticationService authenticationService = services.getAuthenticationService();
		MimetypeService mimetypeService = services.getMimetypeService();
		
		try {
			NodeRef nodeRef = new NodeRef(nodeRefStr);
			System.out.println(
					"USER: " + authenticationService.getCurrentUserName() 
					+ " | nodeRef: " + nodeRef.toString()
					+ " | nodeName: " + nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString()
			);
			
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef);
			for (ChildAssociationRef childAssoc : childAssocs) {
				NodeRef childRef = childAssoc.getChildRef();
				String childName = nodeService.getProperty(childRef, ContentModel.PROP_NAME).toString();
				FileInfo childFileInfo = fileFolderService.getFileInfo(childRef);
				System.out.println("DOC> "+childName+" | Folder? "+childFileInfo.isFolder());
			}
		
			String ticket = getTicket(publicUsername, publicPassword);
			System.out.println("Ticket: "+ticket);
			
		} catch (Exception ex) {
			Logger.getLogger(GetDocumentList.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("ERROR:\n" + ex.getMessage());
		}

		System.out.println("\n\n\n");
		return "Success";
	}
	
	public String getTicket(String username, String password) throws MalformedURLException, ProtocolException, IOException, JSONException {
		String ticket = "";
		URL urlForGetRequest = new URL(hostname+"/alfresco/s/api/login?u="+publicUsername+"&pw="+publicPassword);
		HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
		conection.setRequestMethod("GET");
		int responseCode = conection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(conection.getInputStream()));
			String jsonStr = org.apache.commons.io.IOUtils.toString(buffReader);
			JSONObject json = new JSONObject(jsonStr);
			JSONObject data = json.getJSONObject("data");
			ticket = data.getString("ticket");
			buffReader.close();
		} else {
			System.out.println("ERROR Get Ticket for user: \""+username+"\" | ResponseCode: "+responseCode);
		}
		
		return ticket;
	}
}
