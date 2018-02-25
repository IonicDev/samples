/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

import com.ionicsecurity.sdk.AgentSdk;
import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.DeviceProfilePersistorPlainText;
import com.ionicsecurity.sdk.CreateKeysRequest;
import com.ionicsecurity.sdk.CreateKeysResponse;
import com.ionicsecurity.sdk.GetKeysRequest;
import com.ionicsecurity.sdk.GetKeysResponse;
import com.ionicsecurity.sdk.KeyAttributesMap;
import com.ionicsecurity.sdk.SdkException;

import java.util.ArrayList;
import java.util.List;

public class IonicDirectKeys
{
    public static void main(String[] args)
    {
        // Initialize sdk (calls system load from platform-specific objects)
        AgentSdk.initialize(null);

        // Load a plain-text device profile, or SEP, from disk
        DeviceProfilePersistorPlainText sepPersistorPt = new DeviceProfilePersistorPlainText();
        String sProfilePath = System.getProperty("user.home") + "/.ionicsecurity/profile.pt";
        sepPersistorPt.setFilePath(sProfilePath);

        // Initialize the Ionic agent
        Agent agent = new Agent();
        agent.initialize(sepPersistorPt);
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
        GetKeysResponse keysResponse;
        try {
        	keysResponse = agent.getKeys(keysRequest);
        } catch (SdkException ex) {
        	System.out.println("Sdk Exception: " + ex.getMessage());
        	return;
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
