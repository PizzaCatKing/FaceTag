#include <jni.h>
#include <stdio.h>
#include "faceTag_recognizer_RecognizerInterface.h"
#include <string.h>
#include <iostream>
#include <windows.h>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/contrib/contrib.hpp>



using namespace cv;
using namespace std;

JNIEXPORT jobjectArray JNICALL Java_faceTag_recognizer_RecognizerInterface_detectForRectangles(JNIEnv *env , jobject thisObj, jstring _filesLocation, jstring _imageID){

	const char * filesLocationArr;
	filesLocationArr = env->GetStringUTFChars(_filesLocation, 0);
	const char * imageIDArr;
	imageIDArr = env->GetStringUTFChars(_imageID, 0);

	string filesLocation(filesLocationArr);
	string imageID(imageIDArr);
	CascadeClassifier face_cascade;
	string face_cascade_name = "haarcascade_frontalface_alt.xml";



	Mat image = imread(filesLocation + "images\\" + imageID, CV_LOAD_IMAGE_GRAYSCALE);
	if( !face_cascade.load( filesLocation + face_cascade_name ) ){ printf("--(!)Error loading face cascade\n"); cin.ignore(); return NULL; };

	std::vector<Rect> faces;
	equalizeHist( image, image );

	//-- Detect faces
	face_cascade.detectMultiScale( image, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );
	cout << "Found " << faces.size() << " faces!" << endl;

	jclass rectClass = env->FindClass("faceTag/entities/Rectangle");
	jmethodID rectConstructor = env->GetMethodID(rectClass, "<init>", "(IIII)V");


	jobjectArray jRectArray = env->NewObjectArray(faces.size(), rectClass, NULL);

	for(int i =0; i< faces.size(); i++){
		jobject newRect = env->NewObject(rectClass, rectConstructor, faces[i].x, faces[i].y, faces[i].x + faces[i].width, faces[i].y + faces[i].height);
		 env->SetObjectArrayElement(jRectArray, i, newRect);
	}

	env->ReleaseStringUTFChars(_filesLocation, filesLocationArr);
	env->ReleaseStringUTFChars(_imageID, imageIDArr);
	return jRectArray;
}
