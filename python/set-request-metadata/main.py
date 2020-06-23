# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import binascii
import ionicsdk

key_id = 'HVzG52Kj3to'

# read persistor password from environment variable
persistor_password = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if (persistor_password == None):
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# initialize agent with password persistor
try:
    persistor_path = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistor_path, persistor_password)
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
    key = agent.getkey(key_id, metadata=request_metadata)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching key: {0}".format(e.message))
    sys.exit(-2)

# display fetched key
print("KeyId        : " + key.id)
print("KeyBytes     : " + binascii.hexlify(key.bytes).decode("ascii"))
print("FixedAttrs   : " + json.dumps(key.attributes))
print("MutableAttrs : " + json.dumps(key.mutableAttributes))
