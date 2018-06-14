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
    
    const keyId = "HVzG34L2MVI"

    // initialize agent
    const agent = new window.IonicSdk.ISAgent('https://dev-api.ionic.com/jssdk/latest/');
    await agent.loadUser(appData).catch((error) => {
        console.log(`Error loading profile: ${error}`)
        return
    })

    // define request metadata
    const newMutableAttributes = {
        "classification": ["Highly Restricted"]
    }

    // get key
    const getKeyResponse = await agent.getKeys({
        keyIds: [keyId]
    }).catch((error) => {
        console.log(`Error Creating Key: ${error}`)
        return
    })
    const fetchedKey = getKeyResponse.keys[0]

    // merge old and new mutable attributes
    const mutableAttributes = Object.assign({}, fetchedKey.mutableAttributes, newMutableAttributes)

    // update key
    const updateKeyResponse = await agent.updateKeys({
        keyRequests: [
        {
            keyId: keyId,
            force: true,
            mutableAttributes: mutableAttributes
        }]
    })
    const key = updateKeyResponse.keys[0]

    // display updated key
    console.log(`KeyId             : ${key.keyId}`)
    console.log(`KeyBytes          : ${key.key}`)
    console.log(`FixedAttributes   : ${key.attributes}`)
    console.log(`MutableAttributes : ${key.mutableAttributes}`)
}

main();
