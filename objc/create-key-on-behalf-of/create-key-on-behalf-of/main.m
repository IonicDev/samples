//
//  main.m
//  keys
//
//  Copyright © 2020 Ionic Security Inc. All rights reserved.
//  By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the Privacy Policy (https://www.ionic.com/privacy-notice/).
//

#import <Foundation/Foundation.h>

#import <IonicAgentSDK/IonicAgentSDK.h>
#import <IonicAgentSDK/IonicAgentDeviceProfilePersistor.h>


int main(int argc, const char * argv[]) {
    @autoreleasepool {
        
        NSString* TAG = @"SampleApplicationChannel";
        
        NSString* delegatedUserEmail = @"test@ionic.com";
        
        NSString* persistorPassword = [[[NSProcessInfo processInfo] environment] objectForKey:@"IONIC_PERSISTOR_PASSWORD"];
        if( ! persistorPassword) {
            IonicLog_Error(TAG, @"[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            return 1;
        }
        
        NSString * persistorPath = [NSHomeDirectory() stringByAppendingPathComponent:@".ionicsecurity/profiles.pw"];
        NSFileManager *fileManager = [NSFileManager defaultManager];
        if ( ! [fileManager fileExistsAtPath:persistorPath]){
            IonicLogF_Error(TAG, @"[!] '%@' does not exist", persistorPath);
            return 2;
        }
        
        // initialize agent with password persistor
        IonicAgentDeviceProfilePersistorPassword* persistor = [[IonicAgentDeviceProfilePersistorPassword alloc] init];
        persistor.filePath = persistorPath;
        persistor.password = persistorPassword;
        
        NSError * error = nil;
        // create an agent and initialize it with the password persistor all defaults
        IonicAgent * agent = [[IonicAgent alloc] initWithDefaults:persistor
                                                            error:&error];
        // check for initialization error
        if (error) {
            IonicLogF_Error(TAG, @"Failed to initialize agent object, error code = %d.", error.code);
            return (int) error.code;
        }
        
        // set app metadata
        [agent setMetadataValue:@"ionic-keys-objc-tutorial" forField:@"ionic-application-name"];
        [agent setMetadataValue:@"1.0.0" forField:@"ionic-application-version"];
        
        // define on-behalf-of user in the request metadata
        [agent setMetadataValue:delegatedUserEmail forField:@"ionic-delegated-email"];
        
        // create new key with fixed and mutable attributes
        IonicAgentCreateKeysRequestKey* key = [[IonicAgentCreateKeysRequestKey alloc] initWithReferenceId:@"refid1"
                                                                                                 quantity:1];
        IonicAgentCreateKeysRequest* request = [[IonicAgentCreateKeysRequest alloc] init];
        [request addKey:key];
        IonicAgentCreateKeysResponse* response = [agent createKeysUsingRequest:request error:&error];
        
        // check for key creation errors
        if (error) {
            IonicLogF_Error(TAG, @"Error creating key: %@", error);
            return (int) error.code;
        }
        
        // display new key
        IonicAgentCreateKeysResponseKey* createKeyResponse = [response findKeyUsingRefId:@"refid1"];
        NSLog(@"NEW KEY :");
        NSLog(@"KeyId    : %@", [createKeyResponse id]);
        NSLog(@"KeyBytes : %@", [createKeyResponse key]);
        NSLog(@"FixedAttrs : ");
        for (NSObject* each in [[createKeyResponse mutableAttributes] allKeys])
            NSLog(@"  %@ : %@", each, [[[createKeyResponse mutableAttributes] valueForKey:(NSString*)each] firstObject]);
        NSLog(@"MutableAttrs : ");
        for (NSObject* each in [[createKeyResponse mutableAttributes] allKeys])
            NSLog(@"  %@ : %@", each, [[[createKeyResponse mutableAttributes] valueForKey:(NSString*)each] firstObject]);
        NSLog(@"");
        
        // get key by KeyId
        NSString* keyId = [createKeyResponse id];
        IonicAgentGetKeysResponse* getKeyResponse = [agent getKeyWithId:keyId error:&error];
        
        // check for key fetch errors
        if (error) {
            IonicLogF_Error(TAG, @"Error creating key: %@", error);
            return (int) error.code;
        }
        if ([[getKeyResponse keys] count] == 0) {
            IonicLog_Error(TAG, @"No key was returned (key does not exist or access was denied)");
            return IONIC_AGENT_MISSINGVALUE;
        }
        
        // display fetched key
        IonicAgentGetKeysResponseKey* fetchedKey = [[getKeyResponse keys] firstObject];
        NSLog(@"FETCHED KEY :");
        NSLog(@"KeyId    : %@", [fetchedKey id]);
        NSLog(@"KeyBytes : %@", [fetchedKey key]);
        NSLog(@"FixedAttrs : ");
        for (NSObject* each in [[fetchedKey mutableAttributes] allKeys])
            NSLog(@"  %@ : %@", each, [[[fetchedKey mutableAttributes] valueForKey:(NSString*)each] firstObject]);
        NSLog(@"MutableAttrs : ");
        for (NSObject* each in [[fetchedKey mutableAttributes] allKeys])
            NSLog(@"  %@ : %@", each, [[[fetchedKey mutableAttributes] valueForKey:(NSString*)each] firstObject]);
        NSLog(@"");
        
        // define new mutable attributes
        NSDictionary* updatedAttributes = @{
                                            @"classification": @[@"Restricted"],
                                            };
        [fetchedKey setMutableAttributes:updatedAttributes];
        
        IonicAgentUpdateKeysRequestKey* updatedKey = [[IonicAgentUpdateKeysRequestKey alloc] initWithKey:fetchedKey
                                                                                            ForceUpdate:YES];
        
        IonicAgentUpdateKeysResponse* updateResponse = [agent updateKey:updatedKey error:&error];
        
        // check for key update errors
        if (error) {
            IonicLogF_Error(TAG, @"Error updating key: %@", error);
            return (int) error.code;
        }
        
        IonicAgentUpdateKeysResponseKey* updatedResponseKey = [[updateResponse keys] firstObject];
        
        // display updated key
        NSLog(@"UPDATED KEY :");
        NSLog(@"KeyId    : %@", [updatedResponseKey id]);
        NSLog(@"KeyBytes : %@", [updatedResponseKey key]);
        NSLog(@"FixedAttrs : ");
        for (NSObject* each in [[updatedResponseKey mutableAttributes] allKeys])
            NSLog(@"  %@ : %@", each, [[[updatedResponseKey mutableAttributes] valueForKey:(NSString*)each] firstObject]);
        NSLog(@"MutableAttrs : ");
        for (NSObject* each in [[updatedResponseKey mutableAttributes] allKeys])
            NSLog(@"  %@ : %@", each, [[[updatedResponseKey mutableAttributes] valueForKey:(NSString*)each] firstObject]);
        NSLog(@"");
    }
        
    return IONIC_AGENT_OK;
}
