package faceTag.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class Image implements DBObject {
	ObjectId _id;
	ObjectId ownerID;
	String title;
	
	public Image(){}
	
	public Image(ObjectId ownerID, String title) {
		super();
		this._id = new ObjectId();
		this.ownerID = ownerID;
		this.title = title;
	}

	
	
	public ObjectId getID() {
		return _id;
	}



	public ObjectId getOwnerID() {
		return ownerID;
	}



	public String getTitle() {
		return title;
	}



	@Override
	public boolean containsField(String arg0) {
		return (arg0.equals("_id") || arg0.equals("ownerID") || arg0.equals("title"));
	}

	@Override
	public boolean containsKey(String arg0) {
		return containsField(arg0);
	}

	@Override
	public Object get(String arg0) {
		if (arg0.equals("_id"))
			return _id;
		else if (arg0.equals("ownerID"))
			return ownerID;
		else if (arg0.equals("title"))
			return title;
		else
			return null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		set.add("_id");
		set.add("ownerID");
		set.add("title");
		return set;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		if (arg0.equals("_id")) {
			_id = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("ownerID")) {
			ownerID = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("title")) {
			title = (String) arg1;
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
		if (map.containsKey("_id"))
			this._id = (ObjectId) map.get("_id");
		if (map.containsKey("ownerID"))
			this.ownerID = (ObjectId) map.get("ownerID");
		if (map.containsKey("title"))
			this.title = (String) map.get("title");
	}

	@Override
	public Object removeField(String arg0) {
		throw new RuntimeException("Unsupported method.");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (_id != null)
			map.put("_id", _id);
		if (ownerID != null)
			map.put("ownerID", ownerID);
		if (title != null)
			map.put("title", title);
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
