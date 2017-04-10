/*
 * (c) 2016-2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

using System;
using System.Collections.Generic;

using IonicSecurity.SDK;

namespace DirectKeys
{
    class Program
    {
        static private Agent agent = null;

        static int Main(string[] args)
        {
            // Initialize the Ionic agent
            agent = new Agent();
            agent.Initialize();

            // Request keys
            // Forming the key request object
            CreateKeysRequest request = new CreateKeysRequest();
            // Here update request with the list of what it should create.
            AttributesDictionary dirAttributes = new AttributesDictionary();
            List<string> listClassValues = new List<string>(1);
            listClassValues.Add("restricted");
            dirAttributes.Add("classification", listClassValues);
            CreateKeysRequest.Key requestKey = new CreateKeysRequest.Key("reference_key", 2, dirAttributes);
            request.Keys.Add(requestKey);
            // Now ask the server to make those keys:
            CreateKeysResponse response;
            try
            {
                response = agent.CreateKeys(request);
            }
            catch (SdkException e)
            {
                System.Console.WriteLine("Error creating keys: {0}", e.Message);
                return -1;
            }

            // Show us what keys we got (you can always get a key right when you create it):
            List<CreateKeysResponse.Key> responseKeys = response.Keys;
            GetKeysRequest fetchRequest = new GetKeysRequest(); //we will use this to track the keys we want to fetch later
            foreach(CreateKeysResponse.Key responseKey in responseKeys)
            {
                System.Console.WriteLine("We created a key with the Key Tag: {0}", responseKey.Id);
                fetchRequest.KeyIds.Add(responseKey.Id);
            }

            // The rest of this program would typically happen at a different time,
            //  not right after creating the keys, but when you were going to access
            //  the data protected by those keys.

            // Now, using the Key Tags, ask the server for those keys again:
            // NOTE: We populated fetchRequest's list of keytags in the above loop.
            GetKeysResponse fetchResponse;
            try
            {
                fetchResponse = agent.GetKeys(fetchRequest);
            }
            catch (SdkException e)
            {
                System.Console.WriteLine("Error fetching keys: {0}", e.Message);
                return -1;
            }
            // Show what we got access to after a request for keys:
            foreach(GetKeysResponse.Key responseKey in fetchResponse.Keys)
            {
                System.Console.WriteLine("We fetched a key with the Key Tag: {0}", responseKey.Id);
            }

            // Tell us if we got less keys when we fetched than we created.
            //  This would happen if policy didn't give us access to all the keys.
            if (fetchResponse.Keys.Count < fetchRequest.KeyIds.Count)
            {
                System.Console.Write("We didn't get given all of the requested keys.");
                return -2;
            }

            System.Console.Read();
            return 0;
        }
    }
}