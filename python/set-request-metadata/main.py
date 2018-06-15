# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import json
import binascii
import ionicsdk

keyId = 'ABcd3INKQ7w'

# initialize agent with password persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# define request metadata
request_metadata = {
    "application-state": "active"
}

# get key with request metadata
try:
    key = agent.getkey(keyId, metadata=request_metadata)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching key: {0}".format(e.message))
    sys.exit(-2)

# display fetched key
print("KeyId        : " + key.id)
print("KeyBytes     : " + binascii.hexlify(key.bytes))
print("FixedAttrs   : " + json.dumps(key.attributes))
print("MutableAttrs : " + json.dumps(key.mutableAttributes))
