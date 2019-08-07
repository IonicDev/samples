# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk

persistor_path = '../../sample-data/persistors/sample-persistor.pt'
source_dir = 'github-samples/python/initialize-agent-with-plaintext-persistor'
this_dir = os.getcwd()

# run only from source directory
if not this_dir.endswith(source_dir): 
    print("[!] Please run this sample from inside " + source_dir)
    sys.exit(1)

# initialize agent with plaintext persistor
try:
    persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(persistor_path)
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
