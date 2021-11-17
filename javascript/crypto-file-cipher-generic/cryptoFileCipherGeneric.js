/*
 * (c) 2018-2022 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.iubenda.com/privacy-policy/27287244).
 */

/*
 * WARNING *
 * Calling agent.enrollUser() successfully is a prerequisite before using this code.
 * This is done using enrollDevice.js.
 */

import {getAgentConfig} from '../jssdkConfig.js';

const appData = getAgentConfig('JavaScript Encrypt Bytes File Cipher');

/*
* A. Functions
*/

// Update Filename
const makeNewFilename = function(oldFilename = "", fillText = "") {
    let lastDot = oldFilename.lastIndexOf('.');
    let baseName = "";
    let extSuff = "";
    if (lastDot !== -1) {
        baseName = oldFilename.slice(0, lastDot);
        extSuff = oldFilename.slice(lastDot);
    } else {
        baseName = oldFilename;
    }
    return baseName + fillText + extSuff;
}

// Encrypt File Contents
const encryptFile = async (plaintextBytes) => {
    // initialize agent
    try {
        let resp = await new window.IonicSdk.ISAgent(appData);
        let agent = resp.agent;

        // encrypt file contents
        try {
            let response = await agent.encryptBytesFileCipher(plaintextBytes);
            return response;
        } catch (sdkErrorResponse) {
            console.log('Error Encrypting Byte Array: ' + sdkErrorResponse.error);
        }
    } catch (sdkErrorResponse) {
        console.log('Initializing agent error: ' + sdkErrorResponse.error);
    }
}

/*
* B. Listener - user clicks "Choose File"
*/

document.getElementById('inputfile').addEventListener('change', function() {
    // 1. Get the filename and read the file
    let theFile = document.getElementById('inputfile').files[0]
    let fileReader = new FileReader();
    fileReader.readAsArrayBuffer(theFile);

    // 2. Once the file is read
    fileReader.onload = async () => {
        // 3. Protect contents and create new file
        let cryptoBytes = await encryptFile({data: fileReader.result});
        var cryptoFile = new File([cryptoBytes.data], "MachinaProtected.txt");

        // 4. Update "Download File" button: point to new file, asign filename, and enable button
        document.getElementById('fileLink').download = makeNewFilename(theFile.name, ".protected");
        document.getElementById('fileLink').href = URL.createObjectURL(cryptoFile);
        document.getElementById('downloadButton').disabled = false;
    }
})
