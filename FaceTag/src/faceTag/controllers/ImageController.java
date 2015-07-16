package faceTag.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.tomcat.util.codec.binary.Base64;

import faceTag.entities.Image;
import faceTag.mongo.ImageCollectionManager;

public class ImageController {

	final static String IMAGE_ROOT = "./images/";

	// Add image
	public static String uploadImage(String _id, String token, String title, String base64Image) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(title)
				&& StringTool.isValid(base64Image))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);
		Image newImage = ImageCollectionManager.addImage(_id, title);

		// Decode string to by byte array
		byte[] imageDataBytes = Base64.decodeBase64(base64Image);

		// Save byte array into file
		FileOutputStream imageOutFile;
		try {
			File file = new File(IMAGE_ROOT + newImage.get("_id"));
			if (!file.exists()) {
				file.createNewFile();
			}
			imageOutFile = new FileOutputStream(file);

			imageOutFile.write(imageDataBytes);

			imageOutFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return newImage.toString();
	}

	// Get image
	public static String getImage(String _id, String token, String imageID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);
		Image image = ImageCollectionManager.getImage(imageID);
		if (image == null) {
			return "";
		}
		String imageDataString = null;
		// Encode byte array to string
		try {
			File file = new File(IMAGE_ROOT + image.get("_id"));
			FileInputStream imageInFile = new FileInputStream(IMAGE_ROOT + image.get("_id"));
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			
			//Convert byte array to base64 string
			imageDataString = Base64.encodeBase64URLSafeString(imageData);
			imageInFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		image.put("image", imageDataString);
		return image.toString();
	}
	// deleteImage
	public static String deleteImage(String _id, String token, String imageID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);
		Image image = ImageCollectionManager.getImage(imageID);
		if (image == null) {
			return ""; // Image doesn't exist
		}

		File file = new File(IMAGE_ROOT + image.get("_id"));
		if(file.exists()){
			file.delete();
		}
		
		image = ImageCollectionManager.deleteImage(imageID);
		
		return image.toString();
	}
}
