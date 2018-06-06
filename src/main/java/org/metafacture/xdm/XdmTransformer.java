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
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.DefaultXdmPipe;
import org.metafacture.framework.XdmReceiver;

import javax.xml.transform.stream.StreamSource;
import java.io.File;

@In(XdmReceiver.class)
@Out(XdmReceiver.class)
@Description("Transforms a xdm node using a xsl stylesheet.")
@FluxCommand("transform")
public class XdmTransformer implements DefaultXdmPipe<XdmReceiver> {
    private final Processor processor;
    private XsltTransformer transformer;
    private XdmDestination destination;

    private XdmReceiver receiver;

    public XdmTransformer(String stylesheet) {
        this.processor = new Processor(false);
        try {
            this.transformer = processor
                    .newXsltCompiler()
                    .compile(new StreamSource(new File(stylesheet)))
                    .load();
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }

        this.destination = new XdmDestination();
        transformer.setDestination(destination);
    }

    public void setStylesheet(String stylesheet) {
        try {
            this.transformer = processor
                    .newXsltCompiler()
                    .compile(new StreamSource(new File(stylesheet)))
                    .load();
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void process(final XdmNode node) {
        try {
            XdmNode nodeCopy = processor.newDocumentBuilder().build(node.asSource());
            processor.writeXdmValue(nodeCopy, transformer);
            XdmNode transformedNode = destination.getXdmNode();
            receiver.process(transformedNode);
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public <R extends XdmReceiver> R setReceiver(R receiver) {
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
