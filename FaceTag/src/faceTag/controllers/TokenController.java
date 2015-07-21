package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import faceTag.mongo.TokenCollectionManager;

public class TokenController {
	public static Response validateToken(String userID, String token) {
		if (!(StringTool.isValid(userID) && StringTool.isValid(token))) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		// Check if token exists in system
		if (!TokenCollectionManager.compareToken(new ObjectId(userID), token)) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid token}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		return null;
	}
}
