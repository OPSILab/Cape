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
package it.eng.opsi.cape.consentmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.ListableBeanFactory;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
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
import it.eng.opsi.cape.consentmanager.repository.Base64URLReadConverter;
import it.eng.opsi.cape.consentmanager.repository.Base64URLWriteConverter;
import it.eng.opsi.cape.consentmanager.repository.JWSHeaderReadConverter;
import it.eng.opsi.cape.consentmanager.repository.JWSHeaderWriteConverter;
import it.eng.opsi.cape.consentmanager.repository.RSAKeyReadConverter;
import it.eng.opsi.cape.consentmanager.repository.RSAKeyWriteConverter;
import it.eng.opsi.cape.consentmanager.repository.ZonedDateTimeReadConverter;
import it.eng.opsi.cape.consentmanager.repository.ZonedDateTimeWriteConverter;
import it.eng.opsi.cape.consentmanager.utils.Base64URLSerializer;
import it.eng.opsi.cape.consentmanager.utils.JWSHeaderDeserializer;
import it.eng.opsi.cape.consentmanager.utils.JWSHeaderSerializer;
import it.eng.opsi.cape.consentmanager.utils.RSAKeyDeserializer;
import it.eng.opsi.cape.consentmanager.utils.RSAKeySerializer;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "it.eng.opsi.cape.consentmanager.repository")
@Slf4j
public class ConsentManagerApplication extends SpringBootServletInitializer {

	@Autowired
	CloseableHttpClient httpClient;

	private static ConfigurableApplicationContext applicationContext;

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	@Value("${cape.cors.allowed-origin-patterns}")
	private String[] corsAllowedOriginPatterns;

	@Value("${cape.cors.allowed-origins}")
	private String[] corsAllowedOrigins;
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ConsentManagerApplication.class);
	}

	public static void main(String[] args) {
		applicationContext = SpringApplication.run(ConsentManagerApplication.class, args);
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
	public RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplateBuilder().requestFactory(this::clientHttpRequestFactory)
				.errorHandler(new RestTemplateResponseErrorHandler(applicationContext)).build();

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
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
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
						.allowedOriginPatterns(corsAllowedOriginPatterns).allowedOrigins(corsAllowedOrigins)
						.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
						.allowCredentials(true).maxAge(3600);
			}
		};
	}

	@Configuration
	public class EnumConfig implements WebMvcConfigurer {

		@Autowired
		private ListableBeanFactory beanFactory;

		@Override
		public void addFormatters(FormatterRegistry registry) {
			Map<String, Object> components = beanFactory.getBeansWithAnnotation(RequestParameterConverter.class);
			components.values().parallelStream().forEach(c -> {
				if (c instanceof Converter) {
					registry.addConverter((Converter) c);
				}
			});
		}

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
