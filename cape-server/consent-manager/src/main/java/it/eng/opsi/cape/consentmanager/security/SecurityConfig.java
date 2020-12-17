package it.eng.opsi.cape.consentmanager.security;
/*******************************************************************************
 * CaPe - a Consent Based Personal Data Suite
 *   Copyright (C) 2020 Engineering Ingegneria Informatica S.p.A.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
