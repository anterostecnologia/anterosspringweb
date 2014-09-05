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

import br.com.anteros.springWeb.rest.wadl.HttpMethod;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.ApplicationContext;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.MethodContext;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

class SpringMethodContext extends MethodContext {

    private final RequestMappingInfo mappingInfo;
    private final HandlerMethod handlerMethod;

    SpringMethodContext(ApplicationContext parentContext, RequestMappingInfo mappingInfo, HandlerMethod handlerMethod) {
        super(parentContext);
        this.mappingInfo = mappingInfo;
        this.handlerMethod = handlerMethod;
    }

    @Override
    protected String discoverPath() {
        String path = null;
        for (String uri : mappingInfo.getPatternsCondition().getPatterns()) {
            path = uri;
        }
        return path;
    }

    @Override
    protected Method getJavaMethod() {
        return handlerMethod.getMethod();
    }

    @Override
    protected Set<HttpMethod> getHttpMethods() {
        final Set<HttpMethod> httpMethods = EnumSet.noneOf(HttpMethod.class);

        for (RequestMethod requestMethod : mappingInfo.getMethodsCondition().getMethods()) {
            httpMethods.add(HttpMethod.valueOf(requestMethod.name()));
        }
        return httpMethods;
    }

    @Override
    protected Set<MediaType> getMediaTypes() {
        return mappingInfo.getProducesCondition().getProducibleMediaTypes();
    }

}
