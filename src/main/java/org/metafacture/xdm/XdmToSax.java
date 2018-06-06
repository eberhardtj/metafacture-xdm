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
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.MetafactureException;
import org.metafacture.framework.XmlReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.DefaultXdmPipe;
import org.metafacture.framework.XdmReceiver;
import org.metafacture.xdm.helper.XmlFilterAdapter;

@In(XdmReceiver.class)
@Out(XmlReceiver.class)
@Description("Transforms xdm nodes into sax documents.")
@FluxCommand("xdm-to-sax")
public class XdmToSax implements DefaultXdmPipe<XmlReceiver> {
    private Processor processor;
    private XmlReceiver receiver;
    private SAXDestination destination;

    public XdmToSax() {
        this.processor = new Processor(false);
    }

    @Override
    public void process(XdmNode node) {
        try {
            processor.writeXdmValue(node, destination);
        } catch (SaxonApiException e) {
            throw new MetafactureException(e);
        }
    }

    @Override
    public <R extends XmlReceiver> R setReceiver(R receiver) {
        this.destination = new SAXDestination(new XmlFilterAdapter(receiver));
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
