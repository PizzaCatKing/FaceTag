package faceTag.mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import faceTag.entities.Image;

public class ImageCollectionManager {
	private static MongoCollection<Image> getImageCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoCollection<Image> coll = MongoDBSingleton.getDataBase().getCollection("Image")
				.withDocumentClass(Image.class);
		return coll;
	}

	public static Image getImage(String _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<Image> coll = getImageCollection();
		return coll.find(query).first();
	}

	// BE SURE TO DELETE THE IMAGE OFF OF THE DRIVE BEFORE DELETING THE ENTRY IN
	// THE DATABASE
	public static Image deleteImage(String _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<Image> coll = getImageCollection();
		return coll.findOneAndDelete(query);
	}

	// The image location on the drive is [IMAGE_DIR]/{_id}
	public static Image addImage(String ownerID, String title) {
		MongoCollection<Image> coll = getImageCollection();
		Image imageObject = new Image(ownerID, title);
		
		coll.insertOne(imageObject);
		return imageObject;
	}

	public static Iterable<Image> getImagesForUser(String ownerID) {
		BasicDBObject query = new BasicDBObject("ownerID", ownerID);
		MongoCollection<Image> coll = getImageCollection();

		return coll.find(query);
	}

	public static FindIterable<Image> runQuery(BasicDBObject query) {
		MongoCollection<Image> coll = getImageCollection();
		return coll.find(query);
	}
}
