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

import static org.junit.Assert.*;

public class XdmNodeXmlEncoderTest {

    private Processor processor;

    @Before
    public void setUp() {
        processor = new Processor(false);
    }

    @Test
    public void shouldNotAddDeclarationToXml() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");

        StringWriter resultCollector = new StringWriter();

        XdmXmlEncoder encoder = new XdmXmlEncoder();
        encoder.setOmitDeclaration(true);
        encoder.setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        encoder.process(book);
        encoder.closeStream();

        String result = resultCollector.toString();
        String startOfXmlDeclaration = "<?xml";
        assertFalse(result.contains(startOfXmlDeclaration));
    }

    @Test
    public void shouldSetEncodingToUtf16InDeclaration() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");

        StringWriter resultCollector = new StringWriter();

        XdmXmlEncoder encoder = new XdmXmlEncoder();
        encoder.setEncoding("UTF-16");
        encoder.setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        encoder.process(book);
        encoder.closeStream();

        String expectedDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>";
        String actual = resultCollector.toString();
        assertTrue(actual.contains(expectedDeclaration));
    }

    @Test
    public void shouldSerializeXdmNodeAsXml() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");

        StringWriter resultCollector = new StringWriter();

        XdmXmlEncoder encoder = new XdmXmlEncoder();
        encoder.setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        encoder.process(book);
        encoder.closeStream();

        String result = resultCollector.toString();
        String expected = readFile("src/test/resources/data/book.xml") + "\n";
        assertEquals(expected, result);
    }

    @Test
    public void shouldSerializeTwoDifferentXdmNodesAsXml() throws Exception {
        XdmNode book = createXdmNode("src/test/resources/data/book.xml");
        XdmNode animal = createXdmNode("src/test/resources/data/animal.xml");

        StringWriter resultCollector = new StringWriter();

        XdmXmlEncoder encoder = new XdmXmlEncoder();
        encoder.setReceiver(new ObjectJavaIoWriter<>(resultCollector));
        encoder.process(book);
        encoder.process(animal);
        encoder.closeStream();

        String result = resultCollector.toString();
        String expected = readFile("src/test/resources/data/book.xml") + "\n" +
                readFile("src/test/resources/data/animal.xml") + "\n";
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
