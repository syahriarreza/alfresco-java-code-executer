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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class CallURL implements Job {

	public Serializable run(ServiceRegistry services, Repository repository) {
		String nodeRefStr = "workspace://SpacesStore/b9dba3bd-3de3-43e1-8e37-239365dcc588";

		NodeService nodeService = services.getNodeService();
		ContentService contentService = services.getContentService();
		FileFolderService fileFolderService = services.getFileFolderService();
		PermissionService permissionService = services.getPermissionService();
		MutableAuthenticationService authenticationService = services.getAuthenticationService();
		MimetypeService mimetypeService = services.getMimetypeService();

		try {
			URL urlForGetRequest = new URL("http://localhost:8080/alfresco/s/api/login?u=public&pw=public");
			HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
			conection.setRequestMethod("GET");
			conection.setRequestProperty("u", "admin");
			conection.setRequestProperty("pw", "admin");
			int responseCode = conection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				//--Parse JSON String
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(conection.getInputStream()));
				String jsonStr = org.apache.commons.io.IOUtils.toString(buffReader);
				JSONObject json = new JSONObject(jsonStr);
				JSONObject data = json.getJSONObject("data");
				String ticket = data.getString("ticket");
				System.out.println("TICKET: "+ticket);
				buffReader.close();
			} else {
				System.out.println("GET NOT WORKED");
			}
		} catch (IOException ex) {
			Logger.getLogger(CallURL.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.toString());
		} catch (JSONException ex) {
			Logger.getLogger(CallURL.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex.toString());
		}

		System.out.println("\n\n\n");
		return "Success";
	}
}
