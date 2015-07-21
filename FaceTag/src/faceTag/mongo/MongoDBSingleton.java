package faceTag.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBSingleton {
	private MongoClient mc;
	private static MongoDatabase db;
	private static MongoDBSingleton instance;

	public MongoDBSingleton() {
		mc = new MongoClient("localhost");

		db = mc.getDatabase("FaceTag");

		MongoCollection<?> accountColl = db.getCollection("Account");
		accountColl.createIndex(new BasicDBObject("username",1), new IndexOptions().unique(true));
		accountColl.createIndex(new BasicDBObject("password",1));
		accountColl.createIndex(new BasicDBObject("userID",1), new IndexOptions().unique(true));
		
		MongoCollection<?> userColl = db.getCollection("User");
		userColl.createIndex(new BasicDBObject("name",1));
		
		MongoCollection<?> imageColl = db.getCollection("Image");
		imageColl.createIndex(new BasicDBObject("ownerID",1));
		imageColl.createIndex(new BasicDBObject("title",1));
		
		MongoCollection<?> tokenColl = db.getCollection("Token");
		tokenColl.createIndex(new BasicDBObject("userID",1));
		tokenColl.createIndex(new BasicDBObject("token",1));
		
		MongoCollection<?> friendColl = db.getCollection("Friend");
		friendColl.createIndex(new BasicDBObject("userID1",1).append("userID2",1), new IndexOptions().unique(true));
	}

	public static MongoDBSingleton getInstance() throws UnknownHostException {
		if (instance == null)
			instance = new MongoDBSingleton();
		return instance;
	}

	public static MongoDatabase getDataBase() {
		return db;
	}
}
