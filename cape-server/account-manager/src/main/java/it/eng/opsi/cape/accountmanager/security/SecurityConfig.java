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
//package it.eng.opsi.cape.accountmanager.security;
//
//import java.util.Arrays;
//
//import org.apache.http.client.HttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.data.mongodb.core.aggregation.BooleanOperators.And;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.http.converter.FormHttpMessageConverter;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
//import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
//import org.springframework.web.client.RestTemplate;
//
//import it.eng.opsi.cape.accountmanager.service.RestTemplateResponseErrorHandler;
//
//@Configuration
//@EnableWebSecurity
//@Order(2)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
////	@Override
////	protected void configure(HttpSecurity http) throws Exception {
//////		http.headers().httpStrictTransportSecurity().disable().and().formLogin().disable().authorizeRequests()
//////				.antMatchers("/api/v2/**").authenticated().anyRequest().permitAll().and().sessionManagement()
//////				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//////         .oauth2Client().authorizationCodeGrant();
//////        .oauth2Login().tokenEndpoint().accessTokenResponseClient(accessTokenResposeClient());
//////				.oauth2Login().redirectionEndpoint().baseUri("/account-manager/login/oauth2/code/idm").and()
//////				.tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient());
////
////		http.csrf().disable().formLogin().disable().cors().and().headers().httpStrictTransportSecurity().disable().and()
////				// .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
////				.authorizeRequests().antMatchers("/login").authenticated().and().oauth2Login().tokenEndpoint()
////				.accessTokenResponseClient(accessTokenResponseClient());
////
////	}
//
////	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
////		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
//////		RestTemplate restTemplate = new RestTemplate(
//////				Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
//////		RestTemplate restTemplate = new RestTemplateBuilder()
//////				.messageConverters(Arrays.asList(new FormHttpMessageConverter(),
//////						new OAuth2AccessTokenResponseHttpMessageConverter()))
//////				.errorHandler(new RestTemplateResponseErrorHandler()).build();
//////		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
////
////		RestTemplate restTemplate = new RestTemplate(
////				Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
////		HttpClient requestFactory = HttpClientBuilder.create().build();
////		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(requestFactory));
////		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
////		client.setRestOperations(restTemplate);
////
////		return client;
////	}
//
//	
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/api/v2/idm/user");
//	}
//
//}
