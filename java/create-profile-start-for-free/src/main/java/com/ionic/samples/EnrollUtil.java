package com.ionic.samples;

import com.ionic.sdk.agent.config.AgentConfig;
import com.ionic.sdk.agent.transaction.AgentTransactionUtil;
import com.ionic.sdk.core.codec.Transcoder;
import com.ionic.sdk.core.io.Stream;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.error.SdkData;
import com.ionic.sdk.error.SdkError;
import com.ionic.sdk.httpclient.Http;
import com.ionic.sdk.httpclient.HttpClient;
import com.ionic.sdk.httpclient.HttpClientDefault;
import com.ionic.sdk.httpclient.HttpRequest;
import com.ionic.sdk.httpclient.HttpResponse;
import com.ionic.sdk.json.JsonSource;
import com.ionic.sdk.json.JsonU;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URL;

/**
 * Utility methods associated with Ionic device enrollment.
 */
public class EnrollUtil {

    /**
     * Given an Ionic keyspace, look up the Ionic authorization enrollment URL.
     *
     * @param keyspace the Ionic keyspace of the key server
     * @return the Ionic authorization enrollment URL
     * @throws IonicException on failure to fetch the identity sources JSON, or to retrieve the expected data from it
     */
    public static String getEnrollmentURL(final String keyspace) throws IonicException {
        final URL url = AgentTransactionUtil.getProfileUrl(
                "https://enrollment.ionic.com/keyspace/" + keyspace + "/identity_sources");
        final HttpClient httpClient = new HttpClientDefault(new AgentConfig(), url.getProtocol());
        final HttpRequest httpRequest = new HttpRequest(url, Http.Method.GET, url.getFile());
        try {
            final HttpResponse httpResponse = httpClient.execute(httpRequest);
            final byte[] responseEntity = Stream.read(httpResponse.getEntity());
            final JsonObject jsonEntity = JsonU.getJsonObject(Transcoder.utf8().encode(responseEntity));
            final JsonObject jsonIdentitySources = JsonSource.getJsonObject(jsonEntity, "identitySources");
            SdkData.checkTrue(jsonIdentitySources != null, SdkError.ISAGENT_MISSINGVALUE);
            final JsonArray jsonEnrollIDC = JsonSource.getJsonArray(jsonIdentitySources, "IDC");
            SdkData.checkTrue(jsonEnrollIDC != null, SdkError.ISAGENT_MISSINGVALUE);
            final JsonObject jsonMethod = (JsonObject) JsonSource.getIterator(jsonEnrollIDC).next();
            return JsonSource.getString(jsonMethod, "uri");
        } catch (IOException e) {
            throw new IonicException(SdkError.ISAGENT_REQUESTFAILED);
        }
    }
}
