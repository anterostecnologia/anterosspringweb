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

import javax.xml.namespace.QName;

public class CollectionType extends ClassType {

    private static final String COLLECTION_COMPLEX_TYPE_SCHEMA = "<?xml version=\"1.0\" standalone=\"yes\"?>\n" +
            "<xs:schema version=\"1.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            '\n' +
            "  <xs:element name=\"???collectionName???\" type=\"???collectionType???\"/>\n" +
            '\n' +
            "  <xs:complexType name=\"???collectionType???\">\n" +
            "    <xs:sequence>\n" +
            "      <xs:element name=\"???name???\" type=\"???type???\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
            "    </xs:sequence>\n" +
            "  </xs:complexType>\n" +
            "</xs:schema>\n\n";

    private final QName elementsQName;

    public CollectionType(Class<?> collectionClass, QName collectionQName, QName elementsQName) {
        super(collectionClass, collectionQName);
        this.elementsQName = elementsQName;
    }

    @Override
    public String toSchema() {
        return COLLECTION_COMPLEX_TYPE_SCHEMA
                .replace("???type???", elementsQName.getLocalPart())
                .replace("???name???", elementsQName.getLocalPart())
                .replace("???collectionType???", qName.getLocalPart())
                .replace("???collectionName???", qName.getLocalPart());
    }

    public QName getElementsQName() {
        return elementsQName;
    }
}
