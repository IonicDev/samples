/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.AutoFileCipher;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;
import com.ionicsecurity.sdk.FileCryptoEncryptAttributes;
import com.ionicsecurity.sdk.KeyAttributesMap;
import java.util.List;
import java.util.LinkedList;

public class FileCryptoAutoEncrypt
{
  public static void main(String[] args)
  {
    if (args.length != 2) {
      System.out.println("Usage args: [pathToPlaintext] [pathToTargetCiphertext]");
      return;
    }
    String pathToPlainFile = args[0];
    String pathToEncryptedFile = args[1]; 
    
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
    
    FileCryptoEncryptAttributes encryptAttributes = new FileCryptoEncryptAttributes();

    KeyAttributesMap attrMap = new KeyAttributesMap();
    List values = new ArrayList<String>();
    values.add("restricted"); //make sure you have access to keys marked as restricted before you do add this key attribute
    attrMap.set("classification", values);
    encryptAttributes.setKeyAttributes(attrMap);
    
    fileCipher.encrypt(pathToPlainFile, pathToEncryptedFile);
  }
}
