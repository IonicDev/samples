package com.ionic.samples;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.config.AgentConfig;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceRequest;
import com.ionic.sdk.agent.request.createdevice.CreateDeviceResponse;
import com.ionic.sdk.agent.transaction.AgentTransactionUtil;
import com.ionic.sdk.cipher.rsa.model.RsaKeyHolder;
import com.ionic.sdk.core.codec.Transcoder;
import com.ionic.sdk.core.io.Stream;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.error.SdkData;
import com.ionic.sdk.error.SdkError;
import com.ionic.sdk.httpclient.Http;
import com.ionic.sdk.httpclient.HttpClient;
import com.ionic.sdk.httpclient.HttpClientDefault;
import com.ionic.sdk.httpclient.HttpHeaders;
import com.ionic.sdk.httpclient.HttpRequest;
import com.ionic.sdk.httpclient.HttpResponse;
import com.ionic.sdk.httpclient.HttpUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * A utility class used to enroll a new device to an Ionic key tenant via using Ionic (username / password)
 * authentication.
 */
public class EnrollIonicAuth {

    /**
     * The URL of the enrollment server for the desired tenant.
     */
    private final URL url;

    /**
     * The agent used to perform the enrollment.
     */
    private final Agent agent;

    /**
     * The client-side RSA keypair to use in the context of the request.
     */
    private final RsaKeyHolder rsaKeyHolder;

    /**
     * Constructor.  Accept parameters to use for enrollment request.
     *
     * @param url          the URL of the enrollment server
     * @param agent        the agent instance used to perform the enrollment
     * @param rsaKeyHolder the client-side RSA keypair to use in the context of the request
     * @throws IonicException on invalid input URL
     */
    public EnrollIonicAuth(final String url, Agent agent, RsaKeyHolder rsaKeyHolder) throws IonicException {
        this.url = AgentTransactionUtil.getProfileUrl(url);
        this.agent = agent;
        this.rsaKeyHolder = rsaKeyHolder;
    }

    /**
     * Enroll a new device in the Ionic server infrastructure.
     *
     * @param user              the account name of the identity to which the new device profile should be associated
     * @param pass              the password of the identity to which the new device profile should be associated
     * @param deviceProfileName the label for the new device profile
     * @return the {@link CreateDeviceResponse} object containing the new device profile information
     * @throws IonicException on failure of any step in the SAML enrollment process
     */
    public CreateDeviceResponse enroll(
            final String user, final String pass, final String deviceProfileName) throws IonicException {
        try {
            final HttpClient httpClient = new HttpClientDefault(new AgentConfig(), url.getProtocol());
            final HttpResponse httpResponse1 = submitAuthRequest(httpClient, user, pass);
            final HttpResponse httpResponse2 = retrievePubkey(httpClient, httpResponse1);
            return createDevice(httpResponse1, httpResponse2, deviceProfileName);
        } catch (IOException e) {
            throw new IonicException(SdkError.ISAGENT_REQUESTFAILED, e);
        }
    }

    private HttpResponse submitAuthRequest(
            final HttpClient httpClient, final String user, final String pass) throws IOException, IonicException {
        final byte[] entity = Transcoder.utf8().decode(String.format(REQUEST_AUTH,
                HttpUtils.urlEncode(user), HttpUtils.urlEncode(pass)));
        final HttpRequest httpRequest = new HttpRequest(
                url, Http.Method.POST, url.getFile(), new HttpHeaders(), new ByteArrayInputStream(entity));
        final HttpResponse httpResponse = httpClient.execute(httpRequest);
        SdkData.checkTrue(AgentTransactionUtil.isHttpSuccessCode(
                httpResponse.getStatusCode()), SdkError.ISAGENT_REQUESTFAILED);
        return httpResponse;
    }

