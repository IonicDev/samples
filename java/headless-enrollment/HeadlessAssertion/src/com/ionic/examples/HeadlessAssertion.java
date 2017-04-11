package com.ionic.examples;

import java.io.PrintWriter;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.*;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.saml2.core.*;

import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;


public class HeadlessAssertion  {
	private Assertion assertion;
	
	/**
	 * Generate a SAML assertion for the username in response to a request id 
	 * @param username
	 * 		Ionic username for the identity that we are asserting
	 * @param inResponseTo
	 * 		ID for the response to field
	 * 
	 */
	public HeadlessAssertion(String username, String inResponseTo) {
		assertion = buildAssertion(username, inResponseTo);
		// we don't need to sign the assertion.  The response is signed.
		//signAssertion(assertion);
	}
	
	/**
	 * Write out the assertion in XML format to the outputStream
	 * @param outputStream
	 */
	public void write(PrintWriter outputStream) {
		 OpenSAMLUtils.writeSAMLObject(assertion, outputStream);
	}
	
	/**
	 * Sign the assertion with our credentials
	 * @param assertion
	 */
    private void signAssertion(Assertion assertion) {
        Signature signature = OpenSAMLUtils.buildSAMLObject(Signature.class);
        signature.setSigningCredential(IDPCredentials.getCredential());
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        assertion.setSignature(signature);

        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
            Signer.signObject(signature);
        } catch (MarshallingException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a new assertion for Ionic user <username> and in response to <inResponseTo> ID
     * @param username
     * @param inResponseTo
     * @return
     */
    private Assertion buildAssertion(String username, String inResponseTo) {

        Assertion assertion = OpenSAMLUtils.buildSAMLObject(Assertion.class);

        Issuer issuer = OpenSAMLUtils.buildSAMLObject(Issuer.class);
        issuer.setValue(IDPConstants.IDP_ENTITY_ID);
        assertion.setIssuer(issuer);
        assertion.setIssueInstant(new DateTime());

        assertion.setID(OpenSAMLUtils.generateSecureRandomId());

        Subject subject = OpenSAMLUtils.buildSAMLObject(Subject.class);
        assertion.setSubject(subject);

        NameID nameID = OpenSAMLUtils.buildSAMLObject(NameID.class);
        
        nameID.setFormat(NameIDType.EMAIL);
        nameID.setValue("email");
 
        subject.setNameID(nameID);
        subject.getSubjectConfirmations().add(buildSubjectConfirmation(inResponseTo));

        assertion.setConditions(buildConditions());
        assertion.getAttributeStatements().add(buildAttributeStatement(username));
        assertion.getAuthnStatements().add(buildAuthnStatement());

        return assertion;
    }

    /**
     * Fill out the SubjectConfirmation element.  Set the method to Bearer and the recipient to our
     * enrollment endpoint
     * @param inResponseTo
     * @return
     */
    private SubjectConfirmation buildSubjectConfirmation(String inResponseTo) {
        SubjectConfirmation subjectConfirmation = OpenSAMLUtils.buildSAMLObject(SubjectConfirmation.class);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
      
        SubjectConfirmationData subjectConfirmationData = OpenSAMLUtils.buildSAMLObject(SubjectConfirmationData.class);
        subjectConfirmationData.setInResponseTo(inResponseTo);
        subjectConfirmationData.setNotBefore(new DateTime().minusDays(EnrollmentConstants.VALID_DAYS_BEFORE));
        subjectConfirmationData.setNotOnOrAfter(new DateTime().plusDays(EnrollmentConstants.VALID_DAYS_AFTER));
        subjectConfirmationData.setRecipient(EnrollmentConstants.ENROLLMENT_ENDPOINT);

        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        return subjectConfirmation;
    }

    /**
     * Fill out the AuthnStatement element.  We are using Password context
     * @return
     */
    private AuthnStatement buildAuthnStatement() {
        AuthnStatement authnStatement = OpenSAMLUtils.buildSAMLObject(AuthnStatement.class);
        AuthnContext authnContext = OpenSAMLUtils.buildSAMLObject(AuthnContext.class);
        AuthnContextClassRef authnContextClassRef = OpenSAMLUtils.buildSAMLObject(AuthnContextClassRef.class);
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);

        authnStatement.setAuthnInstant(new DateTime());

        return authnStatement;
    }

    /**
     * Fill out the Conditions element.  Our Audience URI is fill in from the ACS SPConstant
     * @return
     */
    private Conditions buildConditions() {
        Conditions conditions = OpenSAMLUtils.buildSAMLObject(Conditions.class);
        conditions.setNotBefore(new DateTime().minusDays(EnrollmentConstants.VALID_DAYS_BEFORE));
        conditions.setNotOnOrAfter(new DateTime().plusDays(EnrollmentConstants.VALID_DAYS_AFTER));
        AudienceRestriction audienceRestriction = OpenSAMLUtils.buildSAMLObject(AudienceRestriction.class);
        Audience audience = OpenSAMLUtils.buildSAMLObject(Audience.class);
        audience.setAudienceURI(SPConstants.ASSERTION_CONSUMER_SERVICE);
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        return conditions;
    }

    /**
     * Fill out the attributes.  We use one "email" attribute with the user name as the value
     * @param username
     * @return
     */
    private AttributeStatement buildAttributeStatement(String username) {
        AttributeStatement attributeStatement = OpenSAMLUtils.buildSAMLObject(AttributeStatement.class);

        Attribute attributeUserName = OpenSAMLUtils.buildSAMLObject(Attribute.class);

        XSStringBuilder stringBuilder = (XSStringBuilder)XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString userNameValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        userNameValue.setValue(username);

        attributeUserName.getAttributeValues().add(userNameValue);
        attributeUserName.setName("email");
        attributeStatement.getAttributes().add(attributeUserName);

        return attributeStatement;

    }
    
    /**
     * return the assertion object
     * @return
     */
    public Assertion getAssertion() {
    	return assertion;
    }
}
