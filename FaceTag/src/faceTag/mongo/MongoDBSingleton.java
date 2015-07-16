package faceTag.mongo;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBSingleton {
	private MongoClient mc;
	private static MongoDatabase db;
	private static MongoDBSingleton instance;

	public MongoDBSingleton() {
		mc = new MongoClient("localhost");
		db = mc.getDatabase("FaceTag");

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
