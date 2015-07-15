package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/signup")
public class SignUpHandler{
	
	@POST
	@Consumes("text/plain")
	public void signup(@QueryParam("username") String username, @QueryParam("password") String password){
		if(username == null || password == null){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		if(username.equals("") || password.equals("")){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		System.out.println("Got: " + username + " " + password + "!");
	}
}