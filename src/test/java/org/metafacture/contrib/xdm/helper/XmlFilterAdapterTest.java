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

import org.junit.Before;
import org.junit.Test;
import org.metafacture.contrib.xdm.mockito.SingleAttributeMatcher;
import org.metafacture.framework.XmlReceiver;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.xml.sax.helpers.AttributesImpl;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;

public class XmlFilterAdapterTest {
    private XmlFilterAdapter adapter;

    @Mock
    private XmlReceiver receiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adapter = new XmlFilterAdapter(receiver);
    }

    @Test
    public void emptyDocument() throws Exception {
        adapter.startDocument();
        adapter.endDocument();

        final InOrder ordered = inOrder(receiver);
        ordered.verify(receiver).startDocument();
        ordered.verify(receiver).endDocument();
    }

    @Test
    public void singleElement() throws Exception {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "id", "id", "ID", "1");
        adapter.startElement("", "elem", "elem", atts);
        adapter.characters("dummy".toCharArray(), 0, 5);
        adapter.endElement("", "elem", "elem");

        final InOrder ordered = inOrder(receiver);
        ordered.verify(receiver).startElement(eq(""), eq("elem"), eq("elem"),
                argThat(SingleAttributeMatcher.hasSingleAttribute("", "id", "id", "ID", "1")));
        ordered.verify(receiver).characters("dummy".toCharArray(), 0, 5);
        ordered.verify(receiver).endElement("", "elem", "elem");
    }

    @Test
    public void singleDocumentWithElement() throws Exception {
        adapter.startDocument();
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "id", "id", "ID", "1");
        adapter.startElement("", "elem", "elem", attrs);
        adapter.characters("dummy".toCharArray(), 0, 5);
        adapter.endElement("", "elem", "elem");
        adapter.endDocument();

        final InOrder ordered = inOrder(receiver);
        ordered.verify(receiver).startDocument();
        ordered.verify(receiver).startElement(eq(""), eq("elem"), eq("elem"),
                argThat(SingleAttributeMatcher.hasSingleAttribute("", "id", "id", "ID", "1")));
        ordered.verify(receiver).characters("dummy".toCharArray(), 0, 5);
        ordered.verify(receiver).endElement("", "elem", "elem");
        ordered.verify(receiver).endDocument();
    }

}
