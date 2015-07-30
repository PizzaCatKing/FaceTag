package faceTag.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import faceTag.entities.Account;
import faceTag.entities.Friend;
import faceTag.entities.Image;
import faceTag.entities.Rectangle;
import faceTag.entities.Token;
import faceTag.entities.User;

public class MongoDBSingleton {
	private MongoClient mc;
	private static DB db;
	private static MongoDBSingleton instance;

	public MongoDBSingleton() {
		try {
			mc = new MongoClient("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		db = mc.getDB("FaceTag");

		DBCollection accountColl = db.getCollection("Account");
		accountColl.createIndex(new BasicDBObject("username",1), new BasicDBObject("unique",true));
		accountColl.createIndex(new BasicDBObject("password",1));
		accountColl.createIndex(new BasicDBObject("userID",1), new BasicDBObject("unique",true));
		accountColl.setObjectClass(Account.class);
		
		DBCollection  userColl = db.getCollection("User");
		userColl.createIndex(new BasicDBObject("name",1));
		userColl.setObjectClass(User.class);
		
		DBCollection  imageColl = db.getCollection("Image");
		imageColl.createIndex(new BasicDBObject("ownerID",1));
		imageColl.createIndex(new BasicDBObject("title",1));
		imageColl.setObjectClass(Image.class);
		
		DBCollection  tokenColl = db.getCollection("Token");
		tokenColl.createIndex(new BasicDBObject("userID",1));
		tokenColl.createIndex(new BasicDBObject("token",1));
		tokenColl.setObjectClass(Token.class);
		
		DBCollection  friendColl = db.getCollection("Friend");
		friendColl.createIndex(new BasicDBObject("userID1",1).append("userID2",1), new BasicDBObject("unique",true));
		friendColl.setObjectClass(Friend.class);
		
		DBCollection  rectColl = db.getCollection("Rectangle");
		rectColl.createIndex(new BasicDBObject("x1",1));
		rectColl.createIndex(new BasicDBObject("y1",1));
		rectColl.createIndex(new BasicDBObject("x2",1));
		rectColl.createIndex(new BasicDBObject("y2",1));
		rectColl.createIndex(new BasicDBObject("userID",1));
		rectColl.createIndex(new BasicDBObject("imageID",1));
		rectColl.setObjectClass(Rectangle.class);
	}

	public static MongoDBSingleton getInstance() throws UnknownHostException {
		if (instance == null)
			instance = new MongoDBSingleton();
		return instance;
	}

	public static DB getDataBase() {
		return db;
	}
}
