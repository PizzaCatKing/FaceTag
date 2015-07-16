package faceTag.controllers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBList;

import faceTag.entities.Friend;
import faceTag.entities.Image;
import faceTag.entities.User;
import faceTag.mongo.FriendCollectionManager;
import faceTag.mongo.ImageCollectionManager;
import faceTag.mongo.UserCollectionManager;

public class UserController {

	public static String getUser(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);

		User user = UserCollectionManager.getUser(userID);
		if (user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return user.toString();
	}

	public static String getUserFriends(String _id, String token, String userID) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);

		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(userID);
		if (targetUser == null) {
			// Return error message
			return "";
		}

		Iterable<User> friendsIterator = FriendCollectionManager.getFriendsForUser(userID);
		BasicDBList users = new BasicDBList();
		for (User friend : friendsIterator) {
			users.add(friend);
		}
		return users.toString();
	}

	public static String deleteFriend(String _id, String token, String userID) {

		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);
		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(userID);
		if (targetUser == null) {
			// Return error message
			return "";
		}

		Friend deleted = FriendCollectionManager.deleteFriend(_id, userID);
		if (deleted == null) {
			// Return error message
			return "";
		}
		return targetUser.toString();
	}
	
	public static String getImagesForUser(String _id, String token, String userID){
		if (!(StringTool.isValid(_id) && StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);

		// Get user to be sure they exist
		User targetUser = UserCollectionManager.getUser(userID);
		if (targetUser == null) {
			// Return error message
			return "";
		}

		Iterable<Image> imageIterator = ImageCollectionManager.getImagesForUser(userID);
		BasicDBList images = new BasicDBList();
		for (Image image : imageIterator) {
			images.add(image);
		}
		return images.toString();
	}
}
