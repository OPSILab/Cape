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
package it.eng.opsi.cape.accountmanager.security;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import it.eng.opsi.cape.accountmanager.RestTemplateResponseErrorHandler;

@Configuration
@EnableResourceServer
@Order(1)
public class ResourceSecurityConfig extends ResourceServerConfigurerAdapter {

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

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
						.antMatchers("/api/v2/idm/user").permitAll().antMatchers("/api/v2/idm/auth/external_logout").permitAll().antMatchers("/api/v2/**", "/api/v2")
						.authenticated())
//				.authorizeRequests().antMatchers("/dashboard/login").authenticated().and().oauth2Login().tokenEndpoint()
//				.accessTokenResponseClient(accessTokenResponseClient());
//        	.anyRequest().permitAll() //giving access to e.g. swagger
//        .and().exceptionHandling()
		;
	}


//	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient()
//			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
//		DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
////	RestTemplate restTemplate = new RestTemplate(
////			Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
//		RestTemplate restTemplate = new RestTemplate(oauth2ClientHttpRequestFactory());
//		restTemplate.setMessageConverters(
//				Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
//
//		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
//		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
//
////		RestTemplate restTemplate = new RestTemplate(
////				Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
////		HttpClient requestFactory = HttpClientBuilder.create().build();
////		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(requestFactory));
////		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
//		client.setRestOperations(restTemplate);
//
//		return client;
//	}



}
