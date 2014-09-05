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

import static br.com.anteros.springWeb.rest.wadl.lang.ClassUtils.isArrayOrCollection;

public abstract class AbstractClassMetadata implements ClassMetadata {

    @Override
    public boolean isCollection() {
        return isArrayOrCollection(getType());
    }

    @Override
    public Class<?> getComponentType() {
        if (!isCollection()) {
            throw new IllegalStateException("Cannot call this method because this metadata does not represent a collection. This metadata is for class: " + getType());
        }

        if (getType().isArray()) {
            return getType().getComponentType();
        }

        return getActualType();
    }

    abstract Class<?> getActualType();
}
