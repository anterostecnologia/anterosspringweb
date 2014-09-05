/**
 *    Copyright 2013 Autentia Real Business Solution S.L.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package br.com.anteros.springWeb.rest.wadl.xml.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

public class SingleType extends ClassType {

    private static final Logger logger = LoggerFactory.getLogger(SingleType.class);

    private static final String NON_EXISTING_CLASS_SCHEMA = "<?xml version=\"1.0\" standalone=\"yes\"?>\n" +
            "<xs:schema version=\"1.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            '\n' +
            "  <xs:element name=\"???name???\" type=\"???type???\"/>\n" +
            '\n' +
            "  <xs:complexType name=\"???type???\">\n" +
            "    <xs:sequence/>\n" +
            "  </xs:complexType>\n" +
            "</xs:schema>\n\n";

    public SingleType(Class<?> classType, QName qName) {
        super(classType, qName);
    }

    @Override
    public String toSchema() {
        try {
            return tryBuildSchemaFromJaxbAnnotatedClass();

        } catch (Exception e) {
            // Add e to the logger message because JAXB Exceptions has a lot of information in the toString().
            // and some loggers implementations just print the getMessage();
            logger.warn("Cannot generate schema from JAXB annotations for class: " + clazz.getName()
                    + ". Preparing generic Schema.\n" + e, e);
            return schemaForNonAnnotatedClass();
        }
    }

    private String tryBuildSchemaFromJaxbAnnotatedClass() throws JAXBException, IOException {
        final StringWriter stringWriter = new StringWriter();
        JAXBContext.newInstance(clazz).generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                final StreamResult result = new StreamResult(stringWriter);
                result.setSystemId("schema" + clazz.getSimpleName());
                return result;
            }
        });

        return stringWriter.toString();
    }

    private String schemaForNonAnnotatedClass() {
        return NON_EXISTING_CLASS_SCHEMA.replace("???type???", qName.getLocalPart()).replace("???name???", qName.getLocalPart());
    }

}
