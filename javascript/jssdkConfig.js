/*
 * (c) 2019-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// Returns common agent configuration.
export function getAgentConfig(applicationName) {

  // This is the common agent configuration for the Javascript SDK samples.  The appId, userId,
  //  and userAuth needs to be the same as the agent configuration that was used in enrollment.
  const agentConfig = {
    appId: 'ionic-js-samples',
    userId: 'developer',
    userAuth: 'password123',
    metadata: {
      'ionic-application-name': applicationName,
      'ionic-application-version': '1.3.0'
    }
  };

  return (agentConfig);
}

