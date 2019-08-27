# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import ionicsdk

message = 'secret message requiring secrect clearance'

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

# set appplication metadata
agent.setmetadata({
    "ionic-application-name": "Policy-test Tutorial",
    "ionic-application-version": "1.0.0"
})

# define data markings
data_markings = {
    "clearance-level": ["secret"]
}

# initialize chunk cipher object
cipher = ionicsdk.ChunkCipherAuto(agent)

# encrypt
try:
   ciphertext = cipher.encryptstr(message, attributes=data_markings)
except ionicsdk.exceptions.IonicException as e:
    print("Error encrypting plaintext: {0}".format(e.message))
    sys.exit(1)

# decrypt
try:
   plaintext = cipher.decryptstr(ciphertext)
except ionicsdk.exceptions.IonicException as e:
    print("Error decrypting ciphertext: {0}".format(e.message))
    print("You don't have the correct clearance.")
    print("")
    sys.exit(1)

# display data
print("Ciphertext: " + ciphertext)
print("Plaintext : " + plaintext)
print("")
