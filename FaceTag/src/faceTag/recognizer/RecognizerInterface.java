package faceTag.recognizer;

import faceTag.entities.Rectangle;

public class RecognizerInterface {
	
	private static RecognizerInterface instance;
	
	public static RecognizerInterface getInstance() {
		if (instance == null){
			System.load("C:\\Users\\Chris_2\\Documents\\FaceTagWorkspace\\workspace\\FaceTag\\src\\faceTag\\recognizer\\faceTag_recognizer_RecognizerInterface.dll");
			//System.loadLibrary("faceTag_recognizer_RecognizerInterface");
			instance = new RecognizerInterface();
		}
		return instance;
	}

	public native Rectangle[] detectForRectangles(String filesLocation, String imageID);
	
}
