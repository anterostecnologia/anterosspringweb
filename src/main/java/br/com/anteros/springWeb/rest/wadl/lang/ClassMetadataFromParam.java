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
package br.com.anteros.springWeb.rest.wadl.lang;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class ClassMetadataFromParam extends AbstractClassMetadata {
    private final Method method;
    private final int paramIndex;

    public ClassMetadataFromParam(Method method, int paramIndex) {
        this.method = method;
        this.paramIndex = paramIndex;
    }

    @Override
    public Class<?> getType() {
        return method.getParameterTypes()[paramIndex];
    }

    @Override
    Class<?> getActualType() {
        final ParameterizedType parameterizedParamType = (ParameterizedType) method.getGenericParameterTypes()[paramIndex];
        return (Class<?>) parameterizedParamType.getActualTypeArguments()[0];
    }
}
