package faceTag.controllers;

public class StringTool {
	//Check if strings are not null or empty
	public static boolean isValid(String s) {
		if (s == null)
			return false;
		if (s.equals(""))
			return false;
		return true;
	}
}
