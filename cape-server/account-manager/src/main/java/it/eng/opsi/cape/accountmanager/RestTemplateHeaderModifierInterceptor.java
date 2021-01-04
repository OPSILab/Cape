package it.eng.opsi.cape.accountmanager;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor {

	@Autowired
	SecurityContextHolder securityHolder;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


		try {
			OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext()
					.getAuthentication().getDetails();
//		System.out.println("Token: " + details.getTokenValue());

			if (!request.getURI().toString().contains("/user?access_token"))
				request.getHeaders().add("Authorization", "Bearer " + details.getTokenValue());

		} catch (Exception e) {
			log.warn(e.getClass().getSimpleName() + "Maybe no token provided?");
		}
		ClientHttpResponse response = execution.execute(request, body);
		return response;
	}

}
