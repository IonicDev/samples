#!/usr/bin/env python

# (c) 2017 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import ionicsdk

import sys
import platform
import os.path


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
  # Add metadata to describe the decryption context
  req_metadata = {"example-metadata-key": "example-metadata-value"}

  # Now ask the server to make those keys:
  try:
    createdKeys = agent.createkeys(2, attributes=key_attrs, metadata=req_metadata)
  except ionicsdk.exceptions.IonicException as e:
    print("Error creating a key: {0}".format(e.message))
    sys.exit(-2)

  # Show us what keys we got (you can always get a key right when you create it):
  fetchRequest = [] # we will use this to track the keys we want to fetch later
  for key in createdKeys:
    # In Python, run `help(ionicsdk.agent.KeyData)` to learn about the key object.
    print("We created a key with the Key Tag: {0}".format(key.id))
    fetchRequest.append(key.id)

  # The rest of this program would typically happen at a different time,
  #  not right after creating the keys, but when you were going to access
  #  the data protected by those keys.

  # Now, using the Key Tags, ask the server for those keys again:
  # NOTE: We populated fetchRequest's list of keytags in the above loop.
  try:
    fetchedKeys = agent.getkeys(fetchRequest, metadata=req_metadata)
  except ionicsdk.exceptions.IonicException as e:
    print("Error fetching keys: {0}".format(e.message))
    sys.exit(-3)

  # Show what we got access to after a request for keys:
  for fetchedKey in fetchedKeys:
    print("We fetched a key with the Key Tag: {0}".format(fetchedKey.id))

  # Tell us if we got less keys when we fetched than we created.
  # This would happen if policy didn't give us access to all the keys.
  if len(fetchedKeys) != len(fetchRequest):
    print("We did not receive all of the requested keys.")
    sys.exit(-4)
