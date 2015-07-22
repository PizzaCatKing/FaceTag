package faceTag.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class Account implements DBObject {
	private ObjectId userID;
	private String username;
	private String password;
	
	public ObjectId getUserID(){
		return userID;
	}
	public Account(ObjectId userID, String username, String password) {
		super();
		this.userID = userID;
		this.username = username;
		this.password = password;
	}

	public Account() {}

	@Override
	public boolean containsField(String arg0) {
		return (arg0.equals("userID") 
				|| arg0.equals("username")
				|| arg0.equals("password"));
	}

	@Override
	public boolean containsKey(String arg0) {
		return containsField(arg0);
	}

	@Override
	public Object get(String arg0) {
		if (arg0.equals("userID")) return userID;
		else if (arg0.equals("username")) return username;
		else if (arg0.equals("password")) return password;
		else return null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		set.add("userID");
		set.add("username");
		set.add("password");
		return set;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		if (arg0.equals("userID")){
			userID = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("username")){
			username = (String) arg1;
			return arg1;
		}
		if (arg0.equals("password")){
			password = (String) arg1;
			return arg1;
		}
		return null;
	}

	@Override
	public void putAll(BSONObject arg0) {
		if(arg0 != null){
			for (String key : arg0.keySet()){
				put(key, arg0.get(key));
			}
		}
	}

	@Override
	public void putAll(@SuppressWarnings("rawtypes") Map arg0) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) arg0;
		if (map.containsKey("userID"))
			this.userID = (ObjectId) map.get("userID");
		if (map.containsKey("username"))
			this.username = (String) map.get("username");
		if (map.containsKey("password"))
			this.password = (String) map.get("password");
	}

	@Override
	public Object removeField(String arg0) {
		throw new RuntimeException("Unsupported method.");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (userID != null) map.put("userID", userID);
		if (username != null) map.put("username",username);
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
