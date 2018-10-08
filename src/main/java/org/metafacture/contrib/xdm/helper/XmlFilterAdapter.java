/*
 * Copyright 2018 Deutsche Nationalbibliothek
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.metafacture.contrib.xdm.helper;

import org.metafacture.framework.XmlReceiver;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;

/**
 * Adapter class that wraps a XmlReceiver inside a XMLFilter.
 */
public class XmlFilterAdapter extends XMLFilterImpl {
    private XmlReceiver receiver;

    public XmlFilterAdapter(XmlReceiver receiver) {
        this.receiver = receiver;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return receiver.resolveEntity(publicId, systemId);
    }

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        receiver.notationDecl(name, publicId, systemId);
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId,
                                   String systemId, String notationName) throws SAXException {
        receiver.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        receiver.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        receiver.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        receiver.endDocument();
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
            throws SAXException {
        receiver.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        receiver.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        receiver.characters(ch, start, length);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        receiver.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        receiver.endPrefixMapping(prefix);
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        receiver.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        receiver.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        receiver.skippedEntity(name);
    }

    public void warning(SAXParseException e) throws SAXException {
        receiver.warning(e);
    }

    public void error(SAXParseException e) throws SAXException {
        receiver.error(e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        receiver.fatalError(e);
    }
}
