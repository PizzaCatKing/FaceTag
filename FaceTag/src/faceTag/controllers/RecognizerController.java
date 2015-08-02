package faceTag.controllers;

import java.util.ArrayList;

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
import faceTag.mongo.FriendCollectionManager;
import faceTag.mongo.ImageCollectionManager;
import faceTag.mongo.RectangleCollectionManager;
import faceTag.mongo.UserCollectionManager;
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
		ArrayList<Rectangle> rectArray = new ArrayList<>();
		for(Object obj : rectangles){
			((DBObject) obj).put("_id", new ObjectId((String)((DBObject) obj).get("rectID")));
			((DBObject) obj).put("imageID", new ObjectId((String)((DBObject) obj).get("imageID")));
			if(StringTool.isValid((String) ((DBObject) obj).get("userID"))){
				((DBObject) obj).put("userID", new ObjectId((String)((DBObject) obj).get("userID")));
			}
			else{
				((DBObject) obj).put("userID",null);
			}
			
			
			((DBObject) obj).removeField("rectID");
			Rectangle newRect = new Rectangle();
			newRect.putAll((DBObject) obj);
			rectArray.add(newRect);
		}
		
		rectangles = RectangleCollectionManager.updateRectangles(rectangles);
		

		System.out.println(" -- " + JSON.serialize(rectangles));
		RecognizerInterface.getInstance().updateRecognizer(rectArray.toArray(new Rectangle[rectArray.size()]));
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
		
		Rectangle[] detected = RecognizerInterface.getInstance().detectForRectangles(image.getID().toHexString());
		
		//Get the userIDs of this user's friends
		
		BasicDBList friendList = FriendCollectionManager.getFriendsForUser(new ObjectId(_id));
		friendList.add(UserCollectionManager.getUser(new ObjectId(_id)));

		String[] userIDArray = new String[friendList.size()];
		for (int i=0; i< friendList.size(); i++) {
			userIDArray[i] = ((ObjectId) ((DBObject)friendList.get(i)).get("_id")).toHexString();
			System.out.println("Got " + userIDArray[i]);
		}

		for(Rectangle rect : detected){
			rect.put("imageID", image.getID());
		}
		
		//Recognize the people in the rectangles
		String[] recognized = RecognizerInterface.getInstance().recgonizeRectangles(userIDArray, detected);
		
		
		for(int i =0; i< detected.length; i++){
			System.out.println("Got " + recognized[i]);
			if(!recognized[i].equals("")){
				detected[i].put("userID", new ObjectId(recognized[i]));
			}
			else{
				detected[i].put("userID", null);
			}
			
		}
		
		RectangleCollectionManager.deleteForImage(image.getID());
		Rectangle[] result = null;
		if(detected.length > 0){
			result = RectangleCollectionManager.addRectangles(detected);
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
		System.out.println("Response: " + JSON.serialize(rectangles));
		return Response.ok(JSON.serialize(rectangles), MediaType.APPLICATION_JSON).build();
	}
}
