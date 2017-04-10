/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.AutoFileCipher;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;

public class FileCryptoAutoDecrypt
{
  public static void main(String[] args)
  {
    if (args.length != 2) {
      System.out.println("Usage args: [pathToCiphertext] [pathToTargetPlaintext]");
      return;
    }

    //Change these to actual locations.
    String pathToEncryptedFile = args[0];
    String pathToDecryptedFile = args[1];

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
    
    AutoFileCipher fileCipher = new AutoFileCipher(agent);
    fileCipher.decrypt(pathToEncryptedFile, pathToDecryptedFile);
    
  }
}
