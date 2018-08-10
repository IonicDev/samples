/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

const appData = {
    appId: 'ionic-js-samples',
    userId: 'developer',
    userAuth: 'password123',
    enrollmentUrl: 'https://dev-enrollment.ionic.com/keyspace/HVzG/register'
}

const main = async () => {
    
    const message = 'secret message'

    // initialize agent
    const agent = new window.IonicSdk.ISAgent('https://dev-api.ionic.com/jssdk/latest/');

    await agent.loadUser(appData).catch(async (error) => {
        if (
            error &&
            error.sdkResponseCode &&
            (error.sdkResponseCode === 40022 || error.sdkResponseCode === 40002)
        ) {
            const resp = await agent.enrollUser(appData)

            if(resp) {
                if (resp.redirect) {
                    window.open(resp.redirect);
                    return resp.Notifier;
                }
            }
            else {
                console.log('Error loading profile: ', error)
                return Promise.reject('Error enrolling');
            }
        }
        else {
            console.log('Error loading profile: ', error)
            return
        }
    })

    // encrypt message
    const encryptResponse = await agent.encryptStringChunkCipher({stringData: message})
    const ciphertext = encryptResponse.stringChunk

    // display data
    console.log('Plain Text: ', message)
    console.log('Ionic Chunk Encrypted Text: ', ciphertext)
}

main();
