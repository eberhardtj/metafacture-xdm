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
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class XdmNodeTransformerTest {
    private Processor processor;

    @Before
    public void setUp() {
        processor = new Processor(false);
    }

    @Test
    public void shouldApplyIdentityTransformationWithoutAlteringTheNode() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");

        StringWriter resultCollector = new StringWriter();

        XdmTransformer transformer = new XdmTransformer("src/test/resources/xsl/identity.xsl");

        transformer.setReceiver(new XdmXmlEncoder()).setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        transformer.process(book);
        transformer.closeStream();

        String result = resultCollector.toString();
        String expected = readFile("src/test/resources/data/book.xml") + "\n";
        assertEquals(expected, result);
    }

    @Test
    public void shouldApplyTwoIdentityTransformationsWithoutAlteringTheNode() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");

        StringWriter resultCollector = new StringWriter();

        XdmTransformer transformer = new XdmTransformer("src/test/resources/xsl/identity.xsl");
        transformer.setReceiver(new XdmTransformer("src/test/resources/xsl/identity.xsl"))
                .setReceiver(new XdmXmlEncoder())
                .setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        transformer.process(book);
        transformer.closeStream();

        String result = resultCollector.toString();
        String expected = readFile("src/test/resources/data/book.xml") + "\n";
        assertEquals(expected, result);
    }

    private XdmNode createXdmNode(String path) throws SaxonApiException {
        return processor.newDocumentBuilder().build(new StreamSource(new File(path)));
    }

    private String readFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
