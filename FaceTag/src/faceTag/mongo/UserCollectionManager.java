package faceTag.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import faceTag.entities.User;

public class UserCollectionManager {

	private static MongoCollection<User> getUserCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoCollection<User> coll = MongoDBSingleton.getDataBase().getCollection("User").withDocumentClass(User.class);
		return coll;
	}

	public static User getUser(String _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<User> coll = getUserCollection();
		return coll.find(query).first();
	}

	public static User deleteUser(String _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<User> coll = getUserCollection();
		return coll.findOneAndDelete(query);
	}

	public static String addUser(String name) {
		User newUser = new User(name);
		MongoCollection<User> coll = getUserCollection();
		coll.insertOne(newUser);
		return (String) newUser.get("_id");
	}

	public static FindIterable<User> runQuery(BasicDBObject query) {
		MongoCollection<User> coll = getUserCollection();
		return coll.find(query);
	}
}
