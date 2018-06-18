# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import copy
import ionicsdk
import binascii

# TODO: provide key to update
key_id = None

if (key_id == None):
    print("Please set the 'key_id' variable to a key you have already created")
    sys.exit(1)

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

# define new mutable attributes
new_mutable_attributes = {
    "classification": ["Highly Restricted"]
}

# fetch key
try:
    key = agent.getkey(key_id)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# merge new and existing mutable attributes
updated_attributes = copy.copy(dic(key.mutableAttributes))
for key,value in new_mutable_attributes.items():
    updated_attributes[key] = value

# update key
try:
    key = agent.updatekey(key_id, updated_attributes)
except ionicsdk.exceptions.IonicException as e:
    print("Error fetching a key: {0}".format(e.message))
    sys.exit(-2)

# display updated key
print("KeyId        : " + key.id)
print("KeyBytes     : " + binascii.hexlify(key.bytes))
print("FixedAttrs   : " + json.dumps(key.attributes))
print("MutableAttrs : " + json.dumps(key.mutableAttributes))
