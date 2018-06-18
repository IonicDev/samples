# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import ionicsdk

file_original = '../../sample-data/files/Message.pdf'
file_ciphertext = './Message-Protected.pdf'
file_plaintext = './Message.pdf'

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

# define attributes (optional)
mutable_attributes = {
    "classification": "Restricted"
}   

# initialize aes cipher object
cipher = ionicsdk.FileCipherPdf(agent)

# encrypt
cipher.encrypt(file_original, file_ciphertext, mutableAttributes=mutable_attributes)

# decrypt
cipher.decrypt(file_ciphertext, file_plaintext)
