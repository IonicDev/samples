# (c) 2018-2019 Ionic Security Inc.
# By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
# and the Privacy Policy (https://www.ionic.com/privacy-notice/).

from __future__ import print_function

import sys
import ionicsdk

# initialize agent with no persistor
try:
    agent = ionicsdk.Agent(loadprofiles=False)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# display all profiles
profiles = agent.getallprofiles()
print("Initialized agent with {} profiles".format(len(profiles)))
