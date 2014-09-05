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
package br.com.anteros.springWeb.rest.wadl.xml.namespace.cache;

import br.com.anteros.springWeb.rest.wadl.xml.namespace.QNamePrefixesCache;

import java.util.HashSet;
import java.util.Set;

public class InMemoryQNamePrefixesCache implements QNamePrefixesCache {

    private final Set<String> cache = new HashSet<String>();

    @Override
    public boolean contains(String prefix) {
        return cache.contains(prefix);
    }

    @Override
    public void add(String prefix) {
        cache.add(prefix);
    }
}
