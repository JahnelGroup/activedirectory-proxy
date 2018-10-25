package com.jahnelgroup.util.activedirectoryproxy

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider

@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${ldap.domain}")
    lateinit var domain: String

    @Value("\${ldap.url}")
    lateinit var url: String

    @Value("\${ldap.searchFilter}")
    lateinit var searchFilter: String

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin()
    }

    @Throws(Exception::class)
    override fun configure(authManagerBuilder: AuthenticationManagerBuilder) {
        authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider())
    }

    @Bean
    fun activeDirectoryLdapAuthenticationProvider(): AuthenticationProvider {
        val provider = ActiveDirectoryLdapAuthenticationProvider(domain, url)
        provider.setSearchFilter(searchFilter)
        provider.setConvertSubErrorCodesToExceptions(true)
        provider.setUseAuthenticationRequestCredentials(true)
        return provider
    }

}