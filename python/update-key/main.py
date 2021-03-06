# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import ionicsdk
import binascii

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

# create key
try:
    key = agent.createkey()
except ionicsdk.exceptions.IonicException as e:
    print("Error creating a key: {}".format(e.message))
    sys.exit(-2)

# display key
print("KeyId        : " + key.id)
print("KeyBytes     : " + binascii.hexlify(key.bytes).decode("ascii"))
print("FixedAttrs   : " + json.dumps(key.attributes))
print("MutableAttrs : " + json.dumps(key.mutableAttributes))
print('-' * 20 + '\n')

# create mutable attribute
add_mutable_attributes = {
    "classification": ["Highly Restricted"]
}

# update mutable attributes
key.mutableAttributes = add_mutable_attributes
key.forceUpdate = 1

# update key
try:
    updatedKey = agent.updatekey(key)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display updated key
print("Updated KeyId        : " + updatedKey.id)
print("Updated KeyBytes     : " + binascii.hexlify(updatedKey.bytes).decode("ascii"))
print("Updated FixedAttrs   : " + json.dumps(updatedKey.attributes))
print("Updated MutableAttrs : " + json.dumps(updatedKey.mutableAttributes))
print('-' * 20 + '\n')
