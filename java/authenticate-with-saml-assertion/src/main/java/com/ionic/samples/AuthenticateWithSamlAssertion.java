/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 * 
 * This sample can be used to submit a SAML Assertion created through a user-workflow,
 * or to submit a Generated SAML Assertion (e.g., from ionic-assertion).
 * Ensure the correct enrollment endpoint is used for the key the assertion is signed with.
 */

package com.ionic.samples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;

public class AuthenticateWithSamlAssertion
{
    public static void main(String[] args)
    {
        String serverUrl = "https://dev-enrollment.ionic.com/keyspace/HVzG/saml";
        String samlAssertionPath = "./assertion.xml";

        final String HEADER_CONTENT_TYPE = "Content-Type";
        final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
        final String UIDAUTH_FIELDNAME = "X-Ionic-Reg-Uidauth";
        final String IONICURL_FIELDNAME = "X-Ionic-Reg-Ionic-Url";
        final String RSAPUBKEYURL_FIELDNAME = "X-Ionic-Reg-Pubkey-Url";
        final String ENROLLMENTTAG_FIELDNAME = "X-Ionic-Reg-Enrollment-Tag";
        final String TOKEN_FIELDNAME = "X-Ionic-Reg-SToken";

        // Send SAML Assertion to enrollment portal
        String sIonicAssertion = null;
        String sApiUrl = null;
        String sKeyspace = null;
        String sEsPubKeyUrl = null;
        String sToken = null;
        try {
            URL enrollServerURI = new URL(serverUrl);
            HttpsURLConnection uc = (HttpsURLConnection) enrollServerURI.openConnection();
            uc.setUseCaches(false);
            uc.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED);
            uc.setDoOutput(true);
            uc.setRequestMethod("POST");
            String samlXmlString = new String(Files.readAllBytes(Paths.get(samlAssertionPath)));
            OutputStream output = uc.getOutputStream();
            output.write("SAMLResponse=".getBytes());
            output.write(URLEncoder.encode(samlXmlString, "UTF-8").getBytes());
            output.close();
        if (uc.getResponseCode() != 200) {
            System.out.println("Bad response code from enrollment server");
            System.exit(1);
        }
        sIonicAssertion = uc.getHeaderField(UIDAUTH_FIELDNAME);
        sApiUrl = uc.getHeaderField(IONICURL_FIELDNAME);
        sKeyspace = uc.getHeaderField(ENROLLMENTTAG_FIELDNAME);
        sEsPubKeyUrl = uc.getHeaderField(RSAPUBKEYURL_FIELDNAME);
        sToken = uc.getHeaderField(TOKEN_FIELDNAME);
        } catch (IOException e) {
            System.out.println("Request to enrollment service failed");
            System.out.println(e);
            System.exit(1);
        }

        // fetch enrollment service public key
        String sEsPubKey = null;
        try {
            URL esPubKeyUrl = new URL(sEsPubKeyUrl);
            HttpURLConnection esPubKeyHttpRequest = (HttpURLConnection) esPubKeyUrl.openConnection();
            esPubKeyHttpRequest.setUseCaches(false);
            if (esPubKeyHttpRequest.getResponseCode() != 200) {
                System.out.println("Bad response code from enrollment service when requesting public key");
                System.exit(1);
            }
            InputStream esPubKeyHttpResponse = esPubKeyHttpRequest.getInputStream();
            String responseString = IOUtils.toString(esPubKeyHttpResponse, "UTF-8");
            int index = responseString.lastIndexOf('=');
            responseString = responseString.substring(0, index + 1);
            responseString = responseString.replaceAll("\\s|\n", "");
            IOUtils.closeQuietly(esPubKeyHttpResponse);
            sEsPubKey = responseString;
        } catch (IOException e) {
            System.out.println("Failed to request public key from enrollment service");
            System.out.println(e);
            System.exit(1);
        }

        System.out.println("\nKEYSPACE: " + sKeyspace);
        System.out.println("\nAPI_URL: " + sApiUrl);
        System.out.println("\nES_PUBLIC_KEY:\n" + sEsPubKey);
        System.out.println("\nIONIC_ASSERTION:\n" + sIonicAssertion);
        System.out.println("\nTOKEN: " + sToken);
    }
}
