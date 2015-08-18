package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.UserController;

@Path("/user")
public class UserAction {
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@QueryParam("userID") String userID,
			@QueryParam("token") String token) {
		return UserController.getAllUsers(userID, token);
	}
	
	// get a user's info
	@GET
	@Path("/{targetID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("targetID") String targetID, @QueryParam("userID") String userID,
			@QueryParam("token") String token) {
		return UserController.getUser(userID, token, targetID);
	}

	// Get a list of a user's friends
	@GET
	@Path("/{targetID}/friend")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersFriends(@PathParam("targetID") String targetID, @QueryParam("userID") String userID,
			@QueryParam("token") String token) {
		return UserController.getUserFriends(userID, token, targetID);
	}

	// Add a user as a friend
	@POST
	@Path("/{targetID}/friend")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserAsFriend(@PathParam("targetID") String targetID, MultivaluedMap<String, String> multivaluedMap) {
		String userID = null;
		String token = null;
		if (multivaluedMap != null) {
			if (multivaluedMap.containsKey("userID")) {
				userID = multivaluedMap.getFirst("userID");
			}

			if (multivaluedMap.containsKey("token")) {
				token = multivaluedMap.getFirst("token");
			}
		}
		return UserController.addFriend(userID, token, targetID);
	}

	// Remove a user as a friend
	@POST
	@Path("/{targetID}/friend/delete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUserAsFriend(@PathParam("targetID") String targetID,
			MultivaluedMap<String, String> multivaluedMap) {
		String userID = null;
		String token = null;
		if (multivaluedMap != null) {
			if (multivaluedMap.containsKey("userID")) {
				userID = multivaluedMap.getFirst("userID");
			}

			if (multivaluedMap.containsKey("token")) {
				token = multivaluedMap.getFirst("token");
			}
		}
		return UserController.deleteFriend(userID, token, targetID);
	}

	// Get a user's images (image data is not sent over)
	@GET
	@Path("/{targetID}/images")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersImages(@PathParam("targetID") String targetID, @QueryParam("userID") String userID,
			@QueryParam("token") String token) {
		return UserController.getImagesForUser(userID, token, targetID);
	}
}