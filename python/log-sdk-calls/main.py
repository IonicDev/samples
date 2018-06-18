#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path
import json
from inspect import currentframe, getframeinfo

MY_APPLICATION_CHANNEL_NAME = "SampleLoggingApplication"
INPUT_STRING = "Hello World!"


# Helpers for logging:
linenumber = lambda: currentframe().f_back.f_lineno
filename = lambda: getframeinfo(currentframe()).filename

# Logging setup function:
def setup_logging():
  #NOTE: Depending on the severity and channel name, this can cause many of the Ionic internal SDK actions to be logged.
  #	This is made available as it is useful for debugging, but you may want to filter on your channel instead
  #	of "ISAgent" to see content only from your application.

  config = {"sinks": [
              {"channels": ["ISAgent", MY_APPLICATION_CHANNEL_NAME],
               "filter": {"type": "Severity", "level": "Info"},
               "writers": [
                  {"type": "Console"}
               ]
              },
              {"channels": ["ISAgent", MY_APPLICATION_CHANNEL_NAME],
               "filter": {"type": "Severity", "level": "Debug"},
               "writers": [
                  {"type": "File", "filePattern": "sample.log"}
               ]
              }
           ]}
  config_json = json.dumps(config)
  ionicsdk.log.setup_from_config_json(config_json)


if __name__ == "__main__":
  setup_logging()

  # Load a plaintext Device Profile, or SEP, from disk if on Linux (e.g., no default persistor).
  persistor = None
  if platform.system == 'Linux':
    SEP_FILE_PATH = os.path.expanduser("~/.ionicsecurity/profiles.pt")
    persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(SEP_FILE_PATH)
    ionicsdk.log.log(ionicsdk.log.SEV_INFO, MY_APPLICATION_CHANNEL_NAME, linenumber(), filename(),
                     "A plaintext SEP will be loaded from {0}".format(SEP_FILE_PATH))

  # Initialize the Ionic Agent (must be done before most Ionic operations).
  agent = ionicsdk.Agent(None, persistor)

  # Check if there are profiles.
  if not agent.hasanyprofiles():
    ionicsdk.log.log(ionicsdk.log.SEV_ERROR, MY_APPLICATION_CHANNEL_NAME, linenumber(), filename(),
                     "There are no device profiles on this device. Register a device before continuing.")
    sys.exit(-1)

  # Initialize a Chunk Cipher for doing string encryption
  cipher = ionicsdk.ChunkCipherAuto(agent)

  # Encrypt the string using an Ionic-managed Key
  try:
    ciphertext = cipher.encryptstr(INPUT_STRING)
  except ionicsdk.exceptions.IonicException as e:
    ionicsdk.log.log(ionicsdk.log.SEV_ERROR, MY_APPLICATION_CHANNEL_NAME, linenumber(), filename(),
                     "Error encrypting: {0}".format(e.message))
    sys.exit(-2)

  print("Plain Text: " + INPUT_STRING)
  print("Ionic Chunk Encrypted Text: " + ciphertext)
