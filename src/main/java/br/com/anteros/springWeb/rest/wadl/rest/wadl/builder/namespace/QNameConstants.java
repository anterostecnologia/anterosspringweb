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

import javax.xml.namespace.QName;

import br.com.anteros.springWeb.rest.wadl.descriptor.Application;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class QNameConstants {

    private QNameConstants() {
        // Private empty default constructor because instances of this class are not allowed.
    }

    /*
     * Single types.
     */
    public static final QName BOOLEAN = new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs");
    public static final QName BYTE = new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs");
    public static final QName SHORT = new QName("http://www.w3.org/2001/XMLSchema", "short", "xs");
    public static final QName INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "integer", "xs");
    public static final QName LONG = new QName("http://www.w3.org/2001/XMLSchema", "long", "xs");
    public static final QName FLOAT = new QName("http://www.w3.org/2001/XMLSchema", "float", "xs");
    public static final QName DOUBLE = new QName("http://www.w3.org/2001/XMLSchema", "double", "xs");
    public static final QName STRING = new QName("http://www.w3.org/2001/XMLSchema", "string", "xs");
    public static final QName DATE = new QName("http://www.w3.org/2001/XMLSchema", "date", "xs");
    public static final QName WADL_APPLICATION = new QName("http://wadl.dev.java.net/2009/02", "application", "wadl");

    public static final Map<Class<?>, QName> BASIC_SINGLE_TYPES = Collections.unmodifiableMap(new HashMap<Class<?>, QName>() {{
        put(Boolean.class, BOOLEAN);
        put(boolean.class, BOOLEAN);
        put(byte.class, BYTE);
        put(Byte.class, BYTE);
        put(short.class, SHORT);
        put(Short.class, SHORT);
        put(Integer.class, INTEGER);
        put(int.class, INTEGER);
        put(Long.class, LONG);
        put(long.class, LONG);
        put(Float.class, FLOAT);
        put(float.class, FLOAT);
        put(Double.class, DOUBLE);
        put(double.class, DOUBLE);
        put(String.class, STRING);
        put(Date.class, DATE);
        put(Application.class, WADL_APPLICATION);
    }});

    /*
     * Collections types.
     */
    public static final QName BOOLEANS = new QName("http://www.autentia.com/2013/BasicCollections", "booleanCollection", "tcll");
    public static final QName BYTES = new QName("http://www.autentia.com/2013/BasicCollections", "bytesCollection", "tcll");
    public static final QName SHORTS = new QName("http://www.autentia.com/2013/BasicCollections", "shortCollection", "tcll");
    public static final QName INTEGERS = new QName("http://www.autentia.com/2013/BasicCollections", "integerCollection", "tcll");
    public static final QName LONGS = new QName("http://www.autentia.com/2013/BasicCollections", "longCollection", "tcll");
    public static final QName FLOATS = new QName("http://www.autentia.com/2013/BasicCollections", "floatCollection", "tcll");
    public static final QName DOUBLES = new QName("http://www.autentia.com/2013/BasicCollections", "doubleCollection", "tcll");
    public static final QName STRINGS = new QName("http://www.autentia.com/2013/BasicCollections", "stringCollection", "tcll");
    public static final QName DATES = new QName("http://www.autentia.com/2013/BasicCollections", "dateCollection", "tcll");

    public static final Map<Class<?>, QName> BASIC_COLLECTION_TYPES = Collections.unmodifiableMap(new HashMap<Class<?>, QName>() {{
        put(Boolean.class, BOOLEANS);
        put(boolean.class, BOOLEANS);
        put(byte.class, BYTES);
        put(Byte.class, BYTES);
        put(short.class, SHORTS);
        put(Short.class, SHORTS);
        put(Integer.class, INTEGERS);
        put(int.class, INTEGERS);
        put(Long.class, LONGS);
        put(long.class, LONGS);
        put(Float.class, FLOATS);
        put(float.class, FLOATS);
        put(Double.class, DOUBLES);
        put(double.class, DOUBLES);
        put(String.class, STRINGS);
        put(Date.class, DATES);
    }});
}
