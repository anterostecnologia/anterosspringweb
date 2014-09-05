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
package br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace;

import br.com.anteros.springWeb.rest.wadl.xml.namespace.QNameBuilder;
import br.com.anteros.springWeb.rest.wadl.xml.namespace.QNameMemory;
import br.com.anteros.springWeb.rest.wadl.xml.namespace.QNamePrefixesCache;
import br.com.anteros.springWeb.rest.wadl.xml.namespace.QNamesCache;
import br.com.anteros.springWeb.rest.wadl.xml.namespace.cache.InMemoryQNamePrefixesCache;
import br.com.anteros.springWeb.rest.wadl.xml.namespace.cache.InMemoryQNamesCache;
import static br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.QNameConstants.BASIC_COLLECTION_TYPES;
import static br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.namespace.QNameConstants.BASIC_SINGLE_TYPES;

public class QNameBuilderFactory {

    public QNameBuilder getBuilder() {
        return new QNameBuilder(
                new QNameMemory(
                        initCacheForSingleTypes(),
                        initCacheForCollectionTypes(),
                        initCacheForAlreadyUsedPrefixes()));
    }

    private QNamesCache initCacheForSingleTypes() {
        return new InMemoryQNamesCache(BASIC_SINGLE_TYPES);
    }

    private QNamesCache initCacheForCollectionTypes() {
        return new InMemoryQNamesCache(BASIC_COLLECTION_TYPES);
    }

    private QNamePrefixesCache initCacheForAlreadyUsedPrefixes() {
        final QNamePrefixesCache cache = new InMemoryQNamePrefixesCache();
        cache.add("xs");
        cache.add("wadl");
        cache.add("tc");

        return cache;
    }
}
