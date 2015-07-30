# Define a variable for classpath
CLASS_PATH = ..\..\..\target\classes\

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all : faceTag_recognizer_RecognizerInterface.dll

# $@ matches the target, $< matches the first dependancy
faceTag_recognizer_RecognizerInterface.dll : faceTag_recognizer_RecognizerInterface.o
	g++ -o $@ $< -m64 -Wl,--add-stdcall-alias -shared -L"C:\Code\openCV\opencv\build\install\x64\mingw\lib" -lopencv_core2411 -lopencv_imgproc2411 -lopencv_highgui2411 -lopencv_objdetect2411 -lopencv_contrib2411

# $@ matches the target, $< matches the first dependancy
faceTag_recognizer_RecognizerInterface.o : faceTag_recognizer_RecognizerInterface.cpp faceTag_recognizer_RecognizerInterface.h
	g++ -c $< -o $@ -m64 -I"C:\Program Files\Java\jdk1.8.0_51\include" -I"C:\Program Files\Java\jdk1.8.0_51\include\win32" -I"C:\Code\openCV\opencv\build\install\include"

# $* matches the target filename without the extension
faceTag_recognizer_RecognizerInterface.h : 
	javah -d . -classpath $(CLASS_PATH) faceTag.recognizer.RecognizerInterface
		
clean :
	rm faceTag_recognizer_RecognizerInterface.h faceTag_recognizer_RecognizerInterface.o faceTag_recognizer_RecognizerInterface.dll