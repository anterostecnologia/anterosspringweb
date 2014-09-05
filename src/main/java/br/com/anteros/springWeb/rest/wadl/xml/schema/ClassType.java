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

import javax.xml.namespace.QName;

public abstract class ClassType {

    final Class<?> clazz;
    final QName qName;

    ClassType(Class<?> clazz, QName qName) {
        this.qName = qName;
        this.clazz = clazz;
    }

    public abstract String toSchema();

    public QName getQName() {
        return qName;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
