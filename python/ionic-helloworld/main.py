# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk

input_string = "Hello, World!"

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
    sys.exit(1)

# check if there are profiles.
if not agent.hasactiveprofile():
    print("There is not an active device profile selected on this device.")
    print("Register (and select an active profile) this device before continuing.")
    sys.exit(1)

# define data markings
data_markings = {
    "clearance-level": ["secret"]
}

# initialize a Chunk Cipher for doing string encryption
cipher = ionicsdk.ChunkCipherAuto(agent)

# encrypt the string using an Ionic-managed Key
try:
    ciphertext = cipher.encryptstr(input_string, attributes=data_markings)
except ionicsdk.exceptions.IonicException as e:
    print("Error encrypting: {0}".format(e.message))
    sys.exit(1)

# Note: Decryption only works if the policy allows it.
try:
   plaintext = cipher.decryptstr(ciphertext)
except ionicsdk.exceptions.IonicException as e:
    print("Error decrypting ciphertext: {0}".format(e.message))
    print("You don't have the correct clearance.")
    print("")
    sys.exit(1)

print("")
print("Input: " + input_string)
print("Ionic Chunk Encrypted Ciphertext: " + ciphertext)
print("Plaintext: " + plaintext)
print("")
