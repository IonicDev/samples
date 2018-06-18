import sys
import ionicsdk

persistorPath = '../../sample-data/persistors/sample-persistor.pt'

# initialize agent with plaintext persistor
try:
    persistor = ionicsdk.DeviceProfilePersistorPlaintextFile(persistorPath)
    agent = ionicsdk.Agent(None, persistor)
except ionicsdk.exceptions.IonicException as e:
    print("Error initializing agent: {0}".format(e.message))
    sys.exit(-2)

# display all profiles in persistor
profiles = agent.getallprofiles()
for profile in profiles:
    print("---")
    print("Id       : " + profile.deviceid)
    print("Name     : " + profile.name)
    print("Keyspace : " + profile.keyspace)
    print("ApiUrl   : " + profile.server)