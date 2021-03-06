/**
 * Copyright 2013 Ibrahim Chaehoi
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
package net.jawr.application.controller;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jawr.web.JawrConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Jawr Binary controller
 * 
 * @author ibrahim Chaehoi
 *
 */
@Component
@Controller(value="JawrBinaryController")
public class JawrBinaryController extends AbstractJawrController {

	@Autowired
	private ServletContext servletContext; 
	
	/**
	 * Constructor
	 */
	public JawrBinaryController() {
		super(JawrConstant.BINARY_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.servlet.JawrSpringController#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = { "/**/**.gif", "/**/**.ico", "/**/**.png", "/**/**.jpg", "/**/**.jpeg", "/**/**.woff", "/**/**.ttf", "/**/**.eot" }, method = RequestMethod.GET)
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return super.handleRequest(request, response);
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.servlet.JawrSpringController#afterPropertiesSet()
	 */
	@Override
	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		setServletContext(servletContext);
		super.afterPropertiesSet();
	}
}
