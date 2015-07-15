package faceTag.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class TokenManager {
public static Boolean validateToken(String username, String token){
	if(username == null || token == null){
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
	}
	if(username.equals("") || token.equals("")){
		throw new WebApplicationException(Response.Status.BAD_REQUEST);
	}
	// Check if token exists in system
	return true;
}
}
