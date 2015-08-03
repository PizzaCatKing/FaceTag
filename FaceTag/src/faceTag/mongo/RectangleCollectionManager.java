package faceTag.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import faceTag.controllers.StringTool;
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
	
	public static List<ObjectId> getImagesWithUsers(List<String> include,List<String> exclude) {
		DBCollection coll = getRectangleCollection();
		List<ObjectId> resultList = new ArrayList<ObjectId>();
		
		if(include.isEmpty() && exclude.isEmpty()){
			return new ArrayList<ObjectId>();
		}
		
		if(!include.isEmpty()){
			for (String id : include) {
				if(StringTool.isValid(id)){
					if(StringTool.isValidObjectID(id)){
						@SuppressWarnings("unchecked")
						List<ObjectId> result = coll.distinct("imageID", new BasicDBObject("userID", new ObjectId(id)));
						System.out.println("retain: " + result);
						if(resultList.isEmpty()){
							resultList.addAll(result);
						}
						else{
						resultList.retainAll(result);
						}
					}
				}
			}
		}
		
		if(!exclude.isEmpty()){
			for (String id : exclude) {
				if(StringTool.isValid(id)){
					if(StringTool.isValidObjectID(id)){
						@SuppressWarnings("unchecked")
						List<ObjectId> result = coll.distinct("imageID", new BasicDBObject("userID", new ObjectId(id)));
						System.out.println("remove: " + result);
						resultList.removeAll(result);
					}
				}
			}
		}
		return resultList;
	}
}
