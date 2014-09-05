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

/**
 * Useful URLs:
 *  - W3C XML Schema Definition Language (XSD) 1.1 Part 1: Structures --> http://www.w3.org/TR/xmlschema11-1/
 *  - XML Schema Patterns for Common Data Structures --> http://www.w3.org/2005/07/xml-schema-patterns.html
 */
public class SchemaBuilder {

    private final ClassTypeDiscoverer classTypeDiscoverer;

    public SchemaBuilder(ClassTypeDiscoverer classTypeDiscoverer) {
        this.classTypeDiscoverer = classTypeDiscoverer;
    }

    public String buildFor(final String localPart) {
        return classTypeDiscoverer.getBy(localPart).toSchema();
    }

}
