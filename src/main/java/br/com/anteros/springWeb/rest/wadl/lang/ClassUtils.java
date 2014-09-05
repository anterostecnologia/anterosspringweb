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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public final class ClassUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    private ClassUtils() {
        // Private default constructor because we don't want instances of this class.
    }

    public static boolean isArrayOrCollection(Class<?> clazz) {
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    public static Class<?> getElementsClassOf(Class<?> clazz) {
        if (clazz.isArray()) {
            return clazz.getComponentType();
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            // TODO check for annotation
            logger.warn("In Java its not possible to discover de Generic type of a collection like: {}", clazz);
            return Object.class;
        }
        return clazz;
    }

    public static boolean isVoid(Class<?> clazz) {
        return Void.class.equals(clazz) || void.class.equals(clazz);
    }
}
