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

import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.GrammarsDiscoverer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

abstract class ParamFromAnnotationBuilderCommons implements ParamFromAnnotationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ParamFromAnnotationBuilderFactory.class);
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new LocalVariableTableParameterNameDiscoverer();

    final GrammarsDiscoverer grammarsDiscoverer;

    protected ParamFromAnnotationBuilderCommons(GrammarsDiscoverer grammarsDiscoverer) {
        this.grammarsDiscoverer = grammarsDiscoverer;
    }

    String discoverParamName(Method javaMethod, int paramIndex, String explicitName) {
        String name = explicitName;
        if (name.isEmpty()) {
            final String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(javaMethod);
            if (parameterNames != null) {
                name = parameterNames[paramIndex];
            } else {
                logger.warn("Cannot determine name of parameter {} for method {}. Maybe you haven't compiled with debug info, or don't have defined the value in corresponding annotation", paramIndex, javaMethod.getName());
            }
        }
        return name;
    }

}
