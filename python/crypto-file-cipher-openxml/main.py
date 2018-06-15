# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import ionicsdk

file_original = '../../sample-data/files/Message.docx'
file_ciphertext = './Message-Protected.docx'
file_plaintext = './Message.docx'

# initialize agent with password persistor
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
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
cipher = ionicsdk.FileCipherOpenXml(agent)

# encrypt
cipher.encrypt(file_original, file_ciphertext, mutableAttributes=mutable_attributes)

# decrypt
cipher.decrypt(file_ciphertext, file_plaintext)
