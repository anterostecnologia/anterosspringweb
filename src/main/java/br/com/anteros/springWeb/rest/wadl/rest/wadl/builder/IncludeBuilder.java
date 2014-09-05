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

import br.com.anteros.springWeb.rest.wadl.descriptor.Include;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.GrammarsDiscoverer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class IncludeBuilder {

    private final GrammarsDiscoverer grammarsDiscoverer;

    IncludeBuilder(GrammarsDiscoverer grammarsDiscoverer) {
        this.grammarsDiscoverer = grammarsDiscoverer;
    }

    Collection<Include> build() {
        final List<Include> includes = new ArrayList<Include>();
        for (String schemaUrl : grammarsDiscoverer.getSchemaUrlsForComplexTypes()) {
            includes.add(new Include().withHref(schemaUrl));
        }
        return includes;
    }
}
