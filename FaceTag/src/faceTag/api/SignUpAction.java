package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.AccountController;

@Path("/signup")
public class SignUpAction{
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signup(MultivaluedMap<String,String> multivaluedMap){
		return AccountController.signUp(multivaluedMap.getFirst("username"), multivaluedMap.getFirst("password"), multivaluedMap.getFirst("name"));
	}
}