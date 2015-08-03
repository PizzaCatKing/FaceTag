#include <jni.h>
#include <stdio.h>
#include "faceTag_recognizer_RecognizerInterface.h"
#include <string.h>
#include <map>
#include <iostream>
#include <cmath>
#include <limits>
#include <fstream>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/contrib/contrib.hpp>



using namespace cv;
using namespace std;

JNIEXPORT jobjectArray JNICALL Java_faceTag_recognizer_RecognizerInterface_detectForRectangles(JNIEnv *env , jobject thisObj, jstring _imageID){
	jclass jClassRecognizerInterface = env->GetObjectClass(thisObj);
	jfieldID fid_pathString = env->GetFieldID(jClassRecognizerInterface, "filesLocation" , "Ljava/lang/String;");
	jfieldID fid_imagePathString = env->GetFieldID(jClassRecognizerInterface, "imageExtention" , "Ljava/lang/String;");
	jfieldID fid_recognizerPathString = env->GetFieldID(jClassRecognizerInterface, "filesLocation" , "Ljava/lang/String;");
	jstring _filesLocation = (jstring)env->GetObjectField(thisObj, fid_pathString);
	jstring _imageLocation = (jstring)env->GetObjectField(thisObj, fid_imagePathString);
	jstring _recognizerLocation = (jstring)env->GetObjectField(thisObj, fid_recognizerPathString);


	const char * filesLocationArr;
	const char * imageLocationArr;
	const char * recognizerLocationArr;
	const char * imageIDArr;

	filesLocationArr = env->GetStringUTFChars(_filesLocation, 0);
	imageLocationArr = env->GetStringUTFChars(_imageLocation, 0);
	recognizerLocationArr = env->GetStringUTFChars(_recognizerLocation, 0);
	imageIDArr = env->GetStringUTFChars(_imageID, 0);

	string filesLocation(filesLocationArr);
	string imageLocation(imageLocationArr);
	string recognizerLocation(recognizerLocationArr);
	string imageID(imageIDArr);

	env->ReleaseStringUTFChars(_filesLocation, filesLocationArr);
	env->ReleaseStringUTFChars(_imageLocation, imageLocationArr);
	env->ReleaseStringUTFChars(_recognizerLocation, recognizerLocationArr);
	env->ReleaseStringUTFChars(_imageID, imageIDArr);

	CascadeClassifier face_cascade;

	Mat image = imread(filesLocation + imageLocation + imageID, CV_LOAD_IMAGE_GRAYSCALE);
	if( !face_cascade.load( filesLocation + "haarcascade_frontalface_alt.xml" ) ){ printf("--(!)Error loading face cascade\n"); cin.ignore(); return NULL; };

	vector<Rect> faces;
	equalizeHist( image, image );

	//-- Detect faces
	face_cascade.detectMultiScale( image, faces, 1.1, 2, 0, Size(100, 100) );
	cout << "Found " << faces.size() << " faces!" << endl;

	jclass rectClass = env->FindClass("faceTag/entities/Rectangle");
	jmethodID rectConstructor = env->GetMethodID(rectClass, "<init>", "(IIII)V");


	jobjectArray jRectArray = env->NewObjectArray(faces.size(), rectClass, NULL);

	for(int i =0; i< faces.size(); i++){
		jobject newRect = env->NewObject(rectClass, rectConstructor, faces[i].x, faces[i].y, faces[i].x + faces[i].width, faces[i].y + faces[i].height);
		 env->SetObjectArrayElement(jRectArray, i, newRect);
	}


	return jRectArray;
}

JNIEXPORT jobjectArray JNICALL Java_faceTag_recognizer_RecognizerInterface_recgonizeRectangles
  (JNIEnv *env, jobject thisObj, jobjectArray _userIDS, jobjectArray _rectangles)
{	//Get classes
	jclass jClassRecognizerInterface = env->GetObjectClass(thisObj);
	jclass jClassRectangle = env->FindClass("faceTag/entities/Rectangle");
	jobjectArray ret;


	//Get fields
	jfieldID fid_pathString = env->GetFieldID(jClassRecognizerInterface, "filesLocation" , "Ljava/lang/String;");
	jfieldID fid_imagePathString = env->GetFieldID(jClassRecognizerInterface, "imageExtention" , "Ljava/lang/String;");
	jfieldID fid_recognizerPathString = env->GetFieldID(jClassRecognizerInterface, "filesLocation" , "Ljava/lang/String;");
	//get class methods we will be using
	jmethodID rectGetImageIDMethod = env->GetMethodID(jClassRectangle, "getImageIDString", "()Ljava/lang/String;");
	jmethodID rectGetUserIDMethod = env->GetMethodID(jClassRectangle, "getUserIDString", "()Ljava/lang/String;");
	jfieldID rectX1 = env->GetFieldID(jClassRectangle, "x1", "I");
	jfieldID rectY1 = env->GetFieldID(jClassRectangle, "y1", "I");
	jfieldID rectX2 = env->GetFieldID(jClassRectangle, "x2", "I");
	jfieldID rectY2 = env->GetFieldID(jClassRectangle, "y2", "I");
	//jmethodID rectGetY1Method = env->GetMethodID(jClassRectangle, "getY1", "()I;");

	jstring _filesLocation = (jstring)env->GetObjectField(thisObj, fid_pathString);
	jstring _imageLocation = (jstring)env->GetObjectField(thisObj, fid_imagePathString);
	jstring _recognizerLocation = (jstring)env->GetObjectField(thisObj, fid_recognizerPathString);

	const char * filesLocationArr;
	const char * imageLocationArr;
	const char * recognizerLocationArr;

	filesLocationArr = env->GetStringUTFChars(_filesLocation, 0);
	imageLocationArr = env->GetStringUTFChars(_imageLocation, 0);
	recognizerLocationArr = env->GetStringUTFChars(_recognizerLocation, 0);

	string filesLocation(filesLocationArr);
	string imageLocation(imageLocationArr);
	string recognizerLocation(recognizerLocationArr);

	env->ReleaseStringUTFChars(_filesLocation, filesLocationArr);
	env->ReleaseStringUTFChars(_imageLocation, imageLocationArr);
	env->ReleaseStringUTFChars(_recognizerLocation, recognizerLocationArr);


	int idCount = env->GetArrayLength(_userIDS);
	Ptr<FaceRecognizer>* recognizers = new Ptr<FaceRecognizer>[idCount];
	//Load recognizers
	for (int i=0; i < idCount; i++) {
		jstring userIDString = (jstring) env->GetObjectArrayElement(_userIDS, i);
		const char *rawUserIDString = env->GetStringUTFChars(userIDString, 0);
		string userID(rawUserIDString);
		env->ReleaseStringUTFChars(userIDString, rawUserIDString);
		if(userID != ""){
			string recognizerFileName(filesLocation + recognizerLocation + userID);
			if (ifstream(recognizerFileName.c_str())){
				recognizers[i] = createLBPHFaceRecognizer();
				recognizers[i]->load(recognizerFileName);
			}
			else{
				recognizers[i] = NULL;
			}
		}
		else{
			recognizers[i] = NULL;
		}

	}
	// Don't reload images we have already loaded
	map<string, Mat> images;
	int rectCount = env->GetArrayLength(_rectangles);

	ret= (jobjectArray)env->NewObjectArray(rectCount,
		         env->FindClass("java/lang/String"),
		         env->NewStringUTF(""));


	for (int j=0; j<rectCount; j++) {
		jobject rectangleObject = env->GetObjectArrayElement(_rectangles, j);

		jstring imageIDString;
		imageIDString = (jstring)env->CallObjectMethod(rectangleObject, rectGetImageIDMethod);
		const char *strReturn = env->GetStringUTFChars(imageIDString, 0);
		string imageID(strReturn);
		env->ReleaseStringUTFChars(imageIDString, strReturn);
		Mat currentImage;
		string imagesFileName(filesLocation + imageLocation + imageID);
		if (ifstream(imagesFileName.c_str())){
			if ( images.find(imageID) == images.end() ) {
			  // Load image
				images[imageID] = imread(imagesFileName, CV_LOAD_IMAGE_GRAYSCALE);

			}
		}
		currentImage = images[imageID];
		if( currentImage.empty() )
		{
			cout <<" --(!) No image found - " << imagesFileName << " !" << endl;
		}
		else
		{
			cout << "Starting recognizer: " << idCount << endl;
			// We have a valid image, get the rectangle and process the image
			//int x1 = (int)CallIntMethod(env, rectangleObject, rectGetX1Method);
			int x1  = env->GetIntField(rectangleObject, rectX1);
			int y1  = env->GetIntField(rectangleObject, rectY1);
			int x2  = env->GetIntField(rectangleObject, rectX2);
			int y2  = env->GetIntField(rectangleObject, rectY2);
			Mat croppedImage(currentImage, Rect(x1, y1, abs(x2-x1), abs(y2-y1)));
			int bestMatchIndex = -1;
			double bestPredictedConfidence = -1;

			// Get prediction confidence from the model
			for (int r=0; r < idCount; r++) {
				cout << "R: " << r  << endl;
				if(recognizers[r] != NULL){
					int predicted_label = -1;
					double predicted_confidence = 0.0;
					recognizers[r]->predict(croppedImage, predicted_label, predicted_confidence);
					cout << "Found: " << predicted_label << " with confidence: " <<predicted_confidence << endl;
					if(predicted_confidence >= 0){
						if(predicted_confidence < bestPredictedConfidence || bestPredictedConfidence == -1){
							bestPredictedConfidence = predicted_confidence;
							bestMatchIndex = r;
						}
					}
				}
			}
			// We have the best values, add them to the rectangles
			if(bestMatchIndex != -1){
				jstring bestIDString = (jstring)env->GetObjectArrayElement(_userIDS, bestMatchIndex);
				const char *bestUserRaw = env->GetStringUTFChars(bestIDString, 0);
				string bestUser(bestUserRaw);
				env->ReleaseStringUTFChars(bestIDString, bestUserRaw);
				env->SetObjectArrayElement(ret,j,env->NewStringUTF(bestUser.c_str()));

			}
			else{
				env->SetObjectArrayElement(ret,j,env->NewStringUTF(""));
			}
		}
	}
	//Return string array
	delete[] recognizers;
	return ret;
}

JNIEXPORT jboolean JNICALL Java_faceTag_recognizer_RecognizerInterface_updateRecognizer
  (JNIEnv * env, jobject thisObj, jobjectArray _rectangles)
{
	cout << "start" << endl;
	jclass jClassRecognizerInterface = env->GetObjectClass(thisObj);
	jfieldID fid_pathString = env->GetFieldID(jClassRecognizerInterface, "filesLocation" , "Ljava/lang/String;");
	jfieldID fid_imagePathString = env->GetFieldID(jClassRecognizerInterface, "imageExtention" , "Ljava/lang/String;");
	jfieldID fid_recognizerPathString = env->GetFieldID(jClassRecognizerInterface, "filesLocation" , "Ljava/lang/String;");

	jstring _filesLocation = (jstring)env->GetObjectField(thisObj, fid_pathString);
	jstring _imageLocation = (jstring)env->GetObjectField(thisObj, fid_imagePathString);
	jstring _recognizerLocation = (jstring)env->GetObjectField(thisObj, fid_recognizerPathString);

	//Get classes
	jclass jClassRectangle = env->FindClass("faceTag/entities/Rectangle");

	//get class methods we will be using
	jmethodID rectGetImageIDMethod = env->GetMethodID(jClassRectangle, "getImageIDString", "()Ljava/lang/String;");
	jmethodID rectGetUserIDMethod = env->GetMethodID(jClassRectangle, "getUserIDString", "()Ljava/lang/String;");
	//jmethodID rectGetX1Method = env->GetMethodID(jClassRectangle, "getX1", "()I");
	jfieldID rectX1 = env->GetFieldID(jClassRectangle, "x1", "I");
	jfieldID rectY1 = env->GetFieldID(jClassRectangle, "y1", "I");
	jfieldID rectX2 = env->GetFieldID(jClassRectangle, "x2", "I");
	jfieldID rectY2 = env->GetFieldID(jClassRectangle, "y2", "I");

	const char * filesLocationArr;
	const char * imageLocationArr;
	const char * recognizerLocationArr;

	filesLocationArr = env->GetStringUTFChars(_filesLocation, 0);
	imageLocationArr = env->GetStringUTFChars(_imageLocation, 0);
	recognizerLocationArr = env->GetStringUTFChars(_recognizerLocation, 0);

	string filesLocation(filesLocationArr);
	string imageLocation(imageLocationArr);
	string recognizerLocation(recognizerLocationArr);

	env->ReleaseStringUTFChars(_filesLocation, filesLocationArr);
	env->ReleaseStringUTFChars(_imageLocation, imageLocationArr);
	env->ReleaseStringUTFChars(_recognizerLocation, recognizerLocationArr);
	// Don't reload images we have already loaded
	map<string, Mat> images;
	// Don't reload recommenders we have already loaded
	map<string, Ptr<FaceRecognizer> > recognizers;

	int rectCount = env->GetArrayLength(_rectangles);

	//for all rectangles
	for (int j=0; j<rectCount; j++) {
		jobject rectangleObject = env->GetObjectArrayElement(_rectangles, j);
		jstring imageIDString = (jstring)env->CallObjectMethod(rectangleObject, rectGetImageIDMethod);
		const char *strReturn = env->GetStringUTFChars(imageIDString, 0);
		string imageID(strReturn);
		env->ReleaseStringUTFChars(imageIDString, strReturn);
		Mat currentImage;

		string imagesFileName(filesLocation + imageLocation + imageID);
		if (ifstream(imagesFileName.c_str())){
			if ( images.find(imageID) == images.end() ) {
			  // Load image
				images[imageID] = imread(imagesFileName, CV_LOAD_IMAGE_GRAYSCALE);
			}
		}
		currentImage = images[imageID];
		if( currentImage.empty() )
		{
			cout <<" --(!) No image found - " << filesLocation << imageLocation << imageID << " !" << endl;
		}
		else
		{
			jstring userIDString = (jstring)env->CallObjectMethod(rectangleObject, rectGetUserIDMethod);

			const char *userStrReturn = env->GetStringUTFChars(userIDString, 0);
			string userID(userStrReturn);
			env->ReleaseStringUTFChars(userIDString, userStrReturn);
			if(userID != ""){
				// Load the recognizer for this user, create one if none exists
				Ptr<FaceRecognizer> currentRecognizer = createLBPHFaceRecognizer();
				if ( recognizers.find(userID) == recognizers.end() ) {
				  // Load image
					string recognizerFileName(filesLocation + recognizerLocation + userID);
					if (ifstream(recognizerFileName.c_str())){
						cout << "Loaded recognizer: " << recognizerFileName << endl;
						currentRecognizer->load(recognizerFileName);
					}
					else{
						cout << "No recognizer for: " << userID << endl;
					}

				}
				else{
					currentRecognizer = recognizers[userID];
				}
				// We have a valid image, get the rectangle and process the image
				int x1  = env->GetIntField(rectangleObject, rectX1);
				int y1  = env->GetIntField(rectangleObject, rectY1);
				int x2  = env->GetIntField(rectangleObject, rectX2);
				int y2  = env->GetIntField(rectangleObject, rectY2);
				Mat croppedImage(currentImage, Rect(x1, y1, abs(x2-x1), abs(y2-y1)));

				vector<Mat> newImages;
				vector<int> newLabels;
				newImages.push_back(croppedImage);
				newLabels.push_back(1);
				currentRecognizer->update(newImages,newLabels);
				recognizers[userID] = currentRecognizer;
			}
		}
	}
	// We are done updating all the recognizers, save them.
	for(auto &ent : recognizers) {
		// ent.first is user id (name of recognizer)
		// ent.second is the recognizer
		cout << "Saving " << filesLocation << recognizerLocation << ent.first << endl;
		ent.second->save(filesLocation + recognizerLocation + ent.first);
	}
	// Return true means we completed without errors
	return true;
}
