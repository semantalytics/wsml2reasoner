package org.wsml.reasoner.builtin.kaon2;

import org.semanticweb.kaon2.extensionapi.datatype.DatatypeHandler;

/**
 * This is the datatype handler for WsmlBoolean objects
 */
public class WsmlBooleanlDatatypeHandler implements DatatypeHandler {
    protected static final String[] NO_URIS = new String[0];

    protected static final String DATATYPE_URI = "http://www.fzi.de/ipe/datatypes/wsmlBoolean";
    
    public boolean handles(Object object) {
        return object instanceof WsmlBoolean;
    }

    public String getDatatypeURI() {
        return DATATYPE_URI;
    }

    public String[] getAdditionalDatatypeURIs() {
        return NO_URIS;
    }

    public String toString(Object object) {
        return ((WsmlBoolean) object).toString();
    }

    public Object parseObject(String objectValue) {
        Boolean boolValue = new Boolean(objectValue);
        return new WsmlBoolean(boolValue);
    }


}
