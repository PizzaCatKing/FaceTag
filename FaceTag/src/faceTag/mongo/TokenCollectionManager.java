package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import faceTag.entities.Token;

public class TokenCollectionManager {
	private static DBCollection getTokenCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DBCollection coll = MongoDBSingleton.getDataBase().getCollection("Token");
		return coll;
	}

	public static boolean compareToken(ObjectId userID, String token) {
		BasicDBObject query = new BasicDBObject("userID", userID).append("token", token);
		DBCollection coll = getTokenCollection();
		return coll.count(query) == 1;
	}

	public static boolean deleteToken(ObjectId userID, String token) {
		BasicDBObject query = new BasicDBObject("userID", userID).append("token", token);
		DBCollection coll = getTokenCollection();
		// If at least one object was deleted we were successful
		return coll.remove(query).getN() > 0;

	}

	public static boolean deleteAllTokensForUser(ObjectId userID) {
		BasicDBObject query = new BasicDBObject("userID", userID);
		DBCollection coll = getTokenCollection();
		// If at least one object was deleted we were successful
		return coll.remove(query).getN() > 0;
	}

	public static Token addToken(ObjectId userID) {
		//Generate a new token
		String token = TokenGeneratorSingleton.getInstance().generateToken();
		Token newToken = new Token(userID, token);
		DBCollection coll = getTokenCollection();
		coll.insert(newToken);
		return newToken;
	}
}
