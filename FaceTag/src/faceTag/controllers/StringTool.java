package faceTag.controllers;

import org.bson.types.ObjectId;

public class StringTool {
	//Check if strings are not null or empty
	public static boolean isValid(String s) {
		if (s == null)
			return false;
		if (s.equals(""))
			return false;
		return true;
	}
	
	public static boolean isValidObjectID(String id) {
		try{
			new ObjectId(id);
		}
		catch(Exception e){
			return false;
		}
		return true;
	}
}
