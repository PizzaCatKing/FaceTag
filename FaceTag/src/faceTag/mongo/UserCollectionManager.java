package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import faceTag.entities.User;

public class UserCollectionManager {

	private static DBCollection getUserCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DBCollection coll = MongoDBSingleton.getDataBase().getCollection("User");
		return coll;
	}
	
	public static DBCursor getAllUsers() {
		DBCollection coll = getUserCollection();
		return coll.find();
	}
	
	public static User getUser(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		DBCollection coll = getUserCollection();
		
		return (User) coll.findOne(query);
	}

	public static boolean deleteUser(ObjectId _id){
		BasicDBObject query = new BasicDBObject("_id", _id);
		DBCollection coll = getUserCollection();
		
		return coll.remove(query).getN() > 0;
	}

	public static ObjectId addUser(String name) {
		User newUser = new User(name);
		DBCollection coll = getUserCollection();
		coll.insert(newUser);
		return newUser.getID();
	}

	public static DBCursor runQuery(BasicDBObject query) {
		DBCollection coll = getUserCollection();
		return coll.find(query);
	}
}
