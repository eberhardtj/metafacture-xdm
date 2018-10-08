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
package org.metafacture.contrib.xdm;

import net.sf.saxon.s9api.*;
import org.metafacture.contrib.framework.XdmReceiver;
import org.metafacture.contrib.framework.helpers.DefaultXdmPipe;
import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@In(XdmReceiver.class)
@Out(String.class)
@Description("Encodes xdm nodes into the output of a xsl transformation.")
@FluxCommand("encode-transformation")
public class XdmTransformationEncoder extends DefaultXdmPipe<ObjectReceiver<String>> {
    private final Processor processor;
    private XsltTransformer transformer;

    private ByteArrayOutputStream buffer;

    public XdmTransformationEncoder(String stylesheet) {
        this.processor = new Processor(false);
        try {
            this.transformer = processor
                    .newXsltCompiler()
                    .compile(new StreamSource(new File(stylesheet)))
                    .load();
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }

        this.buffer = new ByteArrayOutputStream();
        Serializer serializer = processor.newSerializer(buffer);
        transformer.setDestination(serializer);
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

            buffer.reset();
            processor.writeXdmValue(nodeCopy, transformer);
            String serializedNode = new String(buffer.toByteArray(), StandardCharsets.UTF_8);

            getReceiver().process(serializedNode);
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public void onResetStream() {
        buffer.reset();
    }

    @Override
    public void onCloseStream() {
        try {
            buffer.close();
        } catch (IOException e) {
            throw new MetafactureException(e);
        }
    }
}
