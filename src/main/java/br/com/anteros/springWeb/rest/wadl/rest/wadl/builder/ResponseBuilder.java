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

import br.com.anteros.springWeb.rest.wadl.descriptor.Response;

class ResponseBuilder {

    private static final long OK = 200L;

    private final RepresentationBuilder representationBuilder = new RepresentationBuilder();

    Collection<Response> build(MethodContext ctx) {
        final Collection<Response> responses = new ArrayList<Response>();
        /*
         * By now just one 'response' element is returned, but the method returns a collection
         * because the specification supports several elements of this kind.
         */
        responses.add(new Response()
                .withStatus(OK)
                .withRepresentation(representationBuilder.build(ctx)));
        return responses;
    }
}
