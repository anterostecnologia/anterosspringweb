package br.com.anteros.springWeb.rest.wadl;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import br.com.anteros.springWeb.rest.wadl.descriptor.Application;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.ApplicationBuilder;
import br.com.anteros.springWeb.rest.wadl.rest.wadl.builder.impl.springframework.SpringWadlBuilderFactory;
import br.com.anteros.springWeb.rest.wadl.xml.schema.SchemaBuilder;

@Controller
@RequestMapping(value = "/rest", produces = {"application/xml"})
public class WadlController {

    private final ApplicationBuilder applicationBuilder;
    private final SchemaBuilder schemaBuilder;

    @Autowired
    public WadlController(RequestMappingHandlerMapping handlerMapping) {
        final SpringWadlBuilderFactory wadlBuilderFactory = new SpringWadlBuilderFactory(handlerMapping);
        applicationBuilder = wadlBuilderFactory.getApplicationBuilder();
        schemaBuilder = wadlBuilderFactory.getSchemaBuilder();
    }

    @ResponseBody
    @RequestMapping(value = "wadl", method = RequestMethod.GET)
    public Application generateWadl(HttpServletRequest request) {
        return applicationBuilder.build(request);
    }

    @ResponseBody
    @RequestMapping(value = "schema/{classTypeName}", method = RequestMethod.GET)
    public String generateSchema(@PathVariable String classTypeName) {
        return schemaBuilder.buildFor(classTypeName);
    }

}