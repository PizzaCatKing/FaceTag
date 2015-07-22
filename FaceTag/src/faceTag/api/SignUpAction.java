package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.AccountController;

@Path("/signup")
public class SignUpAction {

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signup(MultivaluedMap<String, String> multivaluedMap) {
		String username = null;
		String password = null;
		String name = null;
		if (multivaluedMap != null) {
			if (multivaluedMap.containsKey("username")) {
				username = multivaluedMap.getFirst("username");
			}

			if (multivaluedMap.containsKey("password")) {
				password = multivaluedMap.getFirst("password");
			}

			if (multivaluedMap.containsKey("name")) {
				name = multivaluedMap.getFirst("name");
			}
		}
		return AccountController.signUp(username, password, name);
	}
}