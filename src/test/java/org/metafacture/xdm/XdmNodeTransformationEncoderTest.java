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
import net.sf.saxon.s9api.XdmNode;
import org.junit.Before;
import org.junit.Test;
import org.metafacture.io.ObjectJavaIoWriter;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class XdmNodeTransformationEncoderTest {
    private Processor processor;

    @Before
    public void setUp() {
        processor = new Processor(false);
    }

    @Test
    public void shouldTransformEachBookToLiteralText() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");

        StringWriter resultCollector = new StringWriter();

        XdmTransformationEncoder transformer = new XdmTransformationEncoder("src/test/resources/xsl/text.xsl");
        transformer.setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        transformer.process(book);
        transformer.process(book);
        transformer.closeStream();

        String result = resultCollector.toString();
        String expected = "text" + "\n" + "text" + "\n";
        assertEquals(expected, result);
    }

    private XdmNode createXdmNode(String path) throws SaxonApiException {
        return processor.newDocumentBuilder().build(new StreamSource(new File(path)));
    }
}
