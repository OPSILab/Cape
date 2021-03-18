/*******************************************************************************
 * CaPe - A Consent Based Personal Data Suite
 *  Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.eng.opsi.cape.sdk.security;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import it.eng.opsi.cape.sdk.RestTemplateResponseErrorHandler;

@Configuration
@EnableResourceServer
@Order(1)
public class ResourceSecurityConfig extends ResourceServerConfigurerAdapter {

//	@Bean
//	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
//	    return new SecurityEvaluationContextExtension();
//	}

//	@Bean
//    public AuthoritiesExtractor customAuthoritiesExtractor() {
//        return new CustomAuthoritiesExtractor();
//    }
//	
//	@Bean
//    public PrincipalExtractor customPrincipalExtractor() {
//        return new CustomPrincipalExtractor();
//    }

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().formLogin().disable().headers().httpStrictTransportSecurity().disable().and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
				.authorizeRequests(authz -> authz.antMatchers("/api/v2/idm/oauth2/token").permitAll()
						.antMatchers("/api/v2/idm/user").permitAll().antMatchers("/api/v2/idm/auth/external_logout")
						.permitAll().antMatchers("/api/v2/**", "/api/v2").authenticated())
//        	.anyRequest().permitAll()
		;
	}

	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
//	RestTemplate restTemplate = new RestTemplate(
//			Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
		RestTemplate restTemplate = new RestTemplateBuilder()
				.messageConverters(Arrays.asList(new FormHttpMessageConverter(),
						new OAuth2AccessTokenResponseHttpMessageConverter()))
				.errorHandler(new RestTemplateResponseErrorHandler()).build();
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

//		RestTemplate restTemplate = new RestTemplate(
//				Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
//		HttpClient requestFactory = HttpClientBuilder.create().build();
//		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(requestFactory));
//		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		client.setRestOperations(restTemplate);

		return client;
	}

}
