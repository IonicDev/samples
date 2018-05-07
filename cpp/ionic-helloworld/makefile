PROJECT=$(lastword $(subst /, ,$(CURDIR)))
PROJECT_TYPE=exe
SDKDIR?=$(IONIC_SDK_PATH)/ISAgentSDK/
SRCDIR?=src/
OBJDIR?=obj/
CFLAGS+= -I . -I include/ -I $(SRCDIR)/ -I $(SDKDIR)/Include/
CXX?=g++

OBJS_CPP=$(subst $(SRCDIR),$(OBJDIR), \
  $(subst .cpp,.o,                    \
    $(wildcard $(SRCDIR)/*.cpp)))

UNAME_S=$(shell uname -s)
ifeq ($(UNAME_S),Linux)
	TGT=linux
	CFLAGS+= -D__STDC_LIMIT_MACROS -D_GLIBCXX_USE_CXX11_ABI=0
	LDFLAGS= \
	  $(SDKDIR)/Lib/Linux/Release/x86_64/libISAgentSDK.a \
	  -lcurl -lpthread -lm -lstdc++
else
	TGT=mac
	LDFLAGS= \
	  $(SDKDIR)/Lib/MacOSX/Release/universal/libISAgentSDK.a \
	  -lstdc++ \
	  -framework CoreFoundation -framework CoreServices \
	  -framework IOKit -framework Foundation -framework Security
	CFLAGS+= -fvisibility-inlines-hidden
endif

ALLDEP=$(PROJECT)_$(TGT)

.PHONY:
all: $(ALLDEP)

$(OBJDIR):
	mkdir -p $(OBJDIR)

$(OBJDIR)/%.o: $(SRCDIR)/%.cpp | $(OBJDIR)
	$(CXX) -fPIC $(CFLAGS) -o $@ -c $^

$(PROJECT)_$(TGT): $(OBJS_CPP)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

.PHONY:
clean:
	rm -f $(PROJECT)_$(TGT) lib$(PROJECT)_$(TGT).so $(OBJS_CPP)
