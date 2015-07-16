package faceTag.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

import faceTag.entities.Token;


public class TokenCollectionManager {
	private static MongoCollection<Token> getTokenCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoCollection<Token> coll = MongoDBSingleton.getDataBase().getCollection("Token").withDocumentClass(Token.class);
		return coll;
	}
	
	public static boolean compareToken(String userID, String token) {
		BasicDBObject query = new BasicDBObject("userID", userID).append("token", token);
		MongoCollection<Token> coll = getTokenCollection();
		return coll.count(query) == 1;
	}
	
	public static Token deleteToken(String userID, String token) {
		BasicDBObject query = new BasicDBObject("userID", userID).append("token", token);
		MongoCollection<Token> coll = getTokenCollection();
		return coll.findOneAndDelete(query);
	}
	
	public static DeleteResult deleteAllTokensForUser(String userID) {
		BasicDBObject query = new BasicDBObject("userID", userID);
		MongoCollection<Token> coll = getTokenCollection();
		return coll.deleteMany(query);
	}

	public static Token addToken(String userID) {
		String token = TokenGeneratorSingleton.getInstance().generateToken(); // Generate a new token
		Token newToken = new Token(userID, token);
		MongoCollection<Token> coll = getTokenCollection();
		coll.insertOne(newToken);
		return newToken;
	}
}

