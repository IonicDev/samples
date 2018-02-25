/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.ChunkCipherAuto;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;

public class ChunkDecrypt
{
  public static void main(String[] args)
  {
    if (args.length != 1) {
      System.out.println("Usage args: [chunkFormatCipherText]");
      return;
    }
    String encryptedText = args[0];

    // Setup an agent object to talk to Ionic
    AgentSdk.initialize(null);
    Agent agent = new Agent();

    if (System.getProperty("os.name").startsWith("Linux")) {
      DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText();
      String sProfilePath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
      ptPersistor.setFilePath(sProfilePath);
      agent.initialize(ptPersistor);
    } else {
      agent.initialize();
    }

    // Encrypt a string using the chunk data format.
    ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);
    String decryptedText = chunkCrypto.decrypt(encryptedText, null);

    System.out.println("Chunk-Encrypted String: " + encryptedText);
    System.out.println("Decrypted String: " + decryptedText);
  }
}
