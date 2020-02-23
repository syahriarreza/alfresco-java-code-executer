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

public class QRCode implements Job {
	private final Integer QR_CODE_WIDTH = 350;
	private final Integer QR_CODE_HEIGHT = 350;
	private final String QR_CODE_FONT = "Arial Black";
	private final Integer QR_CODE_FONT_SIZE = 17;
	
    public Serializable run(ServiceRegistry services, Repository repository) {
        String nodeRefStr = "workspace://SpacesStore/a5db913c-41de-45d5-a580-5bd745a12144";
        
		NodeService nodeService = services.getNodeService();
		ContentService contentService = services.getContentService();
		FileFolderService fileFolderService = services.getFileFolderService();
        PermissionService permissionService = services.getPermissionService();
        MutableAuthenticationService authenticationService = services.getAuthenticationService();
		MimetypeService mimetypeService = services.getMimetypeService();
        
        System.out.println("USER: "+authenticationService.getCurrentUserName());
        
        NodeRef nodeRef = new NodeRef(nodeRefStr);
		Set<AccessPermission> allSetPermissions;

		try {
			allSetPermissions = permissionService.getAllSetPermissions(nodeRef);
			System.out.println("allSetPermissions: " + allSetPermissions);
			
			for (AccessPermission permission : allSetPermissions) {
				System.out.println("Auth: " + permission.getAuthority() + " | Perm: " + permission.getPermission());
			}
		} catch (Exception e) {
			return "Access Denied";
		}
		
		
		//--Generate QR Code
		byte[] qrCodeByte;
		try {
			qrCodeByte = getQRCodeImage("http://localhost:8080/alfresco/d/d/workspace/SpacesStore/88027296-76d7-463a-a531-8745f12f8d2a/actions-article-2ed.pdf", "Manufacturing", QR_CODE_WIDTH, QR_CODE_HEIGHT);
		} catch (WriterException ex) {
			throw new AlfrescoRuntimeException("QRCodeGenerator whilst running getQRCodeImage: " + ex.getMessage());
		} catch (IOException ex) {
			throw new AlfrescoRuntimeException("QRCodeGenerator whilst running getQRCodeImage: " + ex.getMessage());
		}

		//--Create QR Code nodeRef
		String qrCodeName = "TEST QR CODE.png";
		NodeRef companyHome = repository.getCompanyHome();
		NodeRef tempFolderNode = fileFolderService.searchSimple(companyHome, "temp");
		NodeRef qrCodeNode = fileFolderService.searchSimple(tempFolderNode, qrCodeName);
		if (qrCodeNode == null && !nodeService.exists(qrCodeNode)) {
			//fileFolderService.delete(qrCodeNode);
			qrCodeNode = fileFolderService.create(tempFolderNode, qrCodeName, ContentModel.TYPE_CONTENT).getNodeRef();
		}
        
		//--Fill QR Code node with QR Code content
		ContentWriter writer = contentService.getWriter(qrCodeNode, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(mimetypeService.guessMimetype(qrCodeName));
		writer.putContent(new ByteArrayInputStream(qrCodeByte));
		
		
        return "Success";
    }
	
	private byte[] getQRCodeImage(String content, String title, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		byte[] pngData = pngOutputStream.toByteArray();

		ByteArrayInputStream bais = new ByteArrayInputStream(pngData);
		BufferedImage bufferedImage = ImageIO.read(bais);

		if (title != "") {
			drawCenteredString(
				bufferedImage.getGraphics(),
				title,
				new Rectangle(width, height),
				new Font(QR_CODE_FONT, Font.PLAIN, QR_CODE_FONT_SIZE)
			);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		return imageInByte;
	}

	/**
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 */
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2; // Determine the X coordinate for the text
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent(); // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(text, x, QR_CODE_HEIGHT-10);
	}

}
