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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.namespace.QName;
import java.util.regex.Pattern;

import static br.com.anteros.core.utils.StringUtils.isNotBlank;

public class QNameBuilder {

    private static final Logger logger = LoggerFactory.getLogger(QNameBuilder.class);

    private static final Pattern BY_DOTS = Pattern.compile("\\.");
    private static final Pattern VOWELS = Pattern.compile("[aeiou]");
    private static final QName EMPTY_Q_NAME = new QName("", "", "");

    private final QNameMemory memory;

    public QNameBuilder(QNameMemory memory) {
        this.memory = memory;
    }

    public QName buildFor(Class<?> classType) {
        return discoverQNameAndCachesIt(classType, "", "", memory.forSingleTypes());
    }

    public QName buildForCollectionOf(Class<?> clazz) {
        return discoverQNameAndCachesIt(clazz, "Collection", "cll", memory.forCollectionTypes());
    }

    private QName discoverQNameAndCachesIt(Class<?> classType, String classTypeNameSuffix, String prefixSuffix, QNamesCache cache) {
        QName qName = cache.getQNameFor(classType);
        if (qName == null) {
            qName = discoverQNameFromJaxb(classType);

            final String localPart = (isNotBlank(qName.getLocalPart()) ? qName.getLocalPart() : discoverLocalPartFor(classType)) + classTypeNameSuffix;
            final String namespaceUri = isNotBlank(qName.getNamespaceURI()) ? qName.getNamespaceURI() : discoverNamespaceUriFor(classType, localPart);
            final String prefix = isNotBlank(qName.getPrefix()) ? qName.getPrefix() : discoverPrefixFor(classType, prefixSuffix);
            qName = new QName(namespaceUri, localPart, prefix);

            cache.putQNameFor(classType, qName);
        }
        return qName;
    }

    private QName discoverQNameFromJaxb(Class<?> classType) {
        QName qName = null;
        try {
            final JAXBIntrospector jaxbIntrospector = JAXBContext.newInstance(classType).createJAXBIntrospector();
            qName = jaxbIntrospector.getElementName(classType.getConstructor().newInstance());

        } catch (Exception e) {
            // Add e to the logger message because JAXB Exceptions has a lot of information in the toString().
            // and some loggers implementations just print the getMessage();
            logger.warn("Cannot discover QName from JAXB annotations for class: " + classType.getName()
                    + ". Preparing generic QName." + e, e);
        }

        if (qName == null) {
            // Could be null if getElementName returned null, or a exception was thrown.
            return EMPTY_Q_NAME;
        }
        return qName;
    }

    private String discoverNamespaceUriFor(Class<?> classType, String localPart) {
        final String[] classNameSections = BY_DOTS.split(classType.getName());
        return "http://www." + classNameSections[1] + '.' + classNameSections[0] + "/schema/" + localPart;
    }

    private String discoverLocalPartFor(Class<?> classType) {
        final String classTypeName = classType.getSimpleName();
        return classTypeName.substring(0, 1).toLowerCase() + classTypeName.substring(1);
    }

    private String discoverPrefixFor(Class<?> classType, String suffix) {
        final String simpleName = classType.getSimpleName();
        final String classNameWithoutVowels = VOWELS.matcher(simpleName.toLowerCase()).replaceAll("");
        final String reducedClassName = classNameWithoutVowels.length() >= 3 ? classNameWithoutVowels : simpleName;

        String prefix = reducedClassName.substring(0, 3) + suffix;

        int i = 4;
        int numberedSuffix = 2;
        while (memory.forAlreadyUsedPrefixes().contains(prefix)) {
            if (i < reducedClassName.length()) {
                prefix = reducedClassName.substring(0, i) + suffix;
                i++;

            } else {
                prefix = reducedClassName.substring(0, 3) + suffix + numberedSuffix;
                numberedSuffix++;
            }
        }

        memory.forAlreadyUsedPrefixes().add(prefix);
        return prefix;
    }
}
