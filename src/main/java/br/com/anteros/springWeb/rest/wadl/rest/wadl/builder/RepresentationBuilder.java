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
package br.com.anteros.springWeb.rest.wadl.rest.wadl.builder;

import br.com.anteros.springWeb.rest.wadl.descriptor.Representation;
import br.com.anteros.springWeb.rest.wadl.lang.ClassMetadataFromReturnType;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.GrammarsDiscoverer;

import org.springframework.http.MediaType;

import javax.xml.namespace.QName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static br.com.anteros.springWeb.rest.wadl.lang.ClassUtils.isVoid;

class RepresentationBuilder {

    Collection<Representation> build(MethodContext ctx) {
        final Collection<Representation> representations = new ArrayList<Representation>();
        final Method javaMethod = ctx.getJavaMethod();
        final GrammarsDiscoverer grammarsDiscoverer = ctx.getParentContext().getGrammarsDiscoverer();

        for (MediaType mediaType : ctx.getMediaTypes()) {
            final Class<?> returnType = javaMethod.getReturnType();
            if (isVoid(returnType)) {
                continue;
            }

            final QName qName = grammarsDiscoverer.discoverQNameFor(new ClassMetadataFromReturnType(javaMethod));
            representations.add(new Representation().withMediaType(mediaType.toString()).withElement(qName));
        }
        return representations;
    }
}
