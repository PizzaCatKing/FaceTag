package faceTag.controllers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.util.JSON;

import faceTag.entities.Friend;
import faceTag.entities.User;
import faceTag.mongo.FriendCollectionManager;
import faceTag.mongo.ImageCollectionManager;
import faceTag.mongo.UserCollectionManager;

public class UserController {

	public static Response getUser(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}

		User user = UserCollectionManager.getUser(new ObjectId(userID));
		if (user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(user);
		toReturn.put("_id", user.getID().toHexString());

		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	public static Response getUserFriends(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}

		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			return Response.status(Response.Status.NOT_FOUND).entity("{error: user not found}")
					.type(MediaType.APPLICATION_JSON).build();
		}
		BasicDBList results = FriendCollectionManager.getFriendsForUser(new ObjectId(userID));
		for (Object user : results) {
			((BasicDBObject) user).put("_id", ((ObjectId) ((BasicDBObject) user).get("_id")).toHexString());
		}
		return Response.ok(JSON.serialize(results), MediaType.APPLICATION_JSON).build();
	}

	public static Response deleteFriend(String _id, String token, String userID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			return Response.status(Response.Status.NOT_FOUND).entity("{error: user not found}")
					.type(MediaType.APPLICATION_JSON).build();
		}

		Friend deleted = FriendCollectionManager.deleteFriend(new ObjectId(_id), new ObjectId(userID));

		if (deleted == null) {
			// Return error message
			return Response.status(Response.Status.NOT_FOUND).entity("{error: friend not found}")
					.type(MediaType.APPLICATION_JSON).build();
		}

		BasicDBObject toReturn = new BasicDBObject();
		toReturn.putAll(targetUser);
		toReturn.put("_id", targetUser.getID().toHexString());

		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	public static Response addFriend(String _id, String token, String userID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}
		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			return Response.status(Response.Status.NOT_FOUND).entity("{error: user not found}")
					.type(MediaType.APPLICATION_JSON).build();
		}
		try {
			Friend added = FriendCollectionManager.addFriend(new ObjectId(_id), targetUser.getID());
			if (added == null) {
				// Return error message - you can't add this person as a friend
				return Response.status(Response.Status.BAD_REQUEST).entity("{error: friend not added}")
						.type(MediaType.APPLICATION_JSON).build();
			}
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.putAll(targetUser);
			toReturn.put("_id", targetUser.getID().toHexString());

			return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
		}
		// You can't add someone you are already friends with
		catch (MongoWriteException e) {
			// Return error message - you can't add this person as a friend
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{error: You are already friends with this person}").type(MediaType.APPLICATION_JSON)
					.build();
		}

	}

	public static Response getImagesForUser(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if (tokenValidation != null) {
			return tokenValidation;
		}

		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(new ObjectId(userID));
		if (targetUser == null) {
			// Return error message
			return Response.status(Response.Status.NOT_FOUND).entity("{error: user not found}")
					.type(MediaType.APPLICATION_JSON).build();
		}

		Iterable<BasicDBObject> imageIterator = ImageCollectionManager.getImagesForUser(new ObjectId(userID));
		BasicDBList images = new BasicDBList();
		for (BasicDBObject image : imageIterator) {
			BasicDBObject imageToSerialize = new BasicDBObject(image);
			imageToSerialize.put("_id", ((ObjectId) image.get("_id")).toHexString());
			imageToSerialize.put("ownerID", ((ObjectId) image.get("ownerID")).toHexString());
			images.add(imageToSerialize);
		}

		return Response.ok(JSON.serialize(images), MediaType.APPLICATION_JSON).build();
	}
}
