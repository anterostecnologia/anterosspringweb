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

import br.com.anteros.springWeb.rest.wadl.HttpMethod;
import br.com.anteros.springWeb.rest.wadl.descriptor.Method;

import java.util.ArrayList;
import java.util.Collection;

class MethodBuilder {

    final RequestBuilder requestBuilder = new RequestBuilder();
    final ResponseBuilder responseBuilder = new ResponseBuilder();

    Collection<Method> build(MethodContext ctx) {
        final Collection<Method> methods = new ArrayList<Method>();

        for (HttpMethod httpMethod : ctx.getHttpMethods()) {
            methods.add(new Method()
                    .withName(httpMethod.name())
                    .withId(ctx.getJavaMethod().getName())
                    .withRequest(requestBuilder.build(ctx))
                    .withResponse(responseBuilder.build(ctx)));
        }
        return methods;
    }
}
