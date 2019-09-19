#include <ISAgent.h>
#include <ISChunkCrypto.h>

#include <iostream>
#include <iomanip>

#include <chrono>
class Timer
{
public:
	Timer() : beg_(clock_::now()) {}
	void reset() { beg_ = clock_::now(); }
	double elapsed() const {
		return std::chrono::duration_cast<second_>
			(clock_::now() - beg_).count();
	}

private:
	typedef std::chrono::high_resolution_clock clock_;
	typedef std::chrono::duration<double, std::ratio<1> > second_;
	std::chrono::time_point<clock_> beg_;
};

// Simple benchmarking program to compare different ways to initialize an Agent
// verses several uses so you can compare the relative 'heft' of each.
// Accumulates data and prints results so far at increasing intervals so you
// don't have to wait for it to complete to get an idea of the trend.
// More information here: https://ionic.com/analysis-managing-machina-sdk-agents
int main()
{
	// initialize our persistant agent
	ISAgent agent;
	agent.initialize();
	ISAgentDeviceProfile profile = agent.getActiveProfile();

	// set up a disk-based persistor to try
	ISAgentDeviceProfilePersistorPassword passwordPersistor;
	passwordPersistor.setFilePath("temp.deleteme");
	passwordPersistor.setPassword("password");
	passwordPersistor.saveAllProfiles(agent.getAllProfiles(), agent.getActiveProfile().getDeviceId());

	// set up our key to fetch (We don't need to be creating millions of keys just to performance test)
	ISAgentCreateKeysResponse createResponse;
	agent.createKey(createResponse);
	std::string keyId = createResponse.getKeys()[0].getId();
	ISAgentGetKeysResponse getResponse;

	// set up our encrypted string to decrypt
	ISChunkCryptoCipherAuto chunk(agent);
	std::string plain = "Hello World!";
	std::string cipher;
	chunk.encrypt(plain, cipher);

	Timer t;
	static const int initTypeCount = 4;
	static const int testTypeCount = 3;
	double times[initTypeCount][testTypeCount] = { 0 };

	static const int testBlockSize = 9;

	int totalTestCount = 0;
	
	// Incrementally generate more data, so we don't have to wait forever to see a trend
	for (int j = 9; j <= 100000; j *= 10)
	{
		for (int i = 0; i < j; i += testBlockSize)
		{
			totalTestCount += testBlockSize;
			// We want to rotate through the different tests repeatedly instead of doing them all at once
			// It's overly complicated, but I wanted to isolate network response time behavior
			for (int pickTest = 0; pickTest < initTypeCount * testTypeCount; pickTest++)
			{
				int initType = pickTest % initTypeCount;
				int testType = pickTest / initTypeCount;

				// AAAnd we want to do a block of tests at a time so we're not resetting the timer too often
				t.reset();
				for (int testInc = 0; testInc < testBlockSize; testInc++)
				{

					ISAgent* a = NULL;
					switch (initType)
					{
					case 0:
						a = &agent;
						break;
					case 1:
						a = new ISAgent();
						a->initializeWithoutProfiles();
						a->addProfile(profile, true);
						break;
					case 2:
						a = new ISAgent();
						a->initialize();
						break;
					case 3:
						a = new ISAgent();
						a->initialize(passwordPersistor);
						break;
					}

					ISAgentDeviceProfile* prof = NULL;
					switch (testType)
					{
					case 0:
						prof = agent.getDeviceProfileForKeyId("notakey");
						// Something with side effects to make sure it doesn't optimize away
						prof->setName(prof->getName());
						break;
					case 1:
						agent.getKey(keyId, getResponse);
						break;
					case 2:
						ISChunkCryptoCipherAuto c(agent);
						std::string recovered;
						c.decrypt(cipher, recovered);
						break;
					}

					// cleanup
					switch (initType)
					{
					case 1:
					case 2:
					case 3:
						delete a;
						break;
					default:
						break;
					}
				}
				times[initType][testType] += t.elapsed();
			}
		}

		// Print out latest results
		std::cout << std::endl;
		std::cout << std::setw(6) << totalTestCount << " tests   ";
		std::cout << "   find     getKey   chunk" << std::endl;

		for (int initType = 0; initType < initTypeCount; initType++)
		{
			switch (initType)
			{
			case 0: std::cout << "Single agent:   "; break;
			case 1: std::cout << "Cached profile: "; break;
			case 2: std::cout << "OS persistor:   "; break;
			case 3: std::cout << "Password file:  "; break;
			default: std::cout << "Test type count error!"; break;
			}
			for (int testType = 0; testType < testTypeCount; testType++)
			{
				std::cout << std::fixed << std::setw(8) << std::setprecision(3) << times[initType][testType] << " ";
			}
			std::cout << std::endl;
		}
	}
}