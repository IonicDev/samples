#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path

#NOTE: You MUST have the input file present before running this:
INPUT_FILE_PATH = "example_file.docx"
OUTPUT_FILE_PATH = "example_file_encrypted.docx"

def get_agent():
  # Load a plaintext Device Profile, or SEP, from disk if on Linux (e.g., no default persistor).
  persistor = None
  if platform.system == 'Linux':
    SEP_FILE_PATH = os.path.expanduser("~/.ionicsecurity/profiles.pt")
    persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(SEP_FILE_PATH)
    print("A plaintext SEP will be loaded from {0}".format(SEP_FILE_PATH))

  # Initialize the Ionic Agent (must be done before most Ionic operations).
  agent = ionicsdk.Agent(None, persistor)

  # Check if there are profiles and that there is one set as active.
  if not agent.hasanyprofiles() or not agent.hasactiveprofile():
    if not agent.hasanyprofiles():
      print("There are no device profiles on this device.")
    if not agent.hasactiveprofile():
      print("There is not an active device profile selected on this device.")
    print("Register (and select an active profile) this device before continuing.")
    sys.exit(-1)

  return agent

if __name__ == "__main__":
  agent = get_agent()
  if agent is None:
    sys.exit(-1)

  # Create a classification (or any other key attributes to set)
  key_attrs = {"classification": "Restricted"}

  # Encrypt the file using the classification
  cipher = ionicsdk.FileCipherAuto(agent)
  try:
    cipher.encrypt(INPUT_FILE_PATH, OUTPUT_FILE_PATH, attributes=key_attrs)
    #NOTE: Due to the format of CSV files, FileCipherAuto can't automatically detect if a file should be processed
    # as a CSV file vs a generic file format. To handle this, use the FileCrypto.getinfo capability to determine it isn't
    # another type of file, and then check the input filename's extension to match "csv" and use a FileCipherCsv cipher.
  except ionicsdk.exceptions.IonicException as e:
    print("Error encrypting: {0}".format(e.message))
    sys.exit(-2)

  print("Encrypted file {0} to {1}.".format(INPUT_FILE_PATH, OUTPUT_FILE_PATH))
