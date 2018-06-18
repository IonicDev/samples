# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk

profile_id = 'ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj'
persistor_path = '../../sample-data/persistors/sample-persistor.pt'

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

# list all profiles
try:
    profiles = agent.getallprofiles()
    print("Profiles:")
    for profile in profiles:
        print(profile.deviceid)
except ionicsdk.exceptions.IonicException as e:
    print("Error getting all profiles: {0}".format(e.message))
    sys.exit(-2)

# change active profile
try:
    agent.setactiveprofile(profile_id)
    active_profile = agent.getactiveprofile()
except ionicsdk.exceptions.IonicException as e:
    print("Error changing active profile: {0}".format(e.message))
    sys.exit(-2)

# display agent active profile
print("\nActiveProfile:")
print("Id       : " + active_profile.deviceid)
print("Name     : " + active_profile.name)
print("Keyspace : " + active_profile.keyspace)
print("ApiUrl   : " + active_profile.server)