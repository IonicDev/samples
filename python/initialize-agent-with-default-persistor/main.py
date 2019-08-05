# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import sys
import ionicsdk

# default persistor not supported in Linux
if "linux" in sys.platform:
    print("Error default persistor not supported in Linux")

# initialize agent with default persistor
try:
    agent = ionicsdk.Agent()
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# display all profiles in persistor
if agent.hasanyprofiles():
    for profile in agent.getallprofiles():
        print("---")
        print("Id       : " + profile.deviceid)
        print("Name     : " + profile.name)
        print("Keyspace : " + profile.keyspace)
        print("ApiUrl   : " + profile.server)
else:
    print("There are no device profiles on this device")
