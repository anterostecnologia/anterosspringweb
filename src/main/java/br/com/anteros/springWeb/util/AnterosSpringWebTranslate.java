package br.com.anteros.springWeb.util;

import br.com.anteros.core.utils.AbstractCoreTranslate;

public class AnterosSpringWebTranslate extends AbstractCoreTranslate {

	public AnterosSpringWebTranslate(String messageBundleName) {
		super(messageBundleName);
	}

	private static AnterosSpringWebTranslate translate;
	
	public AnterosSpringWebTranslate getInstance(){
		if (translate==null){
			translate = new AnterosSpringWebTranslate("anterosspringweb_messages");
		}
		return translate;
	}
}
