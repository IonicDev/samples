PROG1 = chunk-decrypt
IONIC_SDK_PATH?=/Users/morgan/Documents/IonicSDK_c_1_0_0
CC=gcc
OBJS_C=chunk-decrypt.c
CFLAGS= -x c -Wall -ggdb -I. -I include/

UNAME_S=$(shell uname -s)
ifeq ($(UNAME_S),Linux)
	SDKDIR = $(IONIC_SDK_PATH)/IonicSDK_c_Linux_1_0_0_Default_glibc217
	LDFLAGS= \
	$(SDKDIR)/Lib/Linux/Release/x86_64/libISAgentSDKC.so \
      -lcurl -lpthread
	CFLAGS+= -I $(SDKDIR)/Include/
else
	SDKDIR = $(IONIC_SDK_PATH)/IonicSDK_c_OSX_1_0_0
	LDFLAGS= \
	  -dynamiclib $(SDKDIR)/Lib/MacOSX/Debug/universal/libISAgentSDKC.dylib \
	  -framework CoreFoundation -framework CoreServices \
	  -framework IOKit -framework Foundation -framework Security
	CFLAGS+= -I $(SDKDIR)/Include/ -fvisibility-inlines-hidden
endif


$(PROG1): $(OBJS_C)
	#$(CC) $(CFLAGS) $@ $^ $(LDFLAGS)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@



clean:
	rm -f *~
	rm -f *.o
	rm -f $(PROG1)



