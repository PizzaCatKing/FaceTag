package faceTag.controllers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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

import faceTag.entities.ErrorCode;
import faceTag.entities.Image;
import faceTag.mongo.ImageCollectionManager;

public class ImageController {

	final static String IMAGE_ROOT =  "C:\\Users\\Chris_2\\FaceTagData\\images\\";

	// Add image
	public static Response uploadImage(String _id, String token, String title, String base64Image) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(title)
				&& StringTool.isValid(base64Image) && StringTool.isValidObjectID(_id))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);

			return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		Image newImage = ImageCollectionManager.addImage(new ObjectId(_id), title);

		// Decode string to by byte array
		byte[] imageDataBytes = Base64.decodeBase64(base64Image.getBytes());
		try {

			DataInputStream ins = new DataInputStream(new ByteArrayInputStream(imageDataBytes));
			// Check if string is jpeg

			if (ins.readInt() != 0xffd8ffe0) {
				BasicDBObject toReturn = new BasicDBObject();
				toReturn.put("message", "Invalid image format.");
				toReturn.put("error", ErrorCode.ERROR_BAD_IMAGE_FORMAT);

				return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
						.type(MediaType.APPLICATION_JSON).build();

			}
			ins.close();
		} catch (IOException e1) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid image format.");
			toReturn.put("error", ErrorCode.ERROR_BAD_IMAGE_FORMAT);

			return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();

		}

		// Save byte array into file
		FileOutputStream imageOutFile;
		try {
			File file = new File(IMAGE_ROOT + newImage.getID().toHexString());
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			}
			System.out.println(file.getAbsolutePath());
			imageOutFile = new FileOutputStream(file);

			imageOutFile.write(imageDataBytes);

			imageOutFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			ImageCollectionManager.deleteImage(newImage.getID());
			
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Error saving to server!");
			toReturn.put("error", ErrorCode.ERROR_BAD_IMAGE_FORMAT);

			return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
			
		}
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(newImage);
		toReturn.remove("_id");
		toReturn.put("imageID", newImage.getID().toHexString());
		toReturn.put("ownerID", newImage.getOwnerID().toHexString());
		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	// Get image
	public static Response getImage(String _id, String token, String imageID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID)
				&& StringTool.isValidObjectID(_id) && StringTool.isValidObjectID(imageID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);

			return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		Image image = ImageCollectionManager.getImage(new ObjectId(imageID));
		if (image == null) {
			// Image doesn't exist
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);

			return Response.status(Response.Status.NOT_FOUND).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
		}
		String imageDataString = null;
		// Encode byte array to string
		try {
			File file = new File(IMAGE_ROOT + image.getID().toHexString());
			FileInputStream imageInFile = new FileInputStream(IMAGE_ROOT + image.getID().toHexString());
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);

			// Convert byte array to base64 string
			imageDataString = Base64.encodeBase64URLSafeString(imageData);
			imageInFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BasicDBObject result = new BasicDBObject();
		result.putAll(image);
		result.remove("_id");
		result.put("imageID", image.getID().toHexString());
		result.put("ownerID", image.getOwnerID().toHexString());
		result.put("base64Image", imageDataString);
		return Response.ok(JSON.serialize(result), MediaType.APPLICATION_JSON).build();
	}

	// deleteImage
	public static Response deleteImage(String _id, String token, String imageID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID)
				&& StringTool.isValidObjectID(_id) && StringTool.isValidObjectID(imageID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);

			return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		Image image = ImageCollectionManager.getImage(new ObjectId(imageID));
		if (image == null) {
			// Image doesn't exist
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);

			return Response.status(Response.Status.NOT_FOUND).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
		}

		File file = new File(IMAGE_ROOT + image.getID().toHexString());
		if (file.exists()) {
			file.delete();
		}

		boolean deleted = ImageCollectionManager.deleteImage(new ObjectId(imageID));

		BasicDBObject toReturn = new BasicDBObject();
		toReturn.put("deleted", deleted);
		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}
}
