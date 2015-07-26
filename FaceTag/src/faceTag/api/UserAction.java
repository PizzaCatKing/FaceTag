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
	public Response getAllUsers(@QueryParam("userID") String username,
			@QueryParam("token") String token) {
		return UserController.getAllUsers(username, token);
	}
	
	// get a user's info
	@GET
	@Path("/{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("userid") String userid, @QueryParam("userID") String username,
			@QueryParam("token") String token) {
		return UserController.getUser(username, token, userid);
	}

	// Get a list of a user's friends
	@GET
	@Path("/{userid}/friend")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersFriends(@PathParam("userid") String userid, @QueryParam("userID") String username,
			@QueryParam("token") String token) {
		return UserController.getUserFriends(username, token, userid);
	}

	// Add a user as a friend
	@POST
	@Path("/{userid}/friend")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserAsFriend(@PathParam("userid") String userid, MultivaluedMap<String, String> multivaluedMap) {
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
		return UserController.addFriend(userID, token, userid);
	}

	// Remove a user as a friend
	@POST
	@Path("/{userid}/friend/delete")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUserAsFriend(@PathParam("userid") String userid,
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
		return UserController.deleteFriend(userID, token, userid);
	}

	// Get a user's images (image data is not sent over)
	@GET
	@Path("/{userid}/images")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersImages(@PathParam("userid") String userid, @QueryParam("userID") String username,
			@QueryParam("token") String token) {
		return UserController.getImagesForUser(username, token, userid);
	}
}