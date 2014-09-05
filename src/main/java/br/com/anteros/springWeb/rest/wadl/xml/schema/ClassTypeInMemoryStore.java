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
package br.com.anteros.springWeb.rest.wadl.xml.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ClassTypeInMemoryStore {

    private final Map<String, ClassType> classTypeByLocalPart = new HashMap<String, ClassType>();
    private final Map<Class<?>, ClassType> classTypeByClass = new HashMap<Class<?>, ClassType>();

    ClassType findBy(String localPart) {
        return classTypeByLocalPart.get(localPart);
    }

    ClassType findBy(Class<?> classType) {
        return classTypeByClass.get(classType);
    }

    Map<String, ClassType> getAllByLocalPart() {
        return Collections.unmodifiableMap(classTypeByLocalPart);
    }

    void add(ClassType classType) {
        classTypeByLocalPart.put(classType.getQName().getLocalPart(), classType);
        classTypeByClass.put(classType.getClazz(), classType);
    }
}