    /**
     * Acquire the asymmetric Ionic public key, used to build the {@link CreateDeviceRequest}.
     *
     * @param httpClient    the object encapsulating the http transactions needed to enroll a new device
     * @param httpResponse3 the http server response received during step 3 of the enrollment process
     * @return the http server response to this request, which contains the Ionic enrollment public key in its payload
     * @throws IOException    on http failures during the server request
     * @throws IonicException on invalid input URL, or on receipt of an http request error code
     */
    private HttpResponse retrievePubkey(
            final HttpClient httpClient, final HttpResponse httpResponse3) throws IOException, IonicException {
        final String urlPubkey = httpResponse3.getHttpHeaders().getHeaderValue(Header.X_IONIC_REG_PUBKEY_URL);
        final URL url4 = AgentTransactionUtil.getProfileUrl(urlPubkey);
        final HttpRequest httpRequest = new HttpRequest(url4, Http.Method.GET, url4.getFile());
        final HttpResponse httpResponse = httpClient.execute(httpRequest);
        SdkData.checkTrue(AgentTransactionUtil.isHttpSuccessCode(httpResponse.getStatusCode()),
                SdkError.ISAGENT_REQUESTFAILED, SdkError.getErrorString(SdkError.ISAGENT_REQUESTFAILED));
        return httpResponse;
    }

    /**
     * Assemble the enrollment request, submit it to the Ionic infrastructure, and process the response.
     *
     * @param httpResponseAuth   the http server response received during step 3 of the enrollment process
     * @param httpResponsePubkey the http server response received during step 4 of the enrollment process
     * @param deviceProfileName  the label for the new device profile
     * @return the {@link CreateDeviceResponse} to this request, which on success contains the new device profile
     * @throws IOException    on http failures during the server request
     * @throws IonicException on invalid input URL, or on receipt of an http request error code
     */
    private CreateDeviceResponse createDevice(
            final HttpResponse httpResponseAuth, final HttpResponse httpResponsePubkey,
            final String deviceProfileName) throws IOException, IonicException {
        // transact with server
        final String server = httpResponseAuth.getHttpHeaders().getHeaderValue(Header.X_IONIC_REG_IONIC_URL);
        final String keyspace = httpResponseAuth.getHttpHeaders().getHeaderValue(Header.X_IONIC_REG_ENROLLMENT_TAG);
        final String token = httpResponseAuth.getHttpHeaders().getHeaderValue(Header.X_IONIC_REG_STOKEN);
        final String uid = httpResponseAuth.getHttpHeaders().getHeaderValue(Header.X_IONIC_REG_UIDAUTH);
        final byte[] pubkeyBytes = Stream.read(httpResponsePubkey.getEntity());
        final String pubkeyText = Transcoder.utf8().encode(pubkeyBytes).trim();
        agent.initializeWithoutProfiles();
        final CreateDeviceRequest request = new CreateDeviceRequest(
                deviceProfileName, server, keyspace, token, uid, pubkeyText);
        request.setRsaKeyHolder(rsaKeyHolder);
        return agent.createDevice(request);
    }

    private static final String REQUEST_AUTH = "username=%s&password=%s";

    /**
     * Text names associated with http headers used in device enrollment.
     */
    private static class Header {

        /**
         * Http header name used in device enrollment.
         */
        private static final String X_IONIC_REG_ENROLLMENT_TAG = "X-Ionic-Reg-Enrollment-Tag";

        /**
         * Http header name used in device enrollment.
         */
        private static final String X_IONIC_REG_IONIC_URL = "X-Ionic-Reg-Ionic-Url";

        /**
         * Http header name used in device enrollment.
         */
        private static final String X_IONIC_REG_PUBKEY_URL = "X-Ionic-Reg-Pubkey-Url";

        /**
         * Http header name used in device enrollment.
         */
        private static final String X_IONIC_REG_STOKEN = "X-Ionic-Reg-Stoken";

        /**
         * Http header name used in device enrollment.
         */
        private static final String X_IONIC_REG_UIDAUTH = "X-Ionic-Reg-Uidauth";
    }
}
