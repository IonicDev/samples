PROG1 = chunk-decrypt
IONIC_SDK_PATH?=~/Documents/
SDKDIR=$(IONIC_SDK_PATH)/ISAgentSDKC/
CC=gcc
OBJS_C=chunk-decrypt.c
#CFLAGS= -x c -Wall -ggdb -I. -I include/
CFLAGS=-I . -std=gnu99
POSTBUILD=

UNAME_S=$(shell uname -s)
ifeq ($(UNAME_S),Linux)
	#SDKDIR = $(IONIC_SDK_PATH)/IonicSDK_c_Linux_1_0_0_Default_glibc217
	LDFLAGS= \
	  $(SDKDIR)/Lib/Linux/Release/x86_64/libISAgentSDKC.so \
      	  -lcurl -lpthread
	CFLAGS+= -I $(SDKDIR)/Include/
else
	#SDKDIR = $(IONIC_SDK_PATH)/IonicSDK_c_OSX_1_0_0
	DYLIB=$(SDKDIR)/Lib/MacOSX/Release/universal/libISAgentSDKC.dylib
	POSTBUILD=cp $(DYLIB) .
	LDFLAGS= \
          -rpath @executable_path/ \
	  $(DYLIB) \
	  -framework CoreFoundation -framework CoreServices \
	  -framework IOKit -framework Foundation -framework Security
	CFLAGS+= -I $(SDKDIR)/Include/ -fvisibility-inlines-hidden
endif


$(PROG1): $(OBJS_C)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@
	$(POSTBUILD)

clean:
	rm -f *~
	rm -f *.o
	rm -f $(PROG1)


