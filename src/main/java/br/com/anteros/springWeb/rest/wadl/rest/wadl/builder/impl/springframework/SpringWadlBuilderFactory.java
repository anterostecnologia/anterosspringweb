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
package br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.impl.springframework;

import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.ApplicationBuilder;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.ApplicationContext;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.GrammarsDiscoverer;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.QNameBuilderFactory;
import br.com.anteros.springWeb.rest.wadl.xml.schema.ClassTypeDiscoverer;
import br.com.anteros.springWeb.rest.wadl.xml.schema.SchemaBuilder;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class SpringWadlBuilderFactory {

    private final ApplicationBuilder applicationBuilder;
    private final SchemaBuilder schemaBuilder;

    public SpringWadlBuilderFactory(RequestMappingHandlerMapping handlerMapping) {
        final ClassTypeDiscoverer classTypeDiscoverer = new ClassTypeDiscoverer(new QNameBuilderFactory().getBuilder());
        final GrammarsDiscoverer grammarsDiscoverer = new GrammarsDiscoverer(classTypeDiscoverer);
        final ApplicationContext appCtx = new ApplicationContext(new SpringMethodContextIterator(handlerMapping), grammarsDiscoverer);
        applicationBuilder = new ApplicationBuilder(appCtx);
        schemaBuilder = new SchemaBuilder(classTypeDiscoverer);
    }

    public ApplicationBuilder getApplicationBuilder() {
        return applicationBuilder;
    }

    public SchemaBuilder getSchemaBuilder() {
        return schemaBuilder;
    }

}
