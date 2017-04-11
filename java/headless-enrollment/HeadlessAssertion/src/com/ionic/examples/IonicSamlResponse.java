package com.ionic.examples;

import java.io.PrintWriter;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

public class IonicSamlResponse {
	private Response response;
	/**
	 * build a SAML response with an assertion for <username> in response to ID <inResponseTo>
	 * @param username
	 * @param inResponseTo
	 */
	public IonicSamlResponse(String username, String inResponseTo) 
	{
		response = buildResponse(username, inResponseTo);
	}
	
	/**
	 * This builds the response, by first creating a new assertion
	 * @param username
	 * @param inResponseTo
	 * @return
	 */
	private Response buildResponse(String username, String inResponseTo)
	{
		Response response = OpenSAMLUtils.buildSAMLObject(Response.class);
		response.setDestination(SPConstants.ASSERTION_CONSUMER_SERVICE);
		response.setIssueInstant(new DateTime());
		response.setID(OpenSAMLUtils.generateSecureRandomId());
		response.setInResponseTo(inResponseTo);
		Issuer issuer = OpenSAMLUtils.buildSAMLObject(Issuer.class);
		issuer.setValue(IDPConstants.IDP_ENTITY_ID);

		response.setIssuer(issuer);

		Status status = OpenSAMLUtils.buildSAMLObject(Status.class);
		StatusCode statusCode = OpenSAMLUtils.buildSAMLObject(StatusCode.class);
		statusCode.setValue(StatusCode.SUCCESS);
		status.setStatusCode(statusCode);

		response.setStatus(status);

		HeadlessAssertion ionicAssertion = new HeadlessAssertion(username, inResponseTo);
		Assertion assertion = ionicAssertion.getAssertion();

		response.getAssertions().add(assertion);
		signResponse(response);

		return response;
	}

	/**
	 * Sign the response with our credentials
	 * @param response
	 */
	private void signResponse(Response response) {
		Signature signature = OpenSAMLUtils.buildSAMLObject(Signature.class);
		signature.setSigningCredential(IDPCredentials.getCredential());

		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_WITH_COMMENTS);
		
		response.setSignature(signature);

		try {
			XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(response).marshall(response);
		} catch (MarshallingException e) {
			throw new RuntimeException(e);
		}

		try {
			Signer.signObject(signature);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generate a XML string representation of the response
	 */
	public String toString() {
		try {
			return OpenSAMLUtils.getSAMLObjectXMLString(response);
		} catch (TransformerFactoryConfigurationError | TransformerException | MarshallingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * write out the XML string representation of the response
	 * @param printWriter
	 */
	public void write(PrintWriter printWriter) {
		 OpenSAMLUtils.writeSAMLObject(response, printWriter);
	}
}
