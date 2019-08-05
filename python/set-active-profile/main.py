# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk

profile_id = 'ABcd.1.48sdf0-cs80-5802-sd80-d8s0df80sdfj'
persistor_path = os.path.join("..", "..", "sample-data", "persistors", "sample-persistor.pt")
source_dir = 'github-samples/python/set-active-profile'
this_dir = os.getcwd()

# run only from source directory
if not this_dir.endswith(source_dir):
    print("[!] Please run this sample from inside " + source_dir)
    sys.exit(1)

# Initialize agent with plaintext persistor.
try:
    persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(persistor_path)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# List all profiles.
try:
    profiles = agent.getallprofiles()
except ionicsdk.exceptions.IonicException as e:
    print("Error getting all profiles: {0}".format(e.message))
    sys.exit(-2)

# Verify there is at least one profile.
if profiles.count == 0:
    print("No profiles for plaintext persistor")
    sys.exit(-2)

# Display all profiles for the persistor.
print("Available Profiles:")
for profile in profiles:
    print(profile.deviceid)

# If the number of profiles is equal to one, then there is nothing to set.
if profiles.count == 1:
    print("Only one profile, nothing to change")
    sys.exit(-2)

# Change active profile.
print("\nSetting '{}' as active profile".format(profile_id))
try:
    agent.setactiveprofile(profile_id)
    active_profile = agent.getactiveprofile()
except ionicsdk.exceptions.IonicException as e:
    print("Error changing active profile: {0}".format(e.message))
    sys.exit(-2)

# Display agent active profile.
print("\nActiveProfile:")
print("Id       : " + active_profile.deviceid)
print("Name     : " + active_profile.name)
print("Keyspace : " + active_profile.keyspace)
print("ApiUrl   : " + active_profile.server)
