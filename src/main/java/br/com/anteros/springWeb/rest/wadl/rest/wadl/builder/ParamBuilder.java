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

import br.com.anteros.springWeb.rest.wadl.descriptor.Param;
import br.com.anteros.springWeb.rest.wadl.descriptor.ParamStyle;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.param.ParamFromAnnotationBuilder;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.param.ParamFromAnnotationBuilderFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ParamBuilder {

    private final Method javaMethod;
    private final ParamFromAnnotationBuilderFactory factory;
    private List<Param> templateParams = new ArrayList<Param>();
    private List<Param> noTemplateParams = new ArrayList<Param>();
    private boolean allParametersAreBuilt = false;

    ParamBuilder(MethodContext ctx) {
        javaMethod = ctx.getJavaMethod();
        factory = new ParamFromAnnotationBuilderFactory(ctx.getParentContext().getGrammarsDiscoverer());
    }

    List<Param> buildTemplateParams() {
        buildAllParams();
        return templateParams;
    }

    List<Param> buildNoTemplateParams() {
        buildAllParams();
        return noTemplateParams;
    }

    private void buildAllParams() {
        if (allParametersAreBuilt) {
            return;
        }

        final Annotation[][] paramAnnotations = javaMethod.getParameterAnnotations();

        for (int paramIndex = 0; paramIndex < paramAnnotations.length; paramIndex++) {
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                final ParamFromAnnotationBuilder paramFromAnnotationBuilder = factory.builderFor(annotation);
                if (paramFromAnnotationBuilder != null) {
                    classify(paramFromAnnotationBuilder.build(javaMethod, paramIndex, annotation));

                    // Each parameter is just of one style, so we can jump to the next parameter
                    break;
                }
            }
        }

        templateParams = Collections.unmodifiableList(templateParams);
        noTemplateParams = Collections.unmodifiableList(noTemplateParams);
        allParametersAreBuilt = true;
    }

    private void classify(Param param) {
        if (param.getStyle() == ParamStyle.TEMPLATE) {
            templateParams.add(param);

        } else {
            noTemplateParams.add(param);
        }
    }
}
