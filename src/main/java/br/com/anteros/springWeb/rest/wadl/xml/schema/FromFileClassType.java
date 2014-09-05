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

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.anteros.core.utils.IOUtils;

public class FromFileClassType extends ClassType {

	private static final Logger logger = LoggerFactory.getLogger(FromFileClassType.class);

	private final File sourceFile;

	public FromFileClassType(Class<?> clazz, QName qName, File sourceFile) {
		super(clazz, qName);
		this.sourceFile = sourceFile;
	}

	@Override
	public String toSchema() {
		try {
			return IOUtils.readFileToString(sourceFile, "UTF-8");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	

}
