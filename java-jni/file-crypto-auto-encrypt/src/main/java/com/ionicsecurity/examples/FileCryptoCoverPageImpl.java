/*
 * (c) 2017 Ionic Security Inc. By using this code, I agree to the Terms & Conditions
 * (https://www.ionic.com/terms-of-use/) and the Privacy Policy
 * (https://www.ionic.com/privacy-notice/).
 */

package com.ionicsecurity.examples;

import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.ionicsecurity.sdk.Agent;
import com.ionicsecurity.sdk.FileCryptoCoverPageServicesDefault;
import com.ionicsecurity.sdk.FileCryptoCoverPageServicesInterface;
import com.ionicsecurity.sdk.FileType;
import com.ionicsecurity.sdk.GetResourcesRequest;
import com.ionicsecurity.sdk.GetResourcesResponse;
import com.ionicsecurity.sdk.Log;
import com.ionicsecurity.sdk.SdkException;

/**
 * A class to handle cover page caching on a per SEP (device+user+tenant) basis
 * 
 */
public class FileCryptoCoverPageImpl extends FileCryptoCoverPageServicesInterface
    implements Callable<CoverPageEntry> {
  static final private String logChannel = "ISFileCoverPage";
  static private Cache<ArrayList<String>, CoverPageEntry> coverCache;

  private Agent agent;
  private FileType fileType;
  private CoverPageEntry oldEntry;

  /**
   * Create an instance of the class for an agent. This should be done for each FileCipher
   * operation.
   * 
   * @param agent
   */
  public FileCryptoCoverPageImpl(Agent agent) {
    this.agent = agent;
  }

  /**
   * Initialize the cache. This must be done ONCE prior to creating instances of this class
   */
  public static void cacheInit() {
    coverCache = CacheBuilder.newBuilder().maximumSize(5)
        .removalListener(new RemovalListener<ArrayList<String>, CoverPageEntry>() {
          public void onRemoval(RemovalNotification<ArrayList<String>, CoverPageEntry> removal) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            Log.dc(this.getClass(), methodName, logChannel,
                "removal: " + removal.getKey() + "/" + removal.getValue());
            Log.dc(this.getClass(), methodName, logChannel, "removal cause: " + removal.getCause());
          }
        }).build();
  }

  /**
   * This method is called by a FileCipher class instance in order to retrieve a page to display on
   * access denied. We aren't really implementing this as the example will not decrypt ciphertext
   * let alone display any content.
   * @param fileType
   * 
   * @throws Exception
   */
  @Override
  public byte[] getAccessDeniedPage(FileType fileType) throws Exception {
    FileCryptoCoverPageServicesDefault def = new FileCryptoCoverPageServicesDefault();
    return def.getCoverPage(fileType);
  }

  /**
   * A convenience method to pull out the string needed by the SDK
   * 
   * @return
   */
  private String getFileTypeString() {
    // remove the 'FILETYPE_' characters from the beginning of the enum to
    // get the filetype for the resource request
    return fileType.toString().substring(9).toLowerCase();
  }

  /**
   * Generates the cache entry key from the SEP.deviceID and the filetype string
   * 
   * @param fileType
   * @return
   */
  private ArrayList<String> getEntryKey(FileType fileType) {
    String deviceId = agent.getActiveProfile().getDeviceId();

    // we will use the device id and the file type as the key to our cache
    // entry
    return new ArrayList<String>(Arrays.asList(deviceId, getFileTypeString()));
  }

  /**
   * Return the file content from the cache entry or attempt to use this instance as a callable to
   * retrieve a new entry
   * 
   * @param fileType
   */
  @Override
  public byte[] getCoverPage(FileType fileType) throws Exception {
    this.fileType = fileType;
    ArrayList<String> currPair = getEntryKey(fileType);

    Calendar cal = Calendar.getInstance();

    CoverPageEntry val = coverCache.get(currPair, this);
    if (val.getExpiration() < cal.get(Calendar.SECOND)) { // Expired try to fetch a new entry
      String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
      Log.dc(this.getClass(), methodName, logChannel, "updating a stale entry");
      oldEntry = val;
      coverCache.invalidate(currPair);
      val = coverCache.get(currPair, this);
    }
    return val.getFile();

  }

  /**
   * This method gets called by Guava as the cache loader on a cache miss
   * 
   * @throws Exception
   */
  public CoverPageEntry call() throws Exception {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    Log.dc(this.getClass(), methodName, logChannel, "attempting to load the coverpage cache");

    GetResourcesRequest.Resource res;

    if (oldEntry == null || oldEntry.getHash() == null) {
      // no hash to send, so just request the page
      res = new GetResourcesRequest.Resource("coverpage",
          "{\"format\":\"" + getFileTypeString() + "\"}");
    } else { // send a hash: we will just get a hash back if it hasn't
             // changed or a new page if it has
      res = new GetResourcesRequest.Resource("coverpage", "{\"format\":\"" + getFileTypeString()
          + "\"" + ",\"hash\":\"" + oldEntry.getHash() + "\"}");
    }

    JSONObject obj;
    int newExpiration;
    FileCryptoCoverPageServicesDefault def = new FileCryptoCoverPageServicesDefault();
    try {
      GetResourcesResponse resResponse = agent.getResource(res);
      List<GetResourcesResponse.Resource> resList = resResponse.getResources();
      String resString = resList.get(0).getData();
      obj = new JSONObject(resString);
      newExpiration = Integer.parseInt(obj.getString("ttlseconds"));
    } catch (SdkException | JSONException e) {
      Log.dc(this.getClass(), methodName, logChannel, "error on attempt to get cache page");
      if (oldEntry != null) { // on error: return the oldEntry if it
                              // exists... note expiration is NOT updated
        Log.dc(this.getClass(), methodName, logChannel, "using the old entry");
        return oldEntry;
      }
      return new CoverPageEntry(def.getCoverPage(this.fileType), null, 0);
    }

    if (!obj.has("hash") && !obj.has("file")) { // response indicates no cover page support in
                                                // this tenant so ignore oldEntry if it exists
      // stick the default page in the cache with the ttlseconds to
      // prevent a repeated request
      Log.dc(this.getClass(), methodName, logChannel, "no cover page support in this tenant");
      return new CoverPageEntry(def.getCoverPage(this.fileType), null, newExpiration);
    } else if (!obj.has("file") && oldEntry != null && oldEntry.getHash() != null) { // the hash
                                                                                     // sent matched
      Calendar cal = Calendar.getInstance();
      oldEntry.setExpiration(newExpiration + cal.get(Calendar.SECOND));
      Log.dc(this.getClass(), methodName, logChannel,
          "hash match: updating the ttl for the cover page");
      return oldEntry;
    } else { // hash mismatch or first time we had an entry so create a new
             // one
      Log.dc(this.getClass(), methodName, logChannel,
          "hash mismatch or first time request: updating cache entry");
      String newHash = obj.getString("hash");
      byte[] newFile = Base64.decodeBase64(obj.getString("file").getBytes());
      return new CoverPageEntry(newFile, newHash, newExpiration);
    }
  }

}
