# (c) 2018 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import os
import sys
import json
import binascii
import ionicsdk

# Read persistor password from environment variable.
persistorPassword = os.environ.get('IONIC_PERSISTOR_PASSWORD')
if persistorPassword == None:
    print("[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD")
    sys.exit(1)

# Initialize agent with password persistor.
try:
    persistorPath = os.path.expanduser("~/.ionicsecurity/profiles.pw")
    persistor = ionicsdk.DeviceProfilePersistorPasswordFile(persistorPath, persistorPassword)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# Create a resource request for "classification" marking values
# configured in the dashboard.
resource_id = "marking-values"
args = "classification"

# Fetch the resource "classification" marking values.
try:
    resource_resp = agent.getresource(resource_id, args)
except ionicsdk.exceptions.IonicException as e:
    print("Error getting a resource: {0}".format(e.message))
    sys.exit(-2)

# Check for errors.
if resource_resp.error is not None:
    print("Resource Error: {0}".format(resource_resp.error))
    sys.exit(-2)

print("Classification values:")
print("Data   : {0}".format(resource_resp.data))
