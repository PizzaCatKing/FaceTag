package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import faceTag.controllers.AccountController;

@Path("/login")
public class LoginAction{
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("username") String username, @QueryParam("password") String password){
		return AccountController.login(username, password);
	}
}