package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/logout")
public class LogoutHandler{
	
	@POST
	@Consumes("text/plain")
	public void logout(@QueryParam("username") String username, @QueryParam("token") String token){
		if(username == null || token == null){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		if(username.equals("") || token.equals("")){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		System.out.println("Got: " + username + "/" + token + "!");
	}
}