package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import faceTag.entities.ErrorCode;
import faceTag.mongo.TokenCollectionManager;

public class TokenController {
	public static Response validateToken(String userID, String token) {
		if (!(StringTool.isValid(userID) && StringTool.isValid(token)&& StringTool.isValidObjectID(userID))) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		// Check if token exists in system
		if (!TokenCollectionManager.compareToken(new ObjectId(userID), token)) {
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Credentials");
			toReturn.put("error", ErrorCode.ERROR_BAD_TOKEN);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		return null;
	}
}
