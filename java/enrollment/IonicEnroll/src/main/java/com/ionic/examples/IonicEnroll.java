/*
 * (c) 2017-2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceRequest;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceResponse;
import com.ionic.sdk.device.profile.DeviceProfile;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorBase;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.error.AgentErrorModuleConstants;
import com.ionic.sdk.error.IonicException;

public class IonicEnroll {
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONVERSATION_ID = "X-Conversation-ID";
	public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String UIDAUTH_FIELDNAME = "X-Ionic-Reg-Uidauth";
	public static final String IONICURL_FIELDNAME = "X-Ionic-Reg-Ionic-Url";
	public static final String RSAPUBKEYURL_FIELDNAME = "X-Ionic-Reg-Pubkey-Url";
	public static final String ENROLLMENTTAG_FIELDNAME = "X-Ionic-Reg-Enrollment-Tag";
	public static final String TOKEN_FIELDNAME = "X-Ionic-Reg-SToken";
	public static final String IERSAPUBKEYBASE64 = "Ionic-IE-Pubkey-Base64";
	public static final String DEVICEID_FIELDNAME = "deviceID";
	public static final String SEPAESK_IDC_FIELDNAME = "SEPAESK-IDC";
	public static final String SEPAESK_FIELDNAME = "SEPAESK";

	public static final String DEFAULT_PTSTORE_PATH = "~/.ionicsecurity/profiles.pt";

	private static final Logger logger = Logger.getLogger(IonicEnroll.class.getName());
	static {
		// create parent logger
		Logger.getLogger(IonicEnroll.class.getPackage().getName());
	}
	private static final Scanner sc = new Scanner(System.in);
	private static void errorOut(String errorMsg) {
		sc.close();
		System.out.println("Error: " + errorMsg);
		System.exit(1);
	}
	public static void main(String[] args) {
		/*
		 * This function requests information from a user at the command line,
		 * and then enrolls the device using that. These values can also be
		 * hardcoded or provided in any way, such as from a configuration file.
		 * 
		 * //email enrollment String serverURL =
		 * "https://preview-enrollment.ionic.com/keyspace/ABC/le"; String
		 * emailAddress = "mail@domain.tld"; Map <String, String> mReg =
		 * getIonicRegViaLE(serverURL, emailAddress);
		 * 
		 * //or Generated SAML Assertion enrollment String serverURL =
		 * "https://preview-enrollment.ionic.com/keyspace/ABC/sp/123456789012345678901234/headless/saml"
		 * ; String samlAssertionfilePath = "C:\\Ionic\\samlAssertion.xml"; Map
		 * <String, String> mReg = getIonicRegViaSAML(serverURL,
		 * samlAssertionfilePath);
		 * 
		 */

		// Turn off most SDK logging
		final Logger sdkLogger = Logger.getLogger("com.ionic.sdk");
		sdkLogger.setLevel(Level.SEVERE);

		// We have to have a provider for the Signature algorithm:
		// "SHA256withRSA/PSS"
		// In this case we will use BouncyCastle
		Security.addProvider(new BouncyCastleProvider());

		System.out
				.println("Do you want to use Email or Generated SAML Assertion registration?");
		
		String option;
		do {
			System.out.print("Enter E for Email, S for Generated SAML Assertion or C for Cancel: ");
			System.out.flush();
			option = sc.nextLine();
		} while (option.matches("\\s*|^(?i)[^ces].*$"));

		if (option.matches("^(?i)c.*$")) {
			sc.close();
			return;
		}

		System.out.print("Enter the enrollment server URL: ");
		System.out.flush();
		String serverURL = sc.nextLine();
		if (UrlValidator.getInstance().isValid(serverURL) == false) {
			sc.close();
			System.out.println("Invalid URL entered.");
			System.exit(1);
		}

		Map<String, String> mReg;
		if (option.matches("(?i)e.*")) {
			System.out.print("Enter your email address: ");
			System.out.flush();
			String emailAddress = sc.nextLine();
			if (EmailValidator.getInstance(true).isValid(emailAddress) == false) {
				errorOut("Invalid email address entered.");
			}

			System.out.println("Send email address to enrollment portal.");
			try {
				mReg = getIonicRegViaLE(serverURL, emailAddress);
			} catch (Exception e) {
				errorOut("Failed to register via email: " + e.getMessage());
				return;
			}

			System.out.print("Check your email and enter the enrollment token: ");
			System.out.flush();
			mReg.put(TOKEN_FIELDNAME, sc.nextLine());
			if (mReg.get(TOKEN_FIELDNAME) == null
					|| mReg.get(TOKEN_FIELDNAME).length() < 15) {
				errorOut("Token to small.");
			}
		} else {
			System.out.print("Enter the path to the Generated SAML Assertion file: ");
			System.out.flush();
			String samlAssertionfilePath = sc.nextLine();
			if (samlAssertionfilePath == null
					|| samlAssertionfilePath.length() < 1) {
				errorOut("File path to short.");
			}
			samlAssertionfilePath = samlAssertionfilePath.replaceFirst("^~",
					System.getProperty("user.home"));

			System.out
					.println("Send Generated SAML Assertion to enrollment portal.");
			try {
				mReg = getIonicRegViaSAML(serverURL, samlAssertionfilePath);
			} catch (Exception e) {
				errorOut("Failed to register via Generated SAML Assertion: " + e.getMessage());
				return;
			}
		}

		// Now we have all of the parameters available, and we print them here:

		logger.info("enrollServerURI: " + mReg.get(IONICURL_FIELDNAME)
				+ "/v2.3/register/" + mReg.get(ENROLLMENTTAG_FIELDNAME));
		logger.info("enrollmentTag: " + mReg.get(ENROLLMENTTAG_FIELDNAME));
		logger.info("sToken: " + mReg.get(TOKEN_FIELDNAME));
		logger.info("uidAuth: " + mReg.get(UIDAUTH_FIELDNAME));
		logger.info("rsaPubKeyUrl: " + mReg.get(RSAPUBKEYURL_FIELDNAME));
		logger.info("rsaPubKeyEI: " + mReg.get(IERSAPUBKEYBASE64));
		
		enrollDevice(mReg);
	}

	public static void enrollDevice(Map<String, String> mReg) {
		Agent agent = new Agent();
		try {
			agent.initializeWithoutProfiles();
		} catch (IonicException e1) {
			errorOut("Failed to initialize the Ionic SDK: "
					+ e1.getMessage());
		}
		CreateDeviceRequest createDeviceRequest = new CreateDeviceRequest(
				"example", mReg.get(IONICURL_FIELDNAME),
				mReg.get(ENROLLMENTTAG_FIELDNAME), mReg.get(TOKEN_FIELDNAME),
				mReg.get(UIDAUTH_FIELDNAME), mReg.get(IERSAPUBKEYBASE64));
		
		CreateDeviceResponse createDeviceResponse = null;
		try {
			createDeviceResponse = agent.createDevice(createDeviceRequest);
			if (createDeviceResponse.getServerErrorCode() != 0) {
				errorOut("create device failed: "
						+ createDeviceResponse.getServerErrorMessage());
			}
		} catch (IonicException e1) {
			errorOut("create device failed: " + e1.getMessage());
		}
		if (createDeviceResponse == null) {
			errorOut("no response from create device");
		}

		String defaultPath = DEFAULT_PTSTORE_PATH;
		System.out.print("Enter the path where to store the secret share profile ["
						 + defaultPath + "]: ");
		System.out.flush();
		String storePath = sc.nextLine();
		sc.close();
		if (storePath == null || storePath.length() < 1) {
			storePath = defaultPath;
		}
		storePath = storePath.replaceFirst("^~",
				System.getProperty("user.home"));

		DeviceProfilePersistorBase deviceProfilePersistor;
		
		// ====
		// PlainText Profile Persistor
		// REPLACE WITH OTHER PERSISTOR LOGIC
		DeviceProfilePersistorPlainText plainTextProfilePersistor = new DeviceProfilePersistorPlainText();
		try {
			plainTextProfilePersistor.setFilePath(storePath);
		} catch (IonicException e) {
			errorOut("failed to create a plaintext persistor: " + e.getMessage());
			return;
		}
		deviceProfilePersistor = plainTextProfilePersistor;
		// ====
		
		DeviceProfile deviceProfile = createDeviceResponse.getDeviceProfile();

		// attempt to load any existing profiles
		try {
			agent.loadProfiles(deviceProfilePersistor);
			List<DeviceProfile> deviceProfiles = agent.getAllProfiles();
			for (DeviceProfile profile : deviceProfiles) {
				logger.info("loaded profile: " + profile.getDeviceId());
			}
		} catch (IonicException e) {
			if (e.getReturnCode() != AgentErrorModuleConstants.ISAGENT_LOAD_PROFILES_FAILED.value()
				&& e.getReturnCode() != AgentErrorModuleConstants.ISAGENT_NO_DEVICE_PROFILE.value()) {
				errorOut("Ionic SDK exception: "+ e.getMessage());
			}
			logger.info("The persistor file doesn't exist yet or can't be opened");
		}
		agent.addProfile(deviceProfile);
		if (!agent.hasActiveProfile()) {
			if (!agent.setActiveProfile(deviceProfile.getDeviceId())) {
				System.out.println("Failed to set device: "
						+ deviceProfile.getDeviceId() + "to active");
			} else {
				System.out.println("setting device: "
						+ deviceProfile.getDeviceId() + " to active");
			}
		}
		try {
			agent.saveProfiles(deviceProfilePersistor);
		} catch (IonicException e) {
			errorOut("Failed to save the profile: " + e.getMessage());
		}
		System.out.println("SEP saved to \"" + storePath + "\"");
	}

	private static Map<String, String> getIonicRegViaSAML(String serverURL,
			String samlAssertionfilePath) throws Exception {
		// Open connection to enrollment portal
		URL enrollServerURI = new URL(serverURL);
		HttpsURLConnection uc = (HttpsURLConnection) enrollServerURI
				.openConnection();
		uc.setUseCaches(false);
		uc.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED);
		uc.setDoOutput(true);
		uc.setRequestMethod("POST");

		// Send SAML Assertion to enrollment portal
		String samlXmlString = new String(Files.readAllBytes(Paths
				.get(samlAssertionfilePath)));
		OutputStream output = uc.getOutputStream();
		output.write("SAMLResponse=".getBytes());
		output.write(URLEncoder.encode(samlXmlString, "UTF-8").getBytes());
		output.close();
		if (uc.getResponseCode() != 200) {
			dumpUCResponse(uc);
			throw new Exception("Bad response code from enrollment server");
		}

		// The field names for token registration data returned from enrollment
		// server
		Map<String, String> mReg = new HashMap<String, String>();
		mReg.put(UIDAUTH_FIELDNAME, uc.getHeaderField(UIDAUTH_FIELDNAME));
		mReg.put(IONICURL_FIELDNAME, uc.getHeaderField(IONICURL_FIELDNAME));
		mReg.put(RSAPUBKEYURL_FIELDNAME,
				uc.getHeaderField(RSAPUBKEYURL_FIELDNAME));
		mReg.put(ENROLLMENTTAG_FIELDNAME,
				uc.getHeaderField(ENROLLMENTTAG_FIELDNAME));
		mReg.put(TOKEN_FIELDNAME, uc.getHeaderField(TOKEN_FIELDNAME));

		if (mReg.get(UIDAUTH_FIELDNAME) == null
				|| mReg.get(IONICURL_FIELDNAME) == null
				|| mReg.get(RSAPUBKEYURL_FIELDNAME) == null
				|| mReg.get(ENROLLMENTTAG_FIELDNAME) == null
				|| mReg.get(TOKEN_FIELDNAME) == null) {
			dumpUCResponse(uc);
			throw new Exception("Bad response from enrollment server");
		}
		mReg.put(IERSAPUBKEYBASE64,
				getRsaPubKey(mReg.get(RSAPUBKEYURL_FIELDNAME)));
		return mReg;
	}

	private static Map<String, String> getIonicRegViaLE(String serverURL,
			String emailAddress) throws Exception {
		// Connect to enrollment portal
		URL enrollServerURI = new URL(serverURL);
		HttpsURLConnection uc = (HttpsURLConnection) enrollServerURI
				.openConnection();
		uc.setUseCaches(false);
		uc.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED);
		uc.setDoOutput(true);
		uc.setRequestMethod("POST");

		// Send email address to enrollment portal
		OutputStream output = uc.getOutputStream();
		output.write("email=".getBytes());
		output.write(URLEncoder.encode(emailAddress, "UTF-8").getBytes());
		output.close();
		if (uc.getResponseCode() != 200) {
			dumpUCResponse(uc);
			throw new Exception("Bad response code from enrollment server");
		}

		// The field names for token registration data returned from enrollment
		// server
		Map<String, String> mReg = new HashMap<String, String>();
		mReg.put(UIDAUTH_FIELDNAME, uc.getHeaderField(UIDAUTH_FIELDNAME));
		mReg.put(IONICURL_FIELDNAME, uc.getHeaderField(IONICURL_FIELDNAME));
		mReg.put(RSAPUBKEYURL_FIELDNAME,
				uc.getHeaderField(RSAPUBKEYURL_FIELDNAME));
		mReg.put(ENROLLMENTTAG_FIELDNAME,
				uc.getHeaderField(ENROLLMENTTAG_FIELDNAME));

		if (mReg.get(UIDAUTH_FIELDNAME) == null
				|| mReg.get(IONICURL_FIELDNAME) == null
				|| mReg.get(RSAPUBKEYURL_FIELDNAME) == null
				|| mReg.get(ENROLLMENTTAG_FIELDNAME) == null) {
			dumpUCResponse(uc);
			throw new Exception("Bad response from enrollment server");
		}
		mReg.put(IERSAPUBKEYBASE64,
				getRsaPubKey(mReg.get(RSAPUBKEYURL_FIELDNAME)));
		return mReg;
	}

	private static String getRsaPubKey(String rsaServerURL) throws Exception {
		try {
			URL url = new URL(rsaServerURL);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setUseCaches(false);
			if (uc.getResponseCode() != 200)
				throw new IOException();
			InputStream response = uc.getInputStream();
			String responseString = IOUtils.toString(response, "UTF-8");
			int index = responseString.lastIndexOf('=');
			responseString = responseString.substring(0, index + 1);
			responseString = responseString.replaceAll("\\s|\n", "");
			IOUtils.closeQuietly(response);
			return responseString;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage()
					+ ": Can't get RSA Public Key from Enrollment Server");
		}
	}

	private static void dumpUCResponse(HttpsURLConnection uc) {
		StringBuilder builder = new StringBuilder();
		try {
			builder.append(uc.getResponseCode()).append(" ")
					.append(uc.getResponseMessage()).append("\n");
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
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
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