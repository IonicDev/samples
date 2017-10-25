/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.CreateDeviceRequest;
import com.ionicsecurity.sdk.CreateDeviceResponse;
import com.ionicsecurity.sdk.DeviceProfilePersistorBase;
import com.ionicsecurity.sdk.MetadataMap;
import com.ionicsecurity.sdk.SdkException;

/**
 * This class handles the SAML enrollment. SAML Enrollment employs IdP (Identity
 * Providers) via SAML,as third party authentication mechanisms. The method used
 * is determined by the enrollment URL, and the configuration of the enrollment
 * server. This code assumes that a SAML assertion has been generated and placed
 * in the appropriate file.
 */
public class IonicRegister {

	// NOTE: Configure these values to match the version of your application, to provide
	// context to Ionic.com surrounding the enrollment.
	public static final String ApplicationName = "IonicRegister";
	public static final String ApplicationVersion = "0.0.2";
	public static final String ClientType = "Java Application";
	public static final String ClientVersion = "0.0";
	
	public static void main(String[] args) {
		// Load the assertion into memory.
		// This is simply an XML string and you may skip saving it to a file if you are obtaining
		// it from a web-service or similar.
		String xmlString;
		try {
			xmlString = readAssertion(EnrollmentConstants.ASSERTION_FILE);
		} catch (IOException e1) {
			System.out.println("Failed to read Assertion file: " + e1.getMessage());
			e1.printStackTrace();
			return;
		}
		
		// Obtain the URL of the enrollment endpoint.
		// The configuration of this URL must match the key used to generated the SAML assertion.
		URL enrollmentEP;
		try {
			enrollmentEP = new URL(EnrollmentConstants.ENROLLMENT_ENDPOINT);
		} catch (MalformedURLException e) {
			System.out.println("Bad Enrollment Endpoint: " + e.getMessage());
			return;
		}
		
		// Setup a persistor which will tell the SDK how to store the resulting SEP.
		// This is exposed as the user of this code may wish to use different types of persistors
		// depending on the use case, or different between development and testing/production.
		DeviceProfilePersistorBase<?> profilePersistor = ProfilePersistorFactory.getPersistor();
		
		// Setup an instance of the Ionic SDK, providing it meta-data about the registration application
		// that this function is producing.
		Agent agent = IonicAgentFactory.getAgent();
		// This meta-data is important as it allows Ionic.com visibility so the state of registration can
		// be determined.
		MetadataMap mApplicationMetadata = new MetadataMap();
		mApplicationMetadata.set("ionic-application-name", ApplicationName);
		mApplicationMetadata.set("ionic-application-version", ApplicationVersion);
		mApplicationMetadata.set("ionic-client-type", ClientType);
		mApplicationMetadata.set("ionic-client-version", ClientVersion);
		//NOTE: You may add additional meta-data, see dev.ionic.com for explanations.
		agent.setMetadata(mApplicationMetadata);
		
		// Use the parameters to actually perform the registration:
		IonicRegister ionicRegister = new IonicRegister();
		try {
			// Communicate with the enrollment server to submit the Generated SAML Assertion and obtain
			// an Ionic Assertion (Ionic AOI) which can be used to submit to Ionic.com.
			TokenEnrollmentData tokenEnrollmentData = ionicRegister.doEnrollmentStart(enrollmentEP, xmlString);
			
			// Submit the Ionic AOI to Ionic.com, which will be used in the VBE process to issue a SEP.
			try {
				ionicRegister.doEnrollmentFinish(agent, profilePersistor, tokenEnrollmentData);
			} catch (IOException e) {
				System.out.println("Can't finish the enrollment: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (IonicRegisterException e) {
			System.out.println("Registration Error: " + e.getMessage());
		}
	}

	// Convenience Functions
	
	/***
	 * Get the enrollment data.
	 * 
	 * @param serverURL
	 *            The enrollment server URL (enrollment endpoint)
	 * @param response
	 *            The data to use with the enrollment server, for Generated SAML Assertions this
	 *            is the string of the assertion file.
	 * @return The enrollment data that needs to be provided in order to finish enrollment.
	 * @throws IonicRegisterException
	 */
	public TokenEnrollmentData doEnrollmentStart(URL serverURL, String response) throws IonicRegisterException {
		TokenEnrollmentData enrollmentData;
		try {
			enrollmentData = doGenerateTokenNew(serverURL, response);
		} catch (Exception ex) {
			throw new IonicRegisterException("Failed to generate token: " + ex.getMessage());
		}

		return enrollmentData;
	}
	
	/**
	 * Use the tokenData generated in doEnrollmentStart along with the received
	 * token to enroll this user/device
	 * 
	 * @param agent
	 *            An Ionic SDK agent for this thread
	 * @param profilePersistor
	 *            An instance of some type of Profile Persistor class recognized by the Ionic SDK
	 * @param tokenData
	 *            Data resulting from communication with the customer Enrollment Service
	 * @throws IOException
	 * @throws IonicRegisterException
	 */
	public void doEnrollmentFinish(Agent agent, DeviceProfilePersistorBase<?> profilePersistor,
			TokenEnrollmentData tokenData) throws IOException, IonicRegisterException {
		if (!agent.isInitialized())
			throw new IonicRegisterException("Agent must be intialized to attempt registration.");
		enrollDevice(agent, profilePersistor, tokenData.getIonicUrl(), tokenData.getEnrollmentTag(),
				tokenData.getSToken(), tokenData.getUidAuth(), tokenData.getRsaPubKey());
	}

	// Communication with Enrollment Service
	
	// The field names for the token registration data returned from the enrollment server
	final String uidAuthFieldName = "X-Ionic-Reg-Uidauth";
	final String ionicUrlFieldName = "X-Ionic-Reg-Ionic-Url";
	final String rsaPubKeyUrlFieldName = "X-Ionic-Reg-Pubkey-Url";
	final String enrollmentTagFieldName = "X-Ionic-Reg-Enrollment-Tag";
	final String tokenFieldName = "X-Ionic-Reg-SToken";

	/**
	 * Finish this up and enroll this device/user
	 * 
	 * @param agent
	 *            An instance of the Ionic SDK
	 * @param profilePersistor
	 *            An instance of some type of Profile Persistor class recognized by the Ionic SDK
	 * @param serverUrl
	 *            The enrollment server URL
	 * @param keySpace
	 *            The tenant key space
	 * @param token
	 *            The enrollment token swapped for the assertion
	 * @param uidAuth
	 *            The UID Auth parameter returned with the token
	 * @param rsaPubKey
	 *            The public key for the enrollment server
	 * @throws IonicRegisterException
	 * 
	 */
	private void enrollDevice(Agent agent, DeviceProfilePersistorBase<?> profilePersistor,
			URL serverUrl, String keySpace, String token, String uidAuth, String rsaPubKey) throws IonicRegisterException {
		// This function is provided by the SDK and encapsulates the core VBE communication.
		CreateDeviceRequest request = new CreateDeviceRequest();
		
		request.setServer(serverUrl.toString());
		request.setETag(keySpace);

		request.setToken(token); /* from the response to the SAML assertion */
		request.setUidAuth(uidAuth); /* from the response X-Ionic-Reg-Uidauth */

		request.setEiRsaPublicKeyBase64(rsaPubKey);
		try {
			CreateDeviceResponse response = agent.createDevice(request);
			if (response.getDeviceProfile() != null) {
				agent.saveProfiles(profilePersistor);
			}
		} catch (SdkException ex) {
			throw new IonicRegisterException("Enrollment failed: " + ex.getMessage());
		}
	}

	/**
	 * The new enrollment server passes the enrollment instance data back via
	 * the HTTP headers. There is also a web page with HTML and scripts, but we
	 * don't use that approach here, so we ignore the actual content and just
	 * look at response headers for the fields we need.
	 * 
	 * @param enrollServerURI
	 * @param samlResponseXMLString
	 * @return The enrollment data that should be used with the token to finish
	 *         enrollment
	 * @throws IonicRegisterException
	 * @throws IOException
	 */
	private TokenEnrollmentData doGenerateTokenNew(URL enrollServerURI, String samlResponseXMLString)
			throws IonicRegisterException, IOException {
		HttpsURLConnection uc = (HttpsURLConnection) enrollServerURI.openConnection();

		uc.setUseCaches(false);
		uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		uc.setRequestProperty("Expect", "100-continue");
		uc.setDoOutput(true); // POST
		uc.setRequestMethod("POST");
		try (OutputStream output = uc.getOutputStream()) {
			output.write("SAMLResponse=".getBytes());
			output.write(URLEncoder.encode(samlResponseXMLString, "UTF-8").getBytes());
			output.close();
			if (uc.getResponseCode() != 200) {
				dumpUCResponse(uc);
				throw new IonicRegisterException("Bad response code from enrollment server");
			}
			String uidAuth = uc.getHeaderField(uidAuthFieldName);
			String ionicUrl = uc.getHeaderField(ionicUrlFieldName);
			String rsaPubKeyUrl = uc.getHeaderField(rsaPubKeyUrlFieldName);
			String enrollmentTag = uc.getHeaderField(enrollmentTagFieldName);
			String sToken = uc.getHeaderField(tokenFieldName);
			if (uidAuth == null || ionicUrl == null || rsaPubKeyUrl == null || enrollmentTag == null || sToken == null) {
				dumpUCResponse(uc);
				throw new IonicRegisterException("Bad response from enrollment server");
			}

			String enrollServerRsaPubKey = getRsaPubKey(rsaPubKeyUrl);

			TokenEnrollmentData enrollmentData = new TokenEnrollmentData(ionicUrl, enrollmentTag, uidAuth,
					enrollServerRsaPubKey, sToken);
			return enrollmentData;
		} catch (SdkException ex) {
			throw new IonicRegisterException("Generate Token failed: " + ex.getMessage());
		}
	}

	/**
	 * Use the <rsaServerURL> to retrieve the RSA public key for the enrollment
	 * server
	 * 
	 * @param rsaServerURL
	 *            The URL to send a GET request to
	 * @return The RSA public key for the enrollment server
	 * @throws IonicRegisterException
	 */
	private String getRsaPubKey(String rsaServerURL) throws IonicRegisterException {
		try {
			URL url = new URL(rsaServerURL);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setUseCaches(false);
			InputStream response = uc.getInputStream();
			if (uc.getResponseCode() != 200)
				throw new IOException();
			String responseString = IOUtils.toString(response, "UTF-8");
			responseString = responseString.replaceAll("\\s|\n", "");
			IOUtils.closeQuietly(response);
			return responseString;
		} catch (Exception ex) {
			throw new IonicRegisterException(ex.getMessage() + ": Can't get RSA Public Key from Enrollment Server");
		}
	}
	
	// Utility Functions
	
	/**
	 * Read in the contents of the Assertion file
	 * 
	 * @param fileName
	 * @return Returns the contents of the file in a String, or null on error
	 * @throws IOException
	 */
	private static String readAssertion(String fileName) throws IOException {
		String contents = new String(Files.readAllBytes(Paths.get(fileName)));
		return contents;
	}

	/**
	 * Dump out the headers and any returned content
	 * 
	 * @param uc
	 */
	private void dumpUCResponse(HttpsURLConnection uc) {
	
		StringBuilder builder = new StringBuilder();
		try {
			builder.append(uc.getResponseCode()).append(" ").append(uc.getResponseMessage()).append("\n");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// print out the headers
		Map<String, List<String>> map = uc.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			if (entry.getKey() == null)
				continue;
			builder.append(entry.getKey()).append(": ");

			List<String> headerValues = entry.getValue();
			Iterator<String> it = headerValues.iterator();
			if (it.hasNext()) {
				builder.append(it.next());

				while (it.hasNext()) {
					builder.append(", ").append(it.next());
				}
			}

			builder.append("\n");
		}
		System.out.println(builder);
		
		// print out the data
		InputStream inputStream;
		try {
			if (uc.getResponseCode() == 200) {
				inputStream = uc.getInputStream();
			} else {
				inputStream = uc.getErrorStream();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		IOUtils.closeQuietly(inputStream);
	}

}
