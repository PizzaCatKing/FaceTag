package faceTag.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class Friend implements DBObject, Bson {

	private ObjectId userID1; // Always smaller
	private ObjectId userID2; // Always larger

	public Friend(){}
	
	public Friend(ObjectId id1, ObjectId id2) {
		if (id1.compareTo(id2) < 0) {
			userID1 = id1;
			userID2 = id2;
		} else {
			userID1 = id2;
			userID2 = id1;
		}
	}

	
	
	public ObjectId getUserID1() {
		return userID1;
	}

	public ObjectId getUserID2() {
		return userID2;
	}


	@Override
	public boolean containsField(String arg0) {
		return (arg0.equals("userID1") || arg0.equals("userID2"));
	}

	@Override
	public boolean containsKey(String arg0) {
		return containsField(arg0);
	}

	@Override
	public Object get(String arg0) {
		if (arg0.equals("userID1"))
			return userID1;
		else if (arg0.equals("userID2"))
			return userID2;
		else
			return null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		set.add("userID1");
		set.add("userID2");
		return set;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		if (arg0.equals("userID1")) {
			userID1 = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("userID2")) {
			userID2 = (ObjectId) arg1;
			return arg1;
		}
		return null;
	}

	@Override
	public void putAll(BSONObject arg0) {
		if(arg0 != null){
			for (String key : arg0.keySet()) {
				put(key, arg0.get(key));
			}
		}
	}

	@Override
	public void putAll(@SuppressWarnings("rawtypes") Map arg0) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) arg0;
		if (map.containsKey("userID1"))
			this.userID1 = (ObjectId) map.get("userID1");
		if (map.containsKey("userID2"))
			this.userID2 = (ObjectId) map.get("userID2");
	}

	@Override
	public Object removeField(String arg0) {
		throw new RuntimeException("Unsupported method.");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (userID1 != null)
			map.put("userID1", userID1);
		if (userID2 != null)
			map.put("userID2", userID2);
		return map;
	}

	@Override
	public boolean isPartialObject() {
		return false;
	}

	@Override
	public void markAsPartialObject() {
		throw new RuntimeException("Unsupported method.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		BsonDocument doc = new BsonDocument();
		doc.putAll(this.toMap());
		return doc;
	}

}
