package br.com.smc.jsondiff.configuration

import org.h2.server.web.WebServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration responsible for enabling H2 Web Console in case someone wants to check the
 * in memory database.
 */
@Configuration
class WebConfiguration {

	@Bean
	ServletRegistrationBean h2ServletRegistration() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet())
		registrationBean.addUrlMappings("/console/*")
		return registrationBean
	}
}
