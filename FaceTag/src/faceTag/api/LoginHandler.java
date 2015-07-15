package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
public class LoginHandler{
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getUser(@QueryParam("username") String username, @QueryParam("password") String password){
		if(username == null || password == null){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		if(username.equals("") || password.equals("")){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		return "HELLO " + username + " / " + password + " here is your token [TOKEN]";
	}
}