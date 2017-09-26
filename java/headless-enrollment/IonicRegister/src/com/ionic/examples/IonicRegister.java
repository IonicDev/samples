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
import com.ionicsecurity.sdk.SdkException;

/**
 * This class handles the SAML enrollment. SAML Enrollment employs IdP (Identity
 * Providers) via SAML,as third party authentication mechanisms. The method used
 * is determined by the enrollment URL, and the configuration of the enrollment
 * server. This code assumes that a SAML assertion has been generated and placed
 * in the appropriate file.
 * 
 * @author IonicSecurity
 *
 */
public class IonicRegister {

	// The field names for the token registration data returned from the
	// enrollment server for Loud Enrollment (LE)
	final String uidAuthFieldName = "X-Ionic-Reg-Uidauth";
	final String ionicUrlFieldName = "X-Ionic-Reg-Ionic-Url";
	final String rsaPubKeyUrlFieldName = "X-Ionic-Reg-Pubkey-Url";
	final String enrollmentTagFieldName = "X-Ionic-Reg-Enrollment-Tag";
	final String tokenFieldName = "X-Ionic-Reg-SToken";

	public static void main(String[] args) {
		String xmlString;
		try {
			xmlString = readAssertion(EnrollmentConstants.ASSERTION_FILE);
		} catch (IOException e1) {
			System.out.println("Failed to read Assertion file: " + e1.getMessage());
			e1.printStackTrace();
			return;
		}
		URL enrollmentEP;
		try {
			enrollmentEP = new URL(EnrollmentConstants.ENROLLMENT_ENDPOINT);
		} catch (MalformedURLException e) {
			System.out.println("Bad Enrollment Endpoint: " + e.getMessage());
			return;
		}
		IonicRegister ionicRegister = new IonicRegister();
		try {
			TokenEnrollmentData tokenEnrollmentData = ionicRegister.doEnrollmentStart(enrollmentEP, xmlString);
			Agent agent = IonicAgentFactory.getAgent();
			
			try {
				ionicRegister.doEnrollmentFinish(agent, tokenEnrollmentData);
			} catch (IOException e) {
				System.out.println("Can't finish the enrollment: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (IonicRegisterException e) {
			System.out.println("Registration Error: " + e.getMessage());
		}
	}

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
	 * Finish this up and enroll this device/user
	 * 
	 * @param agent
	 *            An instance of the IonicSDK
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
	private void enrollDevice(Agent agent, URL serverUrl, String keySpace, String token, String uidAuth,
			String rsaPubKey) throws IonicRegisterException {
		CreateDeviceRequest request = new CreateDeviceRequest();
		
		request.setServer(serverUrl.toString());
		request.setETag(keySpace);

		request.setToken(token); /* from the response to the SAML assertion */
		request.setUidAuth(uidAuth); /* from the response X-Ionic-Reg-Uidauth */

		request.setEiRsaPublicKeyBase64(rsaPubKey);
		try {
			CreateDeviceResponse response = agent.createDevice(request);
			if (response.getDeviceProfile() != null) {
				DeviceProfilePersistorBase<?> profilePersistor = ProfilePersistorFactory.getPersistor();
				agent.saveProfiles(profilePersistor);
			}
		} catch (SdkException ex) {
			throw new IonicRegisterException("Enrollment failed: " + ex.getMessage());
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
	 * Use the tokenData generated in doEnrollmentStart along with the received
	 * token to enroll this user/device
	 * 
	 * @param agent
	 *            An SDK agent for this thread
	 * @param token
	 *            The mighty token
	 * @param tokenData
	 *            Data that must be paired with the token in order to complete
	 *            enrollment
	 * @return true if enrollment was successful
	 * @throws IOException
	 * @throws IonicRegisterException
	 */
	public boolean doEnrollmentFinish(Agent agent, TokenEnrollmentData tokenData)
			throws IOException, IonicRegisterException {
		if (!agent.isInitialized())
			return false;
		enrollDevice(agent, tokenData.getIonicUrl(), tokenData.getEnrollmentTag(), tokenData.getSToken(),
				tokenData.getUidAuth(), tokenData.getRsaPubKey());
		return true;
	}

	/***
	 * Get the enrollment data and ask for a token to be sent to <emailAddress>
	 * 
	 * @param serverURL
	 *            The enrollment server URL (enrollment endpoint)
	 * @param emailAddress
	 *            The address that will be enrolled (as the user) along with
	 *            this device.
	 * @return The enrollment data that needs to be provided with a token (sent
	 *         to the emailAddress) in order to finish enrollment.
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

}
