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
package br.com.anteros.springWeb.rest.wadl.xml.namespace;

public class QNameMemory {

    private final QNamesCache cacheForSingleTypes;
    private final QNamesCache cacheForCollectionTypes;
    private final QNamePrefixesCache cacheForAlreadyUsedPrefixes;

    public QNameMemory(QNamesCache cacheForSingleTypes, QNamesCache cacheForCollectionTypes, QNamePrefixesCache cacheForAlreadyUsedPrefixes) {
        this.cacheForSingleTypes = cacheForSingleTypes;
        this.cacheForCollectionTypes = cacheForCollectionTypes;
        this.cacheForAlreadyUsedPrefixes = cacheForAlreadyUsedPrefixes;
    }

    public QNamesCache forSingleTypes() {
        return cacheForSingleTypes;
    }

    public QNamesCache forCollectionTypes() {
        return cacheForCollectionTypes;
    }

    public QNamePrefixesCache forAlreadyUsedPrefixes() {
        return cacheForAlreadyUsedPrefixes;
    }
}
