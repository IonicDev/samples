# (c) 2018-2020 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk
import binascii

persistor_path = '../../sample-data/persistors/sample-persistor.aes'
persistor_key = binascii.unhexlify('A0444B8B5A7209780823617A98986831B8240BAA851A0B1696B0329280286B17')
persistor_auth_data = b'persistor auth data'
source_dir = 'python/initialize-agent-with-aes-persistor'
this_dir = os.getcwd()

# run only from source directory
if not this_dir.endswith(source_dir):
    print("[!] Please run this sample from inside " + source_dir)
    sys.exit(1)

# initialize agent with aes persistor
try:
    persistor = ionicsdk.DeviceProfilePersistorAesGcmFile(persistor_path, persistor_key, persistor_auth_data)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# display all profiles in persistor
profiles = agent.getallprofiles()
for profile in profiles:
    print("---")
    print("Id       : " + profile.deviceid)
    print("Name     : " + profile.name)
    print("Keyspace : " + profile.keyspace)
    print("ApiUrl   : " + profile.server)
