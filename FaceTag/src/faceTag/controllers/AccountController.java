package faceTag.controllers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.mongodb.MongoWriteException;

import faceTag.entities.Account;
import faceTag.entities.Token;
import faceTag.mongo.AccountCollectionManager;
import faceTag.mongo.TokenCollectionManager;

public class AccountController {

	public static boolean signUp(String username, String password, String name) {
		if (!(StringTool.isValid(username) && StringTool.isValid(password) && StringTool.isValid(name))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		try {
			AccountCollectionManager.addAccount(username, password, name);
		} catch (MongoWriteException e) {
			// If the username is already taken we reject the sign up.
			return false;
		}
		return true;
	}

	public static String login(String username, String password) {
		if (!(StringTool.isValid(username) && StringTool.isValid(password))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		Account userAccount = AccountCollectionManager.checkPassword(username, password);
		if (userAccount != null) {
			// Login sucessful, send userid and token
			Token token = TokenCollectionManager.addToken((String) userAccount.get("_id"));
			return token.toString();
		}
		throw new WebApplicationException(Response.Status.FORBIDDEN);
	}
	
	public static String logout(String _id, String token) {
		if (!(StringTool.isValid(_id) && StringTool.isValid(token))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		TokenController.validateToken(_id, token);
		// If null logout failed, token was not deleted or it never existed
		Token deletedToken = TokenCollectionManager.deleteToken(_id, token);
		if(token == null){ 
			throw new WebApplicationException(Response.Status.GONE);
		}
		return deletedToken.toString();
	}
}
