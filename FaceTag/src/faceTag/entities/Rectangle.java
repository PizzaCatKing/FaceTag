package faceTag.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

public class Rectangle implements DBObject {
	private ObjectId _id;
	private ObjectId imageID;
	private ObjectId userID;
	public int x1;
	public int y1; 
	public int x2;
	public int y2;
	
	public Rectangle(){}
	
	public Rectangle(int _x1,int _y1,int _x2,int _y2){
		x1= _x1;
		y1= _y1;
		x2= _x2;
		y2= _y2;
	}
	
	public String getImageIDString(){
		System.out.println("T1");
		System.out.println(imageID.toHexString());
		return imageID.toHexString();
	}
	
	public String getUserIDString(){
		System.out.println("T2");
		
		if(userID == null){
			System.out.println("(NULL)");
			return "";
		}
		System.out.println("(" + userID.toHexString() +")");
		return userID.toHexString();
	}

	@Override
	public boolean containsField(String arg0) {
		return (arg0.equals("_id") 
				|| arg0.equals("imageID")
				|| arg0.equals("userID")
				|| arg0.equals("x1")
				|| arg0.equals("y1")
				|| arg0.equals("x2")
				|| arg0.equals("y2"));
	}

	@Override
	public boolean containsKey(String arg0) {
		return containsField(arg0);
	}

	@Override
	public Object get(String arg0) {
		if (arg0.equals("_id")) return _id;
		else if (arg0.equals("imageID")) return imageID;
		else if (arg0.equals("userID")) return userID;
		else if (arg0.equals("x1")) return x1;
		else if (arg0.equals("y1")) return y1;
		else if (arg0.equals("x2")) return x2;
		else if (arg0.equals("y2")) return y2;
		else return null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		set.add("_id");
		set.add("imageID");
		set.add("userID");
		set.add("x1");
		set.add("y1");
		set.add("x2");
		set.add("y2");
		return set;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		if (arg0.equals("_id")){
			_id = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("imageID")){
			imageID = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("userID")){
			userID = (ObjectId) arg1;
			return arg1;
		}
		if (arg0.equals("x1")){
			x1 = (int) arg1;
			return arg1;
		}if (arg0.equals("y1")){
			y1 = (int) arg1;
			return arg1;
		}if (arg0.equals("x2")){
			x2 = (int) arg1;
			return arg1;
		}if (arg0.equals("y2")){
			y2 = (int) arg1;
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
		if (map.containsKey("_id"))
			this._id = (ObjectId) map.get("_id");
		if (map.containsKey("imageID"))
			this.imageID = (ObjectId) map.get("imageID");
		if (map.containsKey("userID"))
			this.userID = (ObjectId) map.get("userID");
		if (map.containsKey("x1"))
			this.x1 = (int) map.get("x1");
		if (map.containsKey("y1"))
			this.y1 = (int) map.get("y1");
		if (map.containsKey("x2"))
			this.x2 = (int) map.get("x2");
		if (map.containsKey("y2"))
			this.y2 = (int) map.get("y2");
	}

	@Override
	public Object removeField(String arg0) {
		throw new RuntimeException("Unsupported method.");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (_id != null) map.put("_id", _id);
		if (imageID != null) map.put("imageID", imageID);
		if (userID != null) map.put("userID", userID);
		map.put("x1", x1);
		map.put("y1", y1);
		map.put("x2", x2);
		map.put("y2", y2);
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
