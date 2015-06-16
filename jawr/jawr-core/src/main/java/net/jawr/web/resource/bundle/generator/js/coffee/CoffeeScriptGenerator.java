/**
 * Copyright 2012-2013  Ibrahim Chaehoi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.resource.bundle.generator.js.coffee;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.generator.AbstractJavascriptGenerator;
import net.jawr.web.resource.bundle.generator.ConfigurationAwareResourceGenerator;
import net.jawr.web.resource.bundle.generator.GeneratorContext;
import net.jawr.web.resource.bundle.generator.PostInitializationAwareResourceGenerator;
import net.jawr.web.resource.bundle.generator.resolver.ResourceGeneratorResolver;
import net.jawr.web.resource.bundle.generator.resolver.ResourceGeneratorResolverFactory;
import net.jawr.web.util.js.RhinoEngine;

import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

/**
 * This class defines the coffee script generator
 * 
 * @author ibrahim Chaehoi
 */
public class CoffeeScriptGenerator extends AbstractJavascriptGenerator
		implements ConfigurationAwareResourceGenerator,
		PostInitializationAwareResourceGenerator, ICoffeeScriptGenerator {

	/** The coffee script suffix */
	private static final String COFFEE_SCRIPT_SUFFIX = "coffee";

	/** The coffee script options property name */
	private static final String JAWR_JS_GENERATOR_COFFEE_SCRIPT_OPTIONS = "jawr.js.generator.coffee.script.options";

	/** The coffee script generator location */
	private static final String JAWR_JS_GENERATOR_COFFEE_SCRIPT_LOCATION = "jawr.js.generator.coffee.script.location";

	/** The default coffee script JS location */
	private static final String DEFAULT_COFFEE_SCRIPT_JS_LOCATION = "/net/jawr/web/resource/bundle/generator/js/coffee/coffee-script.js";

	/** The resolver */
	private ResourceGeneratorResolver resolver;

	/** The jawr config */
	private JawrConfig config;

	/** The coffee script options */
	private String options;

	/** The Rhino engine */
	private RhinoEngine rhino;

	/**
	 * Constructor
	 */
	public CoffeeScriptGenerator() {
		resolver = ResourceGeneratorResolverFactory
				.createSuffixResolver(COFFEE_SCRIPT_SUFFIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.generator.ConfigurationAwareResourceGenerator
	 * #setConfig(net.jawr.web.config.JawrConfig)
	 */
	public void setConfig(JawrConfig config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.resource.bundle.generator.
	 * PostInitializationAwareResourceGenerator#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {

		options = config.getProperty(JAWR_JS_GENERATOR_COFFEE_SCRIPT_OPTIONS,
				"");

		// Load JavaScript Script Engine
		String script = config.getProperty(
				JAWR_JS_GENERATOR_COFFEE_SCRIPT_LOCATION,
				DEFAULT_COFFEE_SCRIPT_JS_LOCATION);
		rhino = new RhinoEngine();
		InputStream inputStream = getResourceInputStream(script);
		rhino.evaluate("coffee-script.js", inputStream);
	}

	/**
	 * Returns the resource input stream
	 * 
	 * @param path
	 *            the resource path
	 * @return the resource input stream
	 */
	private InputStream getResourceInputStream(String path) {
		InputStream is = config.getContext().getResourceAsStream(path);
		if (is == null) {
			try {
				is = ClassLoaderResourceUtils.getResourceAsStream(path, this);
			} catch (FileNotFoundException e) {
				throw new BundlingProcessException(e);
			}
		}

		return is;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.generator.BaseResourceGenerator#getResolver
	 * ()
	 */
	public ResourceGeneratorResolver getResolver() {
		return resolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.generator.TextResourceGenerator#createResource
	 * (net.jawr.web.resource.bundle.generator.GeneratorContext)
	 */
	public Reader createResource(GeneratorContext context) {

		String path = context.getPath();
		Reader rd = null;
		try {
			List<Class<?>> excluded = new ArrayList<Class<?>>();
			excluded.add(ICoffeeScriptGenerator.class);
			rd = context.getResourceReaderHandler().getResource(path, false,
					excluded);
			StringWriter swr = new StringWriter();
			IOUtils.copy(rd, swr);

			String result = compile(swr.toString());
			rd = new StringReader(result);

		} catch (ResourceNotFoundException e) {
			throw new BundlingProcessException(e);
		} catch (IOException e) {
			throw new BundlingProcessException(e);
		}

		return rd;
	}

	/**
	 * Compile the CoffeeScript source to a JS source
	 * 
	 * @param coffeeScriptSource
	 *            the CoffeeScript source
	 * @return the JS source
	 */
	public String compile(String coffeeScriptSource) {

		Scriptable compileScope = rhino.newObject();
		compileScope
				.put("coffeeScriptSource", compileScope, coffeeScriptSource);
		try {
			return (String) rhino.evaluateString(compileScope,
					String.format(
							"CoffeeScript.compile(coffeeScriptSource, '%s');",
							options), "JCoffeeScriptCompiler");
		} catch (JavaScriptException e) {
			throw new BundlingProcessException(e);
		}
	}
}
