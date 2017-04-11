package com.ionic.examples;

import com.ionicsecurity.sdk.MetadataMap;

public class IonicApplicationMetadata {
	public static final String ApplicationName = "IonicRegister";
	public static final String ApplicationVersion = "0.0.1";
	public static final String ClientType = "Java Application";
	public static final String ClientVersion = "0.0";
	private static final MetadataMap mApplicationMetadata;
	static {
		mApplicationMetadata = new MetadataMap();
		mApplicationMetadata.set("ionic-application-name", ApplicationName);
		mApplicationMetadata.set("ionic-application-version", ApplicationVersion);
		mApplicationMetadata.set("ionic-client-type", ClientType);
		mApplicationMetadata.set("ionic-client-version", ClientVersion);
	}
	
	public static MetadataMap getMetadataMap() {
		return mApplicationMetadata;
	}
}
