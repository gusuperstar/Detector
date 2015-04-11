LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
OPENCV_CAMERA_MODULES:=on 
OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=STATIC

include ..\sdk\native\jni\OpenCV.mk 
TARGET_PLATFORM := android-3
LOCAL_MODULE := zzz
LOCAL_SRC_FILES := zzz.cpp SerialPort.c
LOCAL_LDLIBS += -llog -ldl

#TARGET_PLATFORM := android-3
#LOCAL_MODULE    := serial_port
#LOCAL_SRC_FILES := SerialPort.c
#LOCAL_LDLIBS    := -llog
 
include $(BUILD_SHARED_LIBRARY)