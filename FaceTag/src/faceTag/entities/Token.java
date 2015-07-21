package faceTag.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class Token implements DBObject {

	ObjectId userID;
	String token;
	
	public Token(){}
	
	public Token(ObjectId userID, String token) {
		super();
		this.userID = userID;
		this.token = token;
	}
	
	public ObjectId getUserID() {
		return userID;
	}

	public String getToken() {
		return token;
	}

	@Override
	public boolean containsField(String arg0) {
		return (arg0.equals("userID") || arg0.equals("token"));
	}

	@Override
	public boolean containsKey(String arg0) {
		return containsField(arg0);
	}

	@Override
	public Object get(String arg0) {
		if (arg0.equals("userID"))
			return userID;
		else if (arg0.equals("token"))
			return token;
		else
			return null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		set.add("userID");
		set.add("token");
		return set;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		if (arg0.equals("userID")) {
			userID = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("token")) {
			token = (String) arg1;
			return arg1;
		}
		return null;
	}

	@Override
	public void putAll(BSONObject arg0) {
		for (String key : arg0.keySet()) {
			put(key, arg0.get(key));
		}
	}

	@Override
	public void putAll(@SuppressWarnings("rawtypes") Map arg0) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) arg0;
		if (map.containsKey("userID"))
			this.userID = (ObjectId) map.get("userID");
		if (map.containsKey("token"))
			this.token = (String) map.get("token");
	}

	@Override
	public Object removeField(String arg0) {
		throw new RuntimeException("Unsupported method.");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (userID != null)
			map.put("userID", userID);
		if (token != null)
			map.put("token", token);
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

}
