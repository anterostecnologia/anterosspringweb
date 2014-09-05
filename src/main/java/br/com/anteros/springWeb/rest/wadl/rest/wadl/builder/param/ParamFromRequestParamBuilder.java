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
package br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.param;

import br.com.anteros.springWeb.rest.wadl.descriptor.Param;
import br.com.anteros.springWeb.rest.wadl.descriptor.ParamStyle;
import br.com.anteros.springWeb.rest.wadl.lang.ClassMetadataFromParam;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.GrammarsDiscoverer;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class ParamFromRequestParamBuilder extends ParamFromAnnotationBuilderCommons {

    ParamFromRequestParamBuilder(GrammarsDiscoverer grammarsDiscoverer) {
        super(grammarsDiscoverer);
    }

    @Override
    public Param build(Method javaMethod, int paramIndex, Annotation paramAnnotation) {
        final RequestParam requestParam = (RequestParam) paramAnnotation;
        final Param param = new Param()
                .withName(discoverParamName(javaMethod, paramIndex, requestParam.value()))
                .withStyle(ParamStyle.QUERY)
                .withRequired(requestParam.required())
                .withType(grammarsDiscoverer.discoverQNameFor(new ClassMetadataFromParam(javaMethod, paramIndex)));

        if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
            param.setDefault(requestParam.defaultValue());
        }
        return param;
    }
}
