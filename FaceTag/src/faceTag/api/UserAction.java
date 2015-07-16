package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user/{userid}")
public class UserAction{
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getUser(@PathParam("userid") String userid, @QueryParam("username") String username, @QueryParam("token") String token){
		return "HELLO THIS IS " + userid + "!";
	}
	//Get a list of a user's friends
	@GET @Path("/friend")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUsersFriends(@PathParam("userid") String userid, @QueryParam("username") String username, @QueryParam("token") String token){
		return "Here is a list of " + userid + "'s friends [f1: 1241, f2: 4121]";
	}
	
	//Add a user as a friend
	@POST @Path("/friend")
	@Consumes("text/plain")
	public void addUserAsFriend(@PathParam("userid") String userid, @QueryParam("username") String username, @QueryParam("token") String token){
		System.out.println(  "added " + userid + " as a friend. Message: " + username + " " + token);
	}
	
	@DELETE @Path("/friend")
	@Consumes("text/plain")
	public void removeUserAsFriend(@PathParam("userid") String userid, @QueryParam("username") String username, @QueryParam("token") String token){
		System.out.println( "removed " + userid + " as a friend. Message: " + username + " " + token);
	}
}