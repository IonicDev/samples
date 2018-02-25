/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package ionic.directkeys;
import com.ionic.sdk.error.SdkException;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.agent.key.KeyAttributesMap;

import java.util.ArrayList;
import java.util.List;

public class IonicDirectKeys
{
    public static void main(String[] args)
    {
        // Setup an agent object to talk to Ionic
        Agent agent = new Agent();
        try {
            String sProfilePath = System.getenv("HOME") + "/.ionicsecurity/profiles.pt";
            DeviceProfilePersistorPlainText ptPersistor = new DeviceProfilePersistorPlainText(sProfilePath);
            agent.initialize(ptPersistor);
        } catch (SdkException e) {
            System.out.println("Failed to initialize agent:");
            System.out.println(e);
            System.exit(1);
        }

        System.out.println("\nJava Ionic Direct Key Access\n");

        // Request keys
        CreateKeysRequest request = new CreateKeysRequest();

        // Here update request.getKeys() as the list of what it should create.
        // We are going to add a key attribute "classification", with the single value of "restricted"
        KeyAttributesMap attributes = new KeyAttributesMap();
        List<String> values = new ArrayList<String>();

        // NOTE: Make sure you have access to data tagged with "restricted" or you will not be able to fetch this key:
        values.add("restricted");
        attributes.set("classification", values);
        CreateKeysRequest.Key requestKey = new CreateKeysRequest.Key("example", 2, attributes);
        request.getKeys().add(requestKey);
        
        // Now ask the server to make those keys:
        CreateKeysResponse response;
        try {
        	response = agent.createKeys(request);
        } catch (SdkException ex) {
        	System.out.println("Sdk Exception: " + ex.getMessage());
        	return;
        }
        List<CreateKeysResponse.Key> keys = response.getKeys();
        
        // Show us what keys we got (you can always get a key right when you create it):
        List<String> createdKeyIds = new ArrayList<String>();
        for (CreateKeysResponse.Key key : keys) {
            System.out.println("We created a key with the Key Tag: " + key.getId());
            createdKeyIds.add(key.getId()); //keep a list of IDs so we can request them later
        }
        
        // The rest of this program would typically happen at a different time,
        //  not right after creating the keys, but when you were going to access
        //  the data protected by those keys.
        // Now, using the Key Tags, ask the server for those keys again:
        GetKeysRequest keysRequest = new GetKeysRequest();
        keysRequest.getKeyIds().addAll(createdKeyIds);
        GetKeysResponse keysResponse = null;
        try {
        	keysResponse = agent.getKeys(keysRequest);
        } catch (SdkException ex) {
        	System.out.println("Sdk Exception: " + ex.getMessage());
        	System.exit(1);
        }

        // Show what we got access to after a request for keys:
       for (GetKeysResponse.Key key: keysResponse.getKeys()) {
            System.out.println("We fetched a key with the Key Tag: " + key.getId());
        }
        
        // Tell us if we fetched fewer keys than we created.
        // This would happen if policy didn't give us access to all the keys.
        if (keysResponse.getKeys().size() < keys.size()) {
            System.out.println("We didn't get given all of the requested keys.");
            return;
        }
    }
}
