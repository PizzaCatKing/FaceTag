package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.util.JSON;

import faceTag.entities.Account;
import faceTag.entities.ErrorCode;
import faceTag.entities.Token;
import faceTag.mongo.AccountCollectionManager;
import faceTag.mongo.TokenCollectionManager;

public class AccountController {

	public static Response signUp(String username, String password, String name) {
		if (!(StringTool.isValid(username) && StringTool.isValid(password) && StringTool.isValid(name))){
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		try {
			AccountCollectionManager.addAccount(username, password, name);
		} catch (MongoWriteException e) {
			// If the username is already taken we reject the sign up.
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "That username is already in use.");
			toReturn.put("error", ErrorCode.ERROR_USERNAME_IN_USE);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.put("message", "Success");
		return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
	}

	public static Response login(String username, String password) {
		if (!(StringTool.isValid(username) && StringTool.isValid(password))){
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Account userAccount = AccountCollectionManager.checkPassword(username, password);
		if (userAccount != null) {
			// Login sucessful, send userid and token
			Token token = TokenCollectionManager.addToken(userAccount.getUserID());
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.putAll(token);
			toReturn.remove("_id");
			toReturn.put("userID", token.getUserID().toHexString());
			return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
		}
		BasicDBObject toReturn = new BasicDBObject();
		toReturn.put("message", "Invalid Credentials");
		toReturn.put("error", ErrorCode.ERROR_INVALID_CREDENTIALS);
		
		return Response
				.status(Response.Status.BAD_REQUEST)
				.entity(JSON.serialize(toReturn))
				.type( MediaType.APPLICATION_JSON)
                .build();
	}
	
	public static Response logout(String _id, String token) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(token)&& StringTool.isValidObjectID(_id))){
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.put("message", "Invalid Parameters");
			toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);
			
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(JSON.serialize(toReturn))
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Response tokenValidation = TokenController.validateToken(_id, token);
		if(tokenValidation != null){
			return tokenValidation;
		}
		// If null token was not deleted or it never existed
		TokenCollectionManager.deleteToken(new ObjectId(_id), token);

		return Response.ok("Logout Sucess", MediaType.APPLICATION_JSON).build();
	}
}
