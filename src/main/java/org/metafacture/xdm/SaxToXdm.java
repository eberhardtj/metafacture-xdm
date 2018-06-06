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
package org.metafacture.xdm;

import net.sf.saxon.s9api.*;
import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.XmlReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultXmlPipe;
import org.metafacture.framework.XdmReceiver;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

@In(XmlReceiver.class)
@Out(XdmReceiver.class)
@Description("Transforms sax documents into xdm nodes.")
@FluxCommand("sax-to-xdm")
public class SaxToXdm extends DefaultXmlPipe<XdmReceiver> {
    private Processor processor;
    private DocumentBuilder documentBuilder;
    private BuildingContentHandler buildingContentHandler;

    public SaxToXdm() {
        this.processor = new Processor(false);
        this.documentBuilder = processor.newDocumentBuilder();
        this.buildingContentHandler = newBuildingContentHandler();
    }

    private BuildingContentHandler newBuildingContentHandler() {
        try {
            return documentBuilder.newBuildingContentHandler();
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void startDocument() {
        buildingContentHandler = newBuildingContentHandler();

        try {
            buildingContentHandler.startDocument();
        } catch (SAXException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void endDocument() {
        try {
            buildingContentHandler.endDocument();
            XdmNode node = buildingContentHandler.getDocumentNode();
            getReceiver().process(node);
        } catch (SAXException | SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void startElement(final String uri, final String localName,
                             final String qName, final Attributes attributes) {
        try {
            buildingContentHandler.startElement(uri, localName, qName, attributes);
        } catch (SAXException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        try {
            buildingContentHandler.endElement(uri, localName, qName);
        } catch (SAXException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        try {
            buildingContentHandler.characters(ch, start, length);
        } catch (SAXException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        buildingContentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        buildingContentHandler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        buildingContentHandler.skippedEntity(name);
    }

    @Override
    public void setDocumentLocator(final Locator locator) {
        buildingContentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        buildingContentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        buildingContentHandler.endPrefixMapping(prefix);
    }


}
