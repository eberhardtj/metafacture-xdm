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

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.DefaultXdmPipe;
import org.metafacture.framework.XdmReceiver;

@In(XdmReceiver.class)
@Out(String.class)
@Description("Encodes xdm nodes into their xml equivalent.")
@FluxCommand("xdm-to-xml")
public class XdmXmlEncoder implements DefaultXdmPipe<ObjectReceiver<String>> {
    private ObjectReceiver<String> receiver;
    private Processor processor;
    private Serializer serializer;

    public XdmXmlEncoder() {
        this.processor = new Processor(false);
        this.serializer = processor.newSerializer();

        this.serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
        this.serializer.setOutputProperty(Serializer.Property.ENCODING, "UTF-8");
        this.serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
        this.serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
    }

    public void setEncoding(String encoding) {
        serializer.setOutputProperty(Serializer.Property.ENCODING, encoding);
    }

    public void setOmitDeclaration(boolean omit) {
        if (omit) {
            serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
        } else {
            serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
        }
    }

    @Override
    public void process(XdmNode node) {
        try {
            String xml = serializer.serializeNodeToString(node);
            receiver.process(xml);
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public <R extends ObjectReceiver<String>> R setReceiver(R receiver) {
        this.receiver = receiver;
        return receiver;
    }

    @Override
    public void resetStream() {
        // Do nothing
    }

    @Override
    public void closeStream() {
        // Do nothing
    }
}
