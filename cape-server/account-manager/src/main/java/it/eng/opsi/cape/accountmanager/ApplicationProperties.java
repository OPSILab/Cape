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

	@Valid
	private Idm idm = new Idm();

	@Getter
	@Setter
	@NoArgsConstructor
	public class Cape {

		@Valid
		private AccountManager accountManager = new AccountManager();

		@Valid
		private ServiceManager serviceManager = new ServiceManager();

		@Valid
		private ConsentManager consentManager = new ConsentManager();

		@Valid
		private AuditLogManager auditLogManager = new AuditLogManager();

		@Valid
		private ServiceRegistry serviceRegistry = new ServiceRegistry();

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

			private String eventLogCollection;

			private String auditLogCollection;

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

	@Getter
	@Setter
	@NoArgsConstructor
	public class Idm {

		@NotBlank
		private String host;

//		@Valid
//		private Path path;
//
//		@Getter
//		@Setter
//		@NoArgsConstructor
//		public class Path {
//
//			@NotBlank
//			private String token;
//
//			@NotBlank
//			private String user;
//
//		}

		@NotBlank
		private String clientId;

		@NotBlank
		private String clientSecret;
//
//		@NotBlank
//		private String redirectUri;
//
//		@NotBlank
//		private String logoutCallback;

	}

}
