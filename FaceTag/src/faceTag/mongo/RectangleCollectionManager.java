package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import faceTag.entities.Rectangle;

public class RectangleCollectionManager {
	
	private static DBCollection getRectangleCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DBCollection coll = MongoDBSingleton.getDataBase().getCollection("Rectangle");
		return coll;
	}
	
	public static DBCursor getRectanglesForImage(ObjectId imageID){
		BasicDBObject query = new BasicDBObject("imageID", imageID);
		DBCollection coll = getRectangleCollection();
		return coll.find(query);
	}
	
	public static boolean deleteForImage(ObjectId imageID){
		BasicDBObject query = new BasicDBObject("imageID", imageID);
		DBCollection coll = getRectangleCollection();
		return coll.remove(query).getN() > 0;
	}
	
	public static DBCursor getRectanglesForUser(ObjectId userID){
		BasicDBObject query = new BasicDBObject("userID", userID);
		DBCollection coll = getRectangleCollection();
		return coll.find(query);
	}
	
	public static Rectangle[] addRectangles(Rectangle[] rectangles) {
		DBCollection coll = getRectangleCollection();

		coll.insert(rectangles);
		return rectangles;
	}
	
	public static BasicDBList updateRectangles(BasicDBList rectangles) {
		DBCollection coll = getRectangleCollection();
		for(Object rect : rectangles){
			coll.save((DBObject) rect);
		}
		
		return rectangles;
	}
	
}
