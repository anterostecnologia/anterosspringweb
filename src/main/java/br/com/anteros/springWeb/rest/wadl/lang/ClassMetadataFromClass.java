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

import static br.com.anteros.springWeb.rest.wadl.lang.ClassUtils.getElementsClassOf;

public class ClassMetadataFromClass extends AbstractClassMetadata {

    private final Class<?> clazz;

    public ClassMetadataFromClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }

    @Override
    Class<?> getActualType() {
        return getElementsClassOf(clazz);
    }
}
