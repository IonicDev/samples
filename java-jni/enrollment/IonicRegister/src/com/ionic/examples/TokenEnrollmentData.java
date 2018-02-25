/*
 * (c) 2017 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://www.ionic.com/terms-of-use/)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.examples;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A bean for passing the necessary enrollment data around.
 */
public class TokenEnrollmentData {

	private String mUidAuth;
	private String mRsaPubKey;
	private URL mIonicUrl;
	private String mEnrollmentTag;
	private String mSToken;
	
	public TokenEnrollmentData(URL serverUrl, String keySpace, String uidAuth, String rsaPubKey)
	{
		mIonicUrl = serverUrl;
		mEnrollmentTag = keySpace;
		mUidAuth = uidAuth;
		mRsaPubKey = rsaPubKey;
	}
	
	public TokenEnrollmentData(String ionicUrl, String enrollmentTag, String uidAuth, 
			String rsaPubKey, String sToken) throws MalformedURLException
	{
		mIonicUrl = new URL(ionicUrl);
		mEnrollmentTag = enrollmentTag;
		mUidAuth = uidAuth;
		mRsaPubKey = rsaPubKey;
		mSToken = sToken;
	}
	
	public URL getIonicUrl() {
		return mIonicUrl;
	}
	
	public String getEnrollmentTag() {
		return mEnrollmentTag;
	}
	
	public String getUidAuth() {
		return mUidAuth;
	}
	
	public String getRsaPubKey() {
		return mRsaPubKey;
	}
	
	public String getSToken() {
		return mSToken;
	}
}
