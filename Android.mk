#
# Copyright (C) 2016 Fairphone B.V.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

#
# Build app code.
#
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional \
	tests \

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main/java) 

LOCAL_JAVA_LIBRARIES := android-common\

LOCAL_STATIC_JAVA_LIBRARIES :=  \
  android-support-v7-appcompat \
  android-support-v4 \
	
LOCAL_MANIFEST_FILE = app/src/main/AndroidManifest.xml
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat

LOCAL_RESOURCE_DIR := \
                  frameworks/support/v7/appcompat/res \
                  $(LOCAL_PATH)/app/src/main/res

LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := Checkup

LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := app/proguard-rules.pro

include $(BUILD_PACKAGE)

#
# Add prebuilt jar
#
include $(CLEAR_VARS)

include $(BUILD_MULTI_PREBUILT)
