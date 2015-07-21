package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.UserController;

@Path("/user/{userid}")
public class UserAction{
	
	//get a user's info
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("userid") String userid, @QueryParam("userID") String username, @QueryParam("token") String token){
		return UserController.getUser(username, token, userid);
	}
	//Get a list of a user's friends
	@GET @Path("/friend")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersFriends(@PathParam("userid") String userid, @QueryParam("userID") String username, @QueryParam("token") String token){
		return UserController.getUserFriends(username, token, userid);
	}
	
	//Add a user as a friend
	@POST @Path("/friend")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserAsFriend(@PathParam("userid") String userid, MultivaluedMap<String,String> multivaluedMap){
		return UserController.addFriend(multivaluedMap.getFirst("userID"), multivaluedMap.getFirst("token"), userid);
	}
	//Remove a user as a friend
	@DELETE @Path("/friend")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeUserAsFriend(@PathParam("userid") String userid, MultivaluedMap<String,String> multivaluedMap){
		return UserController.deleteFriend(multivaluedMap.getFirst("userID"), multivaluedMap.getFirst("token"), userid);
	}
	//Get a user's images (image data is not sent over)
	@GET @Path("/images")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersImages(@PathParam("userid") String userid, @QueryParam("userID") String username, @QueryParam("token") String token){
		return UserController.getImagesForUser(username, token, userid);
	}
}