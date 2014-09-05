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

import java.util.ArrayList;
import java.util.Collection;

import br.com.anteros.springWeb.rest.wadl.descriptor.Resource;

class ResourceBuilder {

    private final ApplicationContext applicationContext;
    private final MethodBuilder methodBuilder = new MethodBuilder();

    ResourceBuilder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    Collection<Resource> build() {
        final Collection<Resource> resources = new ArrayList<Resource>();

        for (MethodContext methodContext : applicationContext) {
            methodContext.setParamBuilder(new ParamBuilder(methodContext));

            resources.add(new Resource()
                    .withPath(methodContext.discoverPath())
                    .withParam(methodContext.getParamBuilder().buildTemplateParams())
                    // Cast to Collection because in other case Collection<Method> is resolved with withMethodOrResource(Object... values),
                    // and we want to resolve with withMethodOrResource(Collection<Object> values)
                    .withMethodOrResource((Collection) methodBuilder.build(methodContext)));
        }
        return resources;
    }
}
