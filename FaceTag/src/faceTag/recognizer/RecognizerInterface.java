package faceTag.recognizer;

import java.io.File;

import faceTag.entities.Globals;
import faceTag.entities.Rectangle;

public class RecognizerInterface {
	
	private static RecognizerInterface instance;
	
	private String filesLocation;
	
	public static RecognizerInterface getInstance() {
		if (instance == null){
			System.load("C:\\Users\\Chris_2\\Documents\\FaceTagWorkspace\\workspace\\FaceTag\\src\\faceTag\\recognizer\\faceTag_recognizer_RecognizerInterface.dll");
			//System.loadLibrary("faceTag_recognizer_RecognizerInterface");
			instance = new RecognizerInterface();
			instance.filesLocation = Globals.FILES_ROOT;
			new File(instance.filesLocation + Globals.RECOGNIZER_ROOT_EXTENTION).mkdirs();
		}
		return instance;
	}
	// Generates the rectangles for the image
	public native Rectangle[] detectForRectangles(String imageID);
	// Returns a set of rectangles with updated IDs.
	// The userIds are the IDs that should be checked, as only a users friends can be recognized
	public native String[] recgonizeRectangles(String[] userIDS, Rectangle[] rect);
	// Returning from user input updates the recognizer with the new IDs and rectangles
	// These IDs are considered correct
	// Return false if an error occurred
	public native boolean updateRecognizer(Rectangle[] rect);
	
}
