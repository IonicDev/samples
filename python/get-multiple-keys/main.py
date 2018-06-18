# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import json
import ionicsdk
import binascii

keyId1 = "HVzG5uKl3yE"
keyId2 = "HVzG3AJoHQU"
keyId3 = "HVzG52Kj3to"

# read persistor password from environment variable
persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistorPassword == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with password persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# get multiple keys
try:
    keys = agent.getkeys([keyId1, keyId2, keyId3])
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display fetched keys
for key in keys:
    print('---')
    print("KeyId        : " + key.id)
    print("KeyBytes     : " + binascii.hexlify(key.bytes))
    print("FixedAttrs   : " + json.dumps(key.attributes))
    print("MutableAttrs : " + json.dumps(key.mutableAttributes))
