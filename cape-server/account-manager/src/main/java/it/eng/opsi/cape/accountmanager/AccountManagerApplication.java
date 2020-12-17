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
package it.eng.opsi.cape.accountmanager;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import it.eng.opsi.cape.accountmanager.repository.Base64URLReadConverter;
import it.eng.opsi.cape.accountmanager.repository.Base64URLWriteConverter;
import it.eng.opsi.cape.accountmanager.repository.JWSHeaderReadConverter;
import it.eng.opsi.cape.accountmanager.repository.JWSHeaderWriteConverter;
import it.eng.opsi.cape.accountmanager.repository.RSAKeyReadConverter;
import it.eng.opsi.cape.accountmanager.repository.RSAKeyWriteConverter;
import it.eng.opsi.cape.accountmanager.repository.ZonedDateTimeReadConverter;
import it.eng.opsi.cape.accountmanager.repository.ZonedDateTimeWriteConverter;
import it.eng.opsi.cape.accountmanager.utils.RSAKeyDeserializer;
import it.eng.opsi.cape.accountmanager.utils.RSAKeySerializer;
import it.eng.opsi.cape.accountmanager.utils.Base64URLSerializer;
import it.eng.opsi.cape.accountmanager.utils.JWSHeaderDeserializer;
import it.eng.opsi.cape.accountmanager.utils.JWSHeaderSerializer;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "it.eng.opsi.cape.accountmanager.repository")
@EnableAsync
@Slf4j
public class AccountManagerApplication extends SpringBootServletInitializer {

	private static ConfigurableApplicationContext applicationContext;

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AccountManagerApplication.class);
	}

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(AccountManagerApplication.class, args);
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}

	@Bean
	public MongoCustomConversions customConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(new ZonedDateTimeReadConverter());
		converters.add(new ZonedDateTimeWriteConverter());
		converters.add(new RSAKeyWriteConverter());
		converters.add(new RSAKeyReadConverter());
		converters.add(new JWSHeaderReadConverter());
		converters.add(new JWSHeaderWriteConverter());
		converters.add(new Base64URLReadConverter());
		converters.add(new Base64URLWriteConverter());
		return new MongoCustomConversions(converters);
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RestTemplate createRestTemplate()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler(applicationContext));

		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<>();
		}
		interceptors.add(new RestTemplateHeaderModifierInterceptor());
		restTemplate.setInterceptors(interceptors);

		// find and replace Jackson message converter with our own
		for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
			final HttpMessageConverter<?> httpMessageConverter = restTemplate.getMessageConverters().get(i);
			if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
				restTemplate.getMessageConverters().set(i, createMappingJacksonHttpMessageConverter());
			}
		}
		return restTemplate;
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		CloseableHttpClient httpClient;

		/*
		 * Enable SSL unauthorized connections (only if DEBUG)
		 */
		if (activeProfile.equals("dev")) {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy).build();

			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

			httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		} else
			httpClient = HttpClients.custom().build();

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);
		return clientHttpRequestFactory;
	}

	private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(getObjectMapper());
		return converter;
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule rsaKeyModule = new SimpleModule();
		rsaKeyModule.addDeserializer(RSAKey.class, new RSAKeyDeserializer());
		rsaKeyModule.addSerializer(RSAKey.class, new RSAKeySerializer());
		rsaKeyModule.addSerializer(Base64URL.class, new Base64URLSerializer());
		SimpleModule jwsHeaderModule = new SimpleModule();
		jwsHeaderModule.addDeserializer(JWSHeader.class, new JWSHeaderDeserializer());
		jwsHeaderModule.addSerializer(JWSHeader.class, new JWSHeaderSerializer());

		objectMapper.registerModule(rsaKeyModule);
		objectMapper.registerModule(jwsHeaderModule);
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components().addSecuritySchemes("bearer-key",
				new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("token")));
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
						/* .allowedOrigins("*") */
						.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
						.allowCredentials(true).maxAge(3600);
			}
		};
	}

//	@Bean
//	public CommonsRequestLoggingFilter requestLoggingFilter() {
//	    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
//	    loggingFilter.setIncludeClientInfo(true);
//	    loggingFilter.setIncludeQueryString(true);
//	    loggingFilter.setIncludePayload(true);
//	    loggingFilter.setIncludeHeaders(true);
//	    return loggingFilter;
//	}

}
