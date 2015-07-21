package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.util.JSON;

import faceTag.entities.Account;
import faceTag.entities.Token;
import faceTag.mongo.AccountCollectionManager;
import faceTag.mongo.TokenCollectionManager;

public class AccountController {

	public static Response signUp(String username, String password, String name) {
		if (!(StringTool.isValid(username) && StringTool.isValid(password) && StringTool.isValid(name))){
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		try {
			AccountCollectionManager.addAccount(username, password, name);
		} catch (MongoWriteException e) {
			// If the username is already taken we reject the sign up.
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: username in use}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		return Response.ok("Signup Sucess", MediaType.APPLICATION_JSON).build();
	}

	public static Response login(String username, String password) {
		if (!(StringTool.isValid(username) && StringTool.isValid(password))){
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
					.type( MediaType.APPLICATION_JSON)
	                .build();
		}
		Account userAccount = AccountCollectionManager.checkPassword(username, password);
		if (userAccount != null) {
			// Login sucessful, send userid and token
			Token token = TokenCollectionManager.addToken(userAccount.getUserID());
			BasicDBObject toReturn = new BasicDBObject();
			toReturn.putAll(token);
			toReturn.put("userID", token.getUserID().toHexString());
			return Response.ok(JSON.serialize(toReturn), MediaType.APPLICATION_JSON).build();
		}
		return Response
				.status(Response.Status.BAD_REQUEST)
				.entity("{error: invalid credentials}")
				.type( MediaType.APPLICATION_JSON)
                .build();
	}
	
	public static Response logout(String _id, String token) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(token))){
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{error: invlalid parameters}")
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
