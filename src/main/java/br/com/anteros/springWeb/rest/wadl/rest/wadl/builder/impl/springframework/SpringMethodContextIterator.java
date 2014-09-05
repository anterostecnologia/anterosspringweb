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

import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.ApplicationContext;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.MethodContext;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.MethodContextIterator;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Iterator;
import java.util.Map;

class SpringMethodContextIterator implements MethodContextIterator {

    private final RequestMappingHandlerMapping handlerMapping;
    private Iterator<Map.Entry<RequestMappingInfo, HandlerMethod>> iterator;
    private ApplicationContext parentContext;

    SpringMethodContextIterator(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void initWith(ApplicationContext parentContext) {
        iterator = handlerMapping.getHandlerMethods().entrySet().iterator();
        this.parentContext = parentContext;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public MethodContext next() {
        final Map.Entry<RequestMappingInfo, HandlerMethod> next = iterator.next();
        return new SpringMethodContext(parentContext, next.getKey(), next.getValue());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
