package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

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

	public static User getUser(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<User> coll = getUserCollection();

		User result = new User();
		result.putAll(coll.find(query).first());
		return result;
	}

	public static User deleteUser(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<User> coll = getUserCollection();
		User result = new User();
		result.putAll(coll.findOneAndDelete(query));
		return result;
	}

	public static ObjectId addUser(String name) {
		User newUser = new User(name);
		MongoCollection<User> coll = getUserCollection();
		coll.insertOne(newUser);
		return newUser.getID();
	}

	public static FindIterable<User> runQuery(BasicDBObject query) {
		MongoCollection<User> coll = getUserCollection();
		return coll.find(query);
	}
}
