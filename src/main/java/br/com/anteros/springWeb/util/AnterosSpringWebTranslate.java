package br.com.anteros.springWeb.util;

import br.com.anteros.core.utils.AbstractCoreTranslate;

public class AnterosSpringWebTranslate extends AbstractCoreTranslate {

	private AnterosSpringWebTranslate(String messageBundleName) {
		super(messageBundleName);
	}
	

	static {
		setInstance(new AnterosSpringWebTranslate("anterosspringweb_messages"));
	}

}
