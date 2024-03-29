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
package it.eng.opsi.cape.auditlogmanager.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	@Value("${cape.enableAuth}")
	private Boolean enableAuth;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		HttpSecurity baseConfig = http.cors().and().csrf().disable().formLogin().disable().headers()
				.httpStrictTransportSecurity().disable().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and();

		if (enableAuth)
			baseConfig
					.authorizeRequests(authz -> authz.antMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**")
							.permitAll().anyRequest().authenticated())
					.oauth2ResourceServer(oauth2 -> oauth2.jwt());
		else
			baseConfig.authorizeRequests(authz -> authz.anyRequest().permitAll());
	}

}
