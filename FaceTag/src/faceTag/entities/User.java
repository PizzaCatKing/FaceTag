package faceTag.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.DBObject;

public class User implements DBObject {

	String _id;
	String name;

	public User(String name) {
		this.name = name;
	}

	@Override
	public boolean containsField(String arg0) {
		return (arg0.equals("_id") || arg0.equals("name"));
	}

	@Override
	public boolean containsKey(String arg0) {
		return containsField(arg0);
	}

	@Override
	public Object get(String arg0) {
		if (arg0.equals("_id"))
			return _id;
		else if (arg0.equals("name"))
			return name;
		else
			return null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		set.add("_id");
		set.add("name");
		return set;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		if (arg0.equals("_id")) {
			_id = (String) arg1;
			return arg1;
		}
		if (arg0.equals("name")) {
			name = (String) arg1;
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
			this._id = (String) map.get("_id");
		if (map.containsKey("name"))
			this.name = (String) map.get("name");
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
		if (name != null)
			map.put("name", name);
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
