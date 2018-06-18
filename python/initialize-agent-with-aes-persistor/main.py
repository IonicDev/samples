# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk
import binascii

persistorPath = '../../sample-data/persistors/sample-persistor.aes'
persistorKey = binascii.unhexlify('A0444B8B5A7209780823617A98986831B8240BAA851A0B1696B0329280286B17')
persistorAuthData = 'persistor auth data'

# initialize agent with aes persistor
try:
    persistor = ionicsdk.DeviceProfilePersistorAesGcmFile(persistorPath, persistorKey, persistorAuthData)
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
