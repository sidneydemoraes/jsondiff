package br.com.smc.jsondiff

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.boot.web.servlet.ErrorPage
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus

/**
 * Spring Boot initialization class.
 */
@SpringBootApplication
class Application {

	/**
	 *  Initialization method
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args)
	}

	/**
	 * Configuration that customizes 404 and 405 errors by redirecting them to the main home page
	 * where information about usage of the service can be found.
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/");
				ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/");
				ErrorPage error405Page = new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/");
				container.addErrorPages(error400Page);
				container.addErrorPages(error404Page);
				container.addErrorPages(error405Page);
			}
		};
	}
}

