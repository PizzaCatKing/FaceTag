package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import faceTag.entities.ErrorCode;
import faceTag.entities.Image;
import faceTag.entities.Rectangle;
import faceTag.mongo.ImageCollectionManager;
import faceTag.mongo.RectangleCollectionManager;
import faceTag.recognizer.RecognizerInterface;

public class RecognizerController {

	// Get rectangles for image
	public static Response getRectangles(String _id, String token, String imageID) {
		
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
		
		Iterable<DBObject> rectangleIterator = RectangleCollectionManager.getRectanglesForImage(image.getID());

		BasicDBList rectangles = new BasicDBList();
		
		for (DBObject rect : rectangleIterator) {
			BasicDBObject rectangleToSerialize = new BasicDBObject(rect.toMap());
			rectangleToSerialize.remove("_id");
			rectangleToSerialize.put("rectID", ((ObjectId)rect.get("_id")).toHexString());
			rectangleToSerialize.put("imageID",((ObjectId)rect.get("imageID")).toHexString());
			if(rect.get("userID") != null){
				rectangleToSerialize.put("userID", ((ObjectId)rect.get("userID")).toHexString());
			}
			else{
				rectangleToSerialize.put("userID",null);
			}
			rectangles.add(rectangleToSerialize);
		}
		return Response.ok(JSON.serialize(rectangles), MediaType.APPLICATION_JSON).build();
	}
	
public static Response setRectangles(String _id, String token, String imageID, String rectString) {
		
		if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID) && StringTool.isValid(rectString)
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
		
		//change the JSON rectangles to an array of rectangles
		BasicDBList rectangles = (BasicDBList) JSON.parse(rectString);
		for(Object obj : rectangles){
			((DBObject) obj).put("_id", new ObjectId((String)((DBObject) obj).get("rectID")));
			((DBObject) obj).put("imageID", new ObjectId((String)((DBObject) obj).get("imageID")));
			if(((DBObject) obj).get("userID") != null){
				((DBObject) obj).put("userID", new ObjectId((String)((DBObject) obj).get("userID")));
			}
			else{
				((DBObject) obj).put("userID",null);
			}
			
			
			((DBObject) obj).removeField("rectID");
		}
		rectangles = RectangleCollectionManager.updateRectangles(rectangles);
		
		for(Object obj : rectangles){
			((DBObject) obj).put("rectID",((ObjectId)((DBObject) obj).get("_id")).toHexString());
			((DBObject) obj).put("imageID", ((ObjectId)((DBObject) obj).get("imageID")).toHexString());
			
			if(((DBObject) obj).get("userID") != null){
				((DBObject) obj).put("userID", ((ObjectId)((DBObject) obj).get("userID")).toHexString());
			}
			else{
				((DBObject) obj).put("userID",null);
			}
			
			((DBObject) obj).removeField("_id");
		}
		
		return Response.ok(JSON.serialize(rectangles), MediaType.APPLICATION_JSON).build();
	}
		
	public static Response getNewRectangles(String _id, String token, String imageID) {
		
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
		
		if(!new ObjectId(_id).equals(image.getOwnerID())){
			// This user does not have permission to edit this image
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "You do not have permission to edit this image");
			toReturn.put("error", ErrorCode.ERROR_INVALID_CREDENTIALS);

			return Response.status(Response.Status.NOT_FOUND).entity(JSON.serialize(toReturn))
					.type(MediaType.APPLICATION_JSON).build();
		}
		
		Rectangle[] recognized = RecognizerInterface.getInstance().detectForRectangles(faceTag.entities.Globals.FILES_ROOT, image.getID().toHexString());
		for(Rectangle rect : recognized){
			rect.put("imageID", image.getID());
		}
		RectangleCollectionManager.deleteForImage(image.getID());
		Rectangle[] result = null;
		if(recognized.length > 0){
			result = RectangleCollectionManager.addRectangles(recognized);
		}
		BasicDBList rectangles = new BasicDBList();
		for (Rectangle rect : result) {
			BasicDBObject rectangleToSerialize = new BasicDBObject(rect.toMap());
			rectangleToSerialize.remove("_id");
			rectangleToSerialize.put("rectID", ((ObjectId)rect.get("_id")).toHexString());
			rectangleToSerialize.put("imageID",((ObjectId)rect.get("imageID")).toHexString());
			if(rect.get("userID") != null){
				rectangleToSerialize.put("userID", ((ObjectId)rect.get("userID")).toHexString());
			}
			else{
				rectangleToSerialize.put("userID",null);
			}
			rectangles.add(rectangleToSerialize);
		}
		return Response.ok(JSON.serialize(rectangles), MediaType.APPLICATION_JSON).build();
	}
}
