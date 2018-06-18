# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

import os
import sys
import json
import ionicsdk
import binascii

message = "Hello World!"

# helpers for logging:
linenumber = lambda: currentframe().f_back.f_lineno
filename = lambda: getframeinfo(currentframe()).filename

# initialize logger
config = {
  "sinks": [
    {
      "channels": ["ISAgent"],
      "filter": {"type": "Severity", "level": "Info"},
      "writers": [{"type": "Console"}]
    },
    {
      "channels": ["ISAgent"],
      "filter": {"type": "Severity", "level": "Debug"},
      "writers": [{"type": "File", "filePattern": "sample.log"}]
    }
  ]
}
config_json = json.dumps(config)
ionicsdk.log.setup_from_config_json(config_json)

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

# initialize chunk cipher object
cipher = ionicsdk.ChunkCipherAuto(agent)

# encrypt
ciphertext = cipher.encryptbytes(message)

# decrypt
plaintext = cipher.decryptbytes(ciphertext)

# display data
print("Ciphertext : " + ciphertext)
print("Plaintext  : " + plaintext)
