package faceTag.controllers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import faceTag.mongo.TokenCollectionManager;

public class TokenController {
	public static void validateToken(String userID, String token) {
		if (!(StringTool.isValid(userID) && StringTool.isValid(token))) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		// Check if token exists in system
		if (!TokenCollectionManager.compareToken(userID, token)) {
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
	}
}
