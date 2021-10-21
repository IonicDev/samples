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

const appData = getAgentConfig('JavaScript decrypt Bytes File Cipher');

/*
* A. Utility Functions
*/

// decrypt file body
const decryptFile = async (encryptedBytes) => {
  // initialize agent
  try {
    let resp = await new window.IonicSdk.ISAgent(appData);

    try {
      let response = await resp.agent.decryptBytesFileCipher(encryptedBytes);
      return response;
    } catch (sdkErrorResponse) {
      console.log('Error Decrypting Byte Array: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

// encrypt file body
const encryptFile = async (plaintextBytes) => {
  // initialize agent
  try {
    let resp = await new window.IonicSdk.ISAgent(appData);

    try {
      let response = await resp.agent.encryptBytesFileCipher(plaintextBytes);
      return response;
    } catch (sdkErrorResponse) {
      console.log('Error Encrypting Byte Array: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

// Update filename
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

/*
* B. Create and load a sample protected file
*/

var theFile = {name: "sampleFile.txt"};
var cryptoBytes = null;

// 1. Create a plaintext sample file
var sampleFile = new File(["This is a sample file."], "plaintextSample.txt");
var sampleReader = new FileReader();
sampleReader.readAsArrayBuffer(sampleFile);
sampleReader.onload = async () => {
                                   // 2. Encrypt sample file and create protected sample file
                                   cryptoBytes = await encryptFile({data: sampleReader.result});
                                   let cryptoFile = new File([cryptoBytes.data], "encryptedSample.txt");

                                   // 3. Link protected sample file to the Download File button
                                   document.getElementById('sampleLink').download = "protectedSample.txt";
                                   document.getElementById('sampleLink').href = URL.createObjectURL(cryptoFile);
                                   document.getElementById('sampleButton').disabled = false;
                                   document.getElementById('decryptButton').disabled = false;

                                   // 3. No shenanigans - reread contents of the protected file
                                   let newReader = new FileReader();
                                   newReader.readAsArrayBuffer(cryptoFile);
                                   newReader.onload = async() => {
                                                                  cryptoBytes = {data: newReader.result};
}}

/*
* C. Event Listeners
*/

// User clicks "Choose File"
document.getElementById('inputfile').addEventListener('change', function() {

  // Get the filename and read the file; replace the sample file
  theFile = document.getElementById('inputfile').files[0]
  let fileReader = new FileReader();
  fileReader.readAsArrayBuffer(theFile);
  fileReader.onload = async () => {
                                   cryptoBytes = {data: fileReader.result};
}});

// User clicks "Decrypt"
document.getElementById('decryptButton').onclick = async () => {
  // Decrypt loaded contents and create a new file
  let plaintextBytes = await decryptFile(cryptoBytes);
  var plaintextFile = new File([plaintextBytes.data], "plaintextFile.txt");

  // Link the new plaintext file to the "Download File" button
  document.getElementById('fileLink').download = makeNewFilename(theFile.name, ".plaintext");
  document.getElementById('fileLink').href = URL.createObjectURL(plaintextFile);
  document.getElementById('downloadButton').disabled = false;
};
