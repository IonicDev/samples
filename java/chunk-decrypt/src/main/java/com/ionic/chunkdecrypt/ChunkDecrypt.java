/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.chunkdecrypt;
import com.ionic.sdk.error.SdkException;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherV2;
import com.ionic.sdk.agent.cipher.chunk.ChunkCipherAuto;

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
    Agent agent = new Agent();
    try {
      String profilePath = System.getProperty("user.home") + "/.ionicsecurity/profiles.pt";
      DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(profilePath);
      agent.initialize(ptPersistor);
    } catch (SdkException e) {
      System.out.println("Failed to initialize agent:");
      System.out.println(e);
      System.exit(1);
    }

    // Encrypt a string using the chunk data format.
    ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);
    try {
      String decryptedText = chunkCrypto.decrypt(encryptedText);
      System.out.println("Chunk-Encrypted String: " + encryptedText);
      System.out.println("Decrypted String: " + decryptedText);
    } catch (SdkException e) {
      System.out.println("Failed to decrypt:");
      System.out.println(e);
      System.exit(1);
    }
  }
}
