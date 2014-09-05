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

import br.com.anteros.springWeb.rest.wadl.lang.ClassMetadata;
import br.com.anteros.springWeb.rest.wadl.xml.namespace.QNameBuilder;

import javax.xml.namespace.QName;

import java.util.Map;

public class ClassTypeDiscoverer {

    final QNameBuilder qNameBuilder;
    final ClassTypeInMemoryStore classTypeStore = new ClassTypeInMemoryStore();

    public ClassTypeDiscoverer(QNameBuilder qNameBuilder) {
        this.qNameBuilder = qNameBuilder;
    }

    public ClassType discoverFor(ClassMetadata classMetadata) {
        ClassType classType = classTypeStore.findBy(classMetadata.getType());
        if (classType == null) {
            classType = buildClassTypeFor(classMetadata);
            classTypeStore.add(classType);
        }

        return classType;
    }

    private ClassType buildClassTypeFor(ClassMetadata classMetadata) {
        final ClassType classType;
        if (classMetadata.isCollection()) {
            final QName collectionQName = qNameBuilder.buildForCollectionOf(classMetadata.getComponentType());
            final QName elementsQName = qNameBuilder.buildFor(classMetadata.getComponentType());
            classType = new CollectionType(classMetadata.getType(), collectionQName, elementsQName);

        } else {
            final QName qName = qNameBuilder.buildFor(classMetadata.getType());
            classType = new SingleType(classMetadata.getType(), qName);
        }
        return classType;
    }

    public ClassType getBy(String localPart) {
        final ClassType classType = classTypeStore.findBy(localPart);
        if (classType == null) {
            throw new ClassTypeNotFoundException("Cannot find class type for localPart: " + localPart);
        }
        return classType;
    }

    public Map<String, ClassType> getAllByLocalPart() {
        return classTypeStore.getAllByLocalPart();
    }
}
