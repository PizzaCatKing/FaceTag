package faceTag.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import faceTag.entities.Image;
import faceTag.mongo.ImageCollectionManager;

public class ImageController {

	final static String IMAGE_ROOT = "./images/";
	// Add image
	public static Response uploadImage(String _id, String token, String title, String base64Image) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(title)
				&& StringTool.isValid(base64Image))) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if(tokenValidation != null){
			return tokenValidation;
		}
		Image newImage = ImageCollectionManager.addImage(new ObjectId(_id), title);

		// Decode string to by byte array
		byte[] imageDataBytes = Base64.decodeBase64(base64Image.getBytes());
		// Save byte array into file
		FileOutputStream imageOutFile;
		try {
			File file = new File(IMAGE_ROOT + newImage.getID().toHexString());
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
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(newImage);
		toReturn.put("_id", newImage.getID().toHexString());
		toReturn.put("ownerID", newImage.getOwnerID().toHexString());
		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	// Get image
	public static Response getImage(String _id, String token, String imageID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID))) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if(tokenValidation != null){
			return tokenValidation;
		}
		Image image = ImageCollectionManager.getImage(new ObjectId(imageID));
		if (image == null) {
			// Image doesn't exist
						return Response
								.status(Response.Status.NOT_FOUND)
								.entity("{error: image not found}")
								.type( MediaType.APPLICATION_JSON)
				                .build();
		}
		String imageDataString = null;
		// Encode byte array to string
		try {
			File file = new File(IMAGE_ROOT + image.getID().toHexString());
			FileInputStream imageInFile = new FileInputStream(IMAGE_ROOT + image.getID().toHexString());
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
		
		BasicDBObject result = new BasicDBObject();
		result.putAll(image);
		result.put("_id", image.getID().toHexString());
		result.put("ownerID", image.getOwnerID().toHexString());
		result.put("base64Image", imageDataString);
		return Response.ok(JSON.serialize(result), MediaType.APPLICATION_JSON).build();
	}
	// deleteImage
	public static Response deleteImage(String _id, String token, String imageID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID))) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if(tokenValidation != null){
			return tokenValidation;
		}
		Image image = ImageCollectionManager.getImage(new ObjectId(imageID));
		if (image == null) {
			// Image doesn't exist
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity("{error: image not found}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}

		File file = new File(IMAGE_ROOT + image.getID().toHexString());
		if(file.exists()){
			file.delete();
		}
		
		image = ImageCollectionManager.deleteImage(new ObjectId(imageID));
		
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(image);
		toReturn.put("_id", image.getID().toHexString());
		toReturn.put("ownerID", image.getOwnerID().toHexString());
		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}
}
