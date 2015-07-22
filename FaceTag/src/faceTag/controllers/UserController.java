package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.util.JSON;

import faceTag.entities.ErrorCode;
import faceTag.entities.Friend;
import faceTag.entities.User;
import faceTag.mongo.FriendCollectionManager;
import faceTag.mongo.ImageCollectionManager;
import faceTag.mongo.UserCollectionManager;

public class UserController {

	public static Response getUser(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token)&& StringTool.isValidObjectID(_id)&& StringTool.isValidObjectID(userID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}

		User user = UserCollectionManager.getUser(new ObjectId(userID));
		if (user == null) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);
			
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(user);
		toReturn.remove("_id");
		toReturn.put("userID", user.getID().toHexString());

		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	public static Response getUserFriends(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token)&& StringTool.isValidObjectID(_id)&& StringTool.isValidObjectID(userID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}

		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);
			
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		BasicDBList userList = FriendCollectionManager.getFriendsForUser(new ObjectId(userID));
		BasicDBList results = new BasicDBList();
		for (Object user : userList) {
			BasicDBObject serializedUser = new BasicDBObject();
			serializedUser.put("userID", ((ObjectId) ((BasicDBObject) user).get("_id")).toHexString());
			serializedUser.put("name", (String) ((BasicDBObject) user).get("name"));
			
			results.add(serializedUser);
		}
		return Response.ok(JSON.serialize(results), MediaType.APPLICATION_JSON).build();
	}

	public static Response deleteFriend(String _id, String token, String userID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token)&& StringTool.isValidObjectID(_id)&& StringTool.isValidObjectID(userID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);
			
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}

		Friend deleted = FriendCollectionManager.deleteFriend(new ObjectId(_id), new ObjectId(userID));

		if (deleted == null) {
			// Return error message
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "You are not friends with this user.");
			toReturn.put("error", ErrorCode.ERROR_NOT_FRIENDS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}

		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(targetUser);
		toReturn.remove("_id");
		toReturn.put("userID", targetUser.getID().toHexString());

		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	public static Response addFriend(String _id, String token, String userID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token)&& StringTool.isValidObjectID(_id)&& StringTool.isValidObjectID(userID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);
			
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		try {
			Friend added = FriendCollectionManager.addFriend(new ObjectId(_id), targetUser.getID());
			if (added == null) {
				// Return error message - you can't add this person as a friend
				BasicDBObject toReturn = new BasicDBObject();
				toReturn.put("message", "You can't add this user as a friend.");
				toReturn.put("error", ErrorCode.ERROR_CANT_BE_FRIENDS);
				
				return Response
						.status(Response.Status.BAD_REQUEST)
						.entity(JSON.serialize(toReturn))
						.type( MediaType.APPLICATION_JSON)
		                .build();
			}
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.putAll(targetUser);
			toReturn.remove("_id");
			toReturn.put("userID", targetUser.getID().toHexString());

			return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
		}
		// You can't add someone you are already friends with
		catch (MongoWriteException e) {
			// Return error message - you can't add this person as a friend
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "You are already friends with this person.");
			toReturn.put("error", ErrorCode.ERROR_ALREADY_FIRENDS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}

	}

	public static Response getImagesForUser(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token)&& StringTool.isValidObjectID(_id)&& StringTool.isValidObjectID(userID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}

		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "The requested resource can't be found");
			toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);
			
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}

		Iterable<BasicDBObject> imageIterator = ImageCollectionManager.getImagesForUser(new ObjectId(userID));
		BasicDBList images = new BasicDBList();
		for (BasicDBObject image : imageIterator) {
			BasicDBObject imageToSerialize = new BasicDBObject(image);
			imageToSerialize.remove("_id");
			imageToSerialize.put("imageID", ((ObjectId) image.get("_id")).toHexString());
			imageToSerialize.put("ownerID", ((ObjectId) image.get("ownerID")).toHexString());
			images.add(imageToSerialize);
		}

		return Response.ok(JSON.serialize(images), MediaType.APPLICATION_JSON).build();
	}
}
