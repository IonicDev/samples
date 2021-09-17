//
//  main.m
//  ionic-helloworld
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
        
        NSString* sdkPath = [[[NSProcessInfo processInfo] environment] objectForKey:@"IONIC_SDK_PATH"];
        if( ! sdkPath) {
            IonicLog_Error(TAG, @"[!] Please provide the path to the SDK as env variable: IONIC_SDK_PATH");
            return 3;
        }
        
        [IonicCrypto setCryptoSharedLibraryCustomDirectory:sdkPath];
        
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
        [agent setMetadataValue:@"ionic-application-name" forField:@"ionic-helloworld-objc"];
        [agent setMetadataValue:@"ionic-application-version" forField:@"1.0.0"];
        
#pragma - mark SENDER
        
        // Define data marking clearance-level
        NSDictionary * dataMarkings = @{
                                        @"clearance-level": @[@"secret"]
                                        };
        IonicChunkCryptoEncryptAttributes* keyAttributes =
        [[IonicChunkCryptoEncryptAttributes alloc] initWithKeyAttributes:dataMarkings];
        
        // initialize aes cipher object
        IonicChunkCryptoCipherAuto* senderCipher = [[IonicChunkCryptoCipherAuto alloc] initWithAgent:agent ];
        
        NSString* message = @"this is a secret message!";
        
        // encrypt with data marking
        NSString* cipherText = [senderCipher encryptText:message
                                          withAttributes:keyAttributes
                                                   error:&error];
        
        // check for cipher errors
        if (error) {
            IonicLogF_Error(TAG, @"Error: %@", error);
            return (int) error.code;
        }
        
        // Display Sender information.
        NSLog(@"CIPHERTEXT    : %@", cipherText);
        NSLog(@"");
        
#pragma - mark RECEIVER

        // initialize chunk cipher object
        IonicChunkCryptoCipherAuto* receiverCipher = [[IonicChunkCryptoCipherAuto alloc] initWithAgent:agent];
        
        // decrypt data
        NSString* plainText = [receiverCipher decryptText:cipherText error:&error];
        
        // check for cipher errors
        if (error) {
            IonicLogF_Error(TAG, @"Error: %@", error);
            return (int) error.code;
        }
        
        // Display Sender information.
        NSLog(@"PLAINTEXT    : %@", plainText);
        NSLog(@"");
    }
        
    return IONIC_AGENT_OK;
}
