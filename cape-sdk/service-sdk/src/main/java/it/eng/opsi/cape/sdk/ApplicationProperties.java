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
package it.eng.opsi.cape.sdk;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties
@Validated
public class ApplicationProperties {

	@Valid
	private Cape cape = new Cape();

//	@Valid
//	private Idm idm = new Idm();

	@Getter
	@Setter
	@NoArgsConstructor
	public class Cape {

		@Valid
		private ServiceSdk serviceSdk = new ServiceSdk();

		@Valid
		private AccountManager accountManager = new AccountManager();

		@Valid
		private ServiceManager serviceManager = new ServiceManager();

		@Valid
		private ConsentManager consentManager = new ConsentManager();

//		@Valid
//		private AuditLogManager auditLogManager = new AuditLogManager();

//		@Valid
//		private DataSecurityManager dataSecurityManager = new DataSecurityManager();

		@Valid
		private ServiceRegistry serviceRegistry = new ServiceRegistry();

		@Valid
		private Cors cors = new Cors();
		
		@Valid
		private Http http = new Http();

		@NotBlank
		private String version;

		@NotBlank
		private String releaseTimestamp;

		@NotBlank
		private String operatorId;

		@Getter
		@Setter
		@NoArgsConstructor
		public class Cors {

			private String[] allowedOriginPatterns;
			private String[] allowedOrigins;

		}
		
		@Getter
		@Setter
		@NoArgsConstructor
		public class AccountManager {

			@NotBlank
			private String host;

			private String collection;
		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class ServiceManager {

			@NotBlank
			private String host;

			private String collection;

			@Valid
			private ServiceLinking serviceLinking = new ServiceLinking();

			public class ServiceLinking {

				private String returnUrl;

			}

		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class ConsentManager {

			@NotBlank
			private String host;

		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class AuditLogManager {

			@NotBlank
			private String host;

			@NotBlank
			private String eventLogCollection;

			@NotBlank
			private String auditLogCollection;

		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class ServiceSdk {

			@NotBlank
			private String businessId;

			private String host;

		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class DataSecurityManager {

			@NotBlank
			private String host;

			@NotBlank
			private String userKeysCollection;

		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class ServiceRegistry {

			@NotBlank
			private String host;

		}

		@Getter
		@Setter
		@NoArgsConstructor
		public class Http {

			@NotNull
			private Boolean proxyEnabled;

			@NotNull
			private String proxyHost;

			@NotNull
			private String proxyUser;

			@NotNull
			private String proxyPort;

			@NotNull
			private String proxyPassword;

			@NotBlank
			private String nonProxyHosts;

			@NotNull
			private int maxTotalConnections;

			@NotNull
			private int maxRouteConnections;

			@NotNull
			private int maxLocalhostConnections;

			@NotNull
			private int defaultKeepAliveTime;

			@NotNull
			private int idleConnectionWaitTime;

		}
	}

//	@Getter
//	@Setter
//	@NoArgsConstructor
//	public class Idm {
//
//		@NotBlank
//		private String host;
////
////		@Valid
////		private Path path;
////
////		@Getter
////		@Setter
////		@NoArgsConstructor
////		public class Path {
////
////			@NotBlank
////			private String token;
////
////			@NotBlank
////			private String user;
////
////		}
////
//		@NotBlank
//		private String clientId;
//
//		@NotBlank
//		private String clientSecret;
////
////		@NotBlank
////		private String redirectUri;
////
////		@NotBlank
////		private String logoutCallback;
//
//	}

}
