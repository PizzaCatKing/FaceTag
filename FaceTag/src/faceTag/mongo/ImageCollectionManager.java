package faceTag.mongo;

import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import faceTag.entities.Image;

public class ImageCollectionManager {
	private static DBCollection getImageCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DBCollection coll = MongoDBSingleton.getDataBase().getCollection("Image");
		return coll;
	}

	public static Image getImage(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		DBCollection coll = getImageCollection();
		
		return (Image) coll.findOne(query);
		
	}

	// BE SURE TO DELETE THE IMAGE OFF OF THE DRIVE BEFORE DELETING THE ENTRY IN
	// THE DATABASE
	public static boolean deleteImage(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		DBCollection coll = getImageCollection();
		// If at least 1 image was deleted we were successful
		return coll.remove(query).getN() > 0;
	}

	// The image location on the drive is [IMAGE_DIR]/{_id}
	public static Image addImage(ObjectId ownerID, String title) {
		DBCollection coll = getImageCollection();
		Image imageObject = new Image(ownerID, title);

		coll.insert(imageObject);
		return imageObject;
	}

	public static DBCursor getImagesForUser(ObjectId ownerID) {
		BasicDBObject query = new BasicDBObject("ownerID", ownerID);
		DBCollection coll = getImageCollection();
		return coll.find(query);
	}

	public static DBCursor runQuery(BasicDBObject query) {
		DBCollection coll = getImageCollection();
		return coll.find(query);
	}
	
	public static DBCursor getAllImages(List<ObjectId> ids) {
		DBCollection coll = getImageCollection();
		BasicDBList list = new BasicDBList();
		
		if(ids.isEmpty()){
			return null;
		}
		
		for(ObjectId id: ids){
			list.add(new BasicDBObject("_id", id));
		}
		BasicDBObject query = new BasicDBObject("$or", list);
		return coll.find(query);
	}
}
