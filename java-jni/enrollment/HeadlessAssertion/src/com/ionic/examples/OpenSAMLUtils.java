package com.ionic.examples;

import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;

import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.PrintWriter;
import java.io.StringWriter;

public class OpenSAMLUtils {
    private static RandomIdentifierGenerationStrategy secureRandomIdGenerator;

    static {
        secureRandomIdGenerator = new RandomIdentifierGenerationStrategy();
    }

    @SuppressWarnings("unchecked")
	public static <T> T buildSAMLObject(final Class<T> clazz) {
        T object = null;
        try {
            XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            QName defaultElementName = (QName)clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
            object = (T)builderFactory.getBuilder(defaultElementName).buildObject(defaultElementName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create SAML object");
        }

        return object;
    }

    public static String generateSecureRandomId() {
        return secureRandomIdGenerator.generateIdentifier();
    }

    public static void writeSAMLObject(final XMLObject object, PrintWriter writer) {
        try {
			String xmlString = getSAMLObjectXMLString(object);
			if (writer != null) {
				writer.print(xmlString);
				writer.close();
			}
		} catch (TransformerFactoryConfigurationError | TransformerException | MarshallingException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to write saml object: " + e.getMessage());
		}
    }
    
    
    public static String getSAMLObjectXMLString(final XMLObject object) 
    		throws TransformerFactoryConfigurationError, TransformerException, MarshallingException {
    	Element element = null;

    	if (object instanceof SignableSAMLObject && ((SignableSAMLObject)object).isSigned() && object.getDOM() != null) {
    		element = object.getDOM();
    	} else {
    		Marshaller out = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(object);
    		out.marshall(object);
    		element = object.getDOM();
    	}

    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
   
    	StreamResult result = new StreamResult(new StringWriter());
    	DOMSource source = new DOMSource(element);

    	transformer.transform(source, result);
    	String xmlString = result.getWriter().toString();
    	return xmlString;

    }
}
