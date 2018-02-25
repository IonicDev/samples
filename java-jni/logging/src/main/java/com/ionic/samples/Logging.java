/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;
import com.ionicsecurity.sdk.ChunkCipherAuto;
import com.ionicsecurity.sdk.KeyAttributesMap;
import com.ionicsecurity.sdk.Log;
import com.ionicsecurity.sdk.LogBase;
import com.ionicsecurity.sdk.LogFactory;
import java.util.List;
import java.util.ArrayList;

public class Logging
{
  public static void main(String[] args)
  {

    String plainText = "Hello, Ionic!";

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

    // The configuration file, instead of programmatic commands, can be used to setup the logging.
    LogBase logger = LogFactory.getInstance().createFromConfigFile("logConfig.json");
    System.out.println(logger);
    Log.setSingleton(logger);
    Log.tc(Logging.class, "main", "LogDemoChannel", "This is a log of severity TRACE");
    Log.dc(Logging.class, "main", "LogDemoChannel", "This is a log of severity DEBUG");
    Log.ic(Logging.class, "main", "LogDemoChannel", "This is a log of severity INFO");
    Log.wc(Logging.class, "main", "LogDemoChannel", "This is a log of severity WARN");
    Log.ec(Logging.class, "main", "LogDemoChannel", "This is a log of severity ERROR");
    Log.fc(Logging.class, "main", "LogDemoChannel", "This is a log of severity FATAL");
    
    // Encrypt a string using the chunk data format.  
    KeyAttributesMap attrMap = new KeyAttributesMap();
    
    List<String> values = new ArrayList<String>();
    values.add("restricted"); //make sure you have access to keys marked as restricted before setting this key attribute
    attrMap.set("classification", values);
    ChunkCipherAuto chunkCrypto = new ChunkCipherAuto(agent);
    String encryptedText = chunkCrypto.encrypt(plainText, null);

    System.out.println("Plain Text: " + plainText);
    System.out.println("Chunk-Encrypted String: " + encryptedText);
  }
}
