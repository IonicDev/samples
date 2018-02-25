/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;
import com.ionicsecurity.sdk.ChunkCipherAuto;

public class IonicHelloWorld 
{
    public static void main(String[] args) 
    {
        // Set input string
        String str = "Hello World!";

        // Initialize sdk (calls system load from platform-specific objects)
        AgentSdk.initialize(null);

        // Initialize Ionic agent
        Agent agent = new Agent();
        if (System.getProperty("os.name").startsWith("Linux")) {
            // Load a plain-text device profile (SEP) from disk if on Linux:
            DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText();
            String sProfilePath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
            ptPersistor.setFilePath(sProfilePath);
            agent.initialize(ptPersistor);
        } else {
            // On platforms with a default SEP persistor:
            agent.initialize();
        }

        // Initialize chunk cipher
        ChunkCipherAuto chunkCipher = new ChunkCipherAuto(agent);

        // Encrypt simple string
        String ciphertext = chunkCipher.encrypt(str);

        // Output results
        System.out.println("Plain Text: " + str);
        System.out.println("Ionic Chunk Encrypted Text: " + ciphertext);
    }
}
