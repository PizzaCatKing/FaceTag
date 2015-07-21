package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

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

	public static Image getImage(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<Image> coll = getImageCollection();
		Image result = new Image();
		result.putAll(coll.find(query).first());
		return result;
	}

	// BE SURE TO DELETE THE IMAGE OFF OF THE DRIVE BEFORE DELETING THE ENTRY IN
	// THE DATABASE
	public static Image deleteImage(ObjectId _id) {
		BasicDBObject query = new BasicDBObject("_id", _id);
		MongoCollection<Image> coll = getImageCollection();

		Image result = new Image();
		result.putAll(coll.findOneAndDelete(query));
		return result;
	}

	// The image location on the drive is [IMAGE_DIR]/{_id}
	public static Image addImage(ObjectId ownerID, String title) {
		MongoCollection<Image> coll = getImageCollection();
		Image imageObject = new Image(ownerID, title);

		coll.insertOne(imageObject);
		return imageObject;
	}

	public static FindIterable<BasicDBObject> getImagesForUser(ObjectId ownerID) {
		BasicDBObject query = new BasicDBObject("ownerID", ownerID);
		MongoCollection<Image> coll = getImageCollection();
		return coll.find(query, BasicDBObject.class);
	}

	public static FindIterable<Image> runQuery(BasicDBObject query) {
		MongoCollection<Image> coll = getImageCollection();
		return coll.find(query);
	}
}
