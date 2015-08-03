# Define a variable for classpath
CLASS_PATH = ..\..\..\target\classes\;..\..\..\target\FaceTag-0.0.1-SNAPSHOT\WEB-INF\lib\mongo-java-driver-2.13.2.jar
JDK_INCLUDE_PATH = C:\Program Files\Java\jdk1.8.0_51\include
JDK_INCLUDE_PATH_DIST = C:\Program Files\Java\jdk1.8.0_51\include\win32
OPENCV_INCLUDE_DIR = C:\Code\openCV\opencv\build\install\include
OPENCV_LIB_DIR = C:\Code\openCV\opencv\build\install\x64\mingw\lib

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

windows : faceTag_recognizer_RecognizerInterface.dll
linux : faceTag_recognizer_RecognizerInterface.a
# $@ matches the target, $< matches the first dependancy
faceTag_recognizer_RecognizerInterface.dll : faceTag_recognizer_RecognizerInterface.o
	g++ -o $@ $< -m64 -std=c++11 -Wl,--add-stdcall-alias -shared -L$(OPENCV_LIB_DIR) -lopencv_core2411 -lopencv_imgproc2411 -lopencv_highgui2411 -lopencv_objdetect2411 -lopencv_contrib2411

faceTag_recognizer_RecognizerInterface.a : faceTag_recognizer_RecognizerInterface.o
	g++ -o $@ $< -m64 -std=c++11 -Wl -shared -L$(OPENCV_LIB_DIR) -lopencv_core -lopencv_imgproc -lopencv_highgui -lopencv_objdetect -lopencv_contrib

# $@ matches the target, $< matches the first dependancy
faceTag_recognizer_RecognizerInterface.o : faceTag_recognizer_RecognizerInterface.cpp faceTag_recognizer_RecognizerInterface.h
	g++ -c $< -o $@ -m64 -std=c++11 -I"$(JDK_INCLUDE_PATH)" -I"$(JDK_INCLUDE_PATH_DIST)" -I"$(OPENCV_INCLUDE_DIR)"

# $* matches the target filename without the extension
faceTag_recognizer_RecognizerInterface.h : 
	javah -d . -classpath $(CLASS_PATH) faceTag.recognizer.RecognizerInterface
		
clean :
	rm faceTag_recognizer_RecognizerInterface.h faceTag_recognizer_RecognizerInterface.o faceTag_recognizer_RecognizerInterface.dll faceTag_recognizer_RecognizerInterface.a