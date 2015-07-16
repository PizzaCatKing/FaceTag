package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.AccountController;

@Path("/logout")
public class LogoutAction{
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(MultivaluedMap<String,String> multivaluedMap){
		return Response.ok(AccountController.logout(multivaluedMap.getFirst("userID"), multivaluedMap.getFirst("token")), MediaType.APPLICATION_JSON).build();
	}
}