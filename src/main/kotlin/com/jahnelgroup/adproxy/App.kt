package com.jahnelgroup.adproxy

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableZuulProxy
@Controller
@EnableWebSecurity
@SpringBootApplication
@ConfigurationProperties(prefix = "app")
class App(
		var proxiedAppName: String = "",
		var instructions: String = "",
		var help: String = ""
) : WebSecurityConfigurerAdapter(), WebMvcConfigurer {

	override fun configure(http: HttpSecurity) {
		http
				.authorizeRequests()
				.antMatchers("/proxy/**").permitAll()
				.anyRequest().fullyAuthenticated()
				.and().formLogin().loginPage("/login").permitAll()
				.failureHandler { request, response, exception ->
					request.session.setAttribute("loginMessage",
							if (exception::class.java.isAssignableFrom(BadCredentialsException::class.java)) {
								"Bad credentials."
							} else{
								exception.message
							}
					)
					response.sendRedirect("/login")
				}
				.and().csrf().disable()
	}

	override fun addViewControllers(registry: ViewControllerRegistry) {
		registry.addViewController("/login").setViewName("login")
	}

	@GetMapping("/login")
	fun getLogin(model: Model): String{
		model.addAttribute("app", this)
		return "login"
	}
}

@Configuration
class LdapAuthentication : GlobalAuthenticationConfigurerAdapter() {

	var logger = LoggerFactory.getLogger(LdapAuthentication::class.java)

	@Value("\${ldap.domain}")
	lateinit var domain: String

	@Value("\${ldap.url}")
	lateinit var url: String

	@Value("\${ldap.searchFilter}")
	lateinit var searchFilter: String

	@Bean
	fun authenticationManager(): AuthenticationManager {
		return ProviderManager(listOf(activeDirectoryLdapAuthenticationProvider()))
	}

	@Bean
	fun activeDirectoryLdapAuthenticationProvider(): AuthenticationProvider {
		logger.info("domain=$domain, url=$url, searchFilter=$searchFilter")
		val provider = ActiveDirectoryLdapAuthenticationProvider(domain, url)
		provider.setSearchFilter(searchFilter)
		provider.setConvertSubErrorCodesToExceptions(true)
		provider.setUseAuthenticationRequestCredentials(true)
		return provider
	}

	@Throws(Exception::class)
	override fun init(auth: AuthenticationManagerBuilder) {
		auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider())
	}

}

fun main(args: Array<String>) {
	runApplication<App>(*args)
}