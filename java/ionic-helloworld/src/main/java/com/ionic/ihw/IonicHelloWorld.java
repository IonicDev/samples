/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package ionic.hw;

import com.ionic.sdk.error.SdkException;
import com.ionic.sdk.error.AgentErrorModuleConstants;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherV2;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;

public class IonicHelloWorld
{
    public static void main(String[] args)
    {
        try {
            // Initialize Ionic agent
            Agent isAgent = new Agent();
            String profilePath = System.getproperty("user.home") + "/.ionicsecurity/profiles.pt";
            DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(profilePath);
            isAgent.initialize(ptPersistor);

            // Encrypt and decrypt string "hello world"
            ChunkCipherV2 cipher = new ChunkCipherV2(isAgent);
            String cipherText = cipher.encrypt("hello world");
            String plainText = cipher.decrypt(cipherText);
            System.out.println("Plain Text: " + plainText);
            System.out.println("Ionic Chunk Encrypted Text: " + cipherText);

        } catch (SdkException e) {
            System.out.println(e);
            if(e.getReturnCode() == AgentErrorModuleConstants.ISAGENT_NO_DEVICE_PROFILE.value()) {
                System.out.println("Profile does not exist");
            }
            if(e.getReturnCode() == AgentErrorModuleConstants.ISAGENT_KEY_DENIED.value()) {
                System.out.println("Key request denied");
            }
        }
    }
}
