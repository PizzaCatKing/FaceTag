package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import faceTag.entities.Friend;
import faceTag.entities.User;

public class FriendCollectionManager {

	private static MongoCollection<Friend> getFriendCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoCollection<Friend> coll = MongoDBSingleton.getDataBase().getCollection("Friend")
				.withDocumentClass(Friend.class);
		return coll;
	}

	// ID1 is us, ID2 is the friend
	public static Friend addFriend(ObjectId userID, ObjectId friendID) throws MongoWriteException {
		// Check friend exists
		User friend = UserCollectionManager.getUser(friendID);
		if (friend == null) {
			return null;
		}
		Friend newFriend;
		if (userID.equals(friendID)) {
			return null;
		} else if (userID.compareTo(friendID) < 0) {
			newFriend = new Friend(userID, friendID);
		} else {
			newFriend = new Friend(friendID, userID);
		}

		MongoCollection<Friend> coll = getFriendCollection();
		coll.insertOne(newFriend);
		return newFriend;
	}

	// ID1 is us, ID2 is the friend
	public static Friend deleteFriend(ObjectId userID, ObjectId friendID) {
		BasicDBObject friendToDelete;
		if (userID.equals(friendID)) {
			return null;
		} else if (userID.compareTo(friendID) < 0) {
			friendToDelete = new BasicDBObject("userID1", userID).append("userID2", friendID);
		} else {
			friendToDelete = new BasicDBObject("userID1", friendID).append("userID2", userID);
		}

		MongoCollection<Friend> coll = getFriendCollection();
		Friend result = new Friend();
		result.putAll(coll.findOneAndDelete(friendToDelete));
		if(result.getUserID1() == null || result.getUserID1() == null) return null;
		return result;
	}

	public static Boolean getFriendship(ObjectId id1, ObjectId id2) {

		BasicDBObject query;
		if (id1.equals(id2)) {
			return false;
		} else if (id1.compareTo(id2) < 0) {
			query = new BasicDBObject("userID1", id1).append("userID2", id2);
		} else {
			query = new BasicDBObject("userID1", id2).append("userID2", id1);
		}
		MongoCollection<Friend> coll = getFriendCollection();

		return coll.count(query) == 1;
	}

	// Returns all of user with userID's friends as user objects
	public static BasicDBList getFriendsForUser(ObjectId userID) {
		// Our user is user 1
		BasicDBObject clause1 = new BasicDBObject("userID1", userID);
		// Our user is user 2
		BasicDBObject clause2 = new BasicDBObject("userID2", userID);

		MongoCollection<Friend> coll = getFriendCollection();

		FindIterable<BasicDBObject> search1 = coll.find(clause1,BasicDBObject.class);
		FindIterable<BasicDBObject> search2 = coll.find(clause2,BasicDBObject.class);

		BasicDBList results = new BasicDBList();

		// We are friend 1 so we need to find all the friend 2s
		for (BasicDBObject friend : search1) {
			BasicDBObject friendProfile = new BasicDBObject();
			friendProfile.putAll(UserCollectionManager.getUser((ObjectId) friend.get("userID2")));
			results.add(friendProfile);
		}

		// We are friend 2 so we need to find all the friend 1s
		for (BasicDBObject friend : search2) {
			BasicDBObject friendProfile = new BasicDBObject();
			friendProfile.putAll(UserCollectionManager.getUser((ObjectId) friend.get("userID1")));
			results.add(friendProfile);
		}
		
		return results;
	}

}
