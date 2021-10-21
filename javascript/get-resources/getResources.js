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

const main = async () => {

  const appData = getAgentConfig('JavaScript Get Resources');

  // initialize agent
  try {
    const resp = await new window.IonicSdk.ISAgent(appData);
    const agent = resp.agent;
    console.log('');

    try {
      // Get Markings
      const response = await agent.getResources({requests: [{'resource': 'markings'}]});

      // Print Markings and Values
      for (let index = 0, len = response.responses.length; index < len; ++index) {
        for (let index2 = 0, len2 = response.responses[index].data.length; index2 < len2; ++index2) {
            let thisDefaultValue = response.responses[index].data[index2].defaultValue;
            if (thisDefaultValue === "") {
                thisDefaultValue = '~Empty String~';
            }
            console.log('Marking Name          : ' + response.responses[index].data[index2].name);
            console.log('Marking Default Value : ' + thisDefaultValue);
            console.log('Marking Value Set     : ' + response.responses[index].data[index2].values);
            console.log('--------------------------------------------');
        }
      }
    } catch (sdkErrorResponse) {
      console.log('Error Getting Markings Resources: ' + sdkErrorResponse.error);
    }
  } catch (sdkErrorResponse) {
    console.log('Initializing agent error: ' + sdkErrorResponse.error);
  }
}

main()
