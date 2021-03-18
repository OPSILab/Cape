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
package it.eng.opsi.cape.accountmanager;

import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class RestTemplateHttpClientConfiguration {

//	private static final int CONNECT_TIMEOUT = 0;
//	private static final String SOCKET_TIMEOUT = null;
//	private static final int REQUEST_TIMEOUT = 0;

	private final ApplicationProperties appConfig;
	private final int MAX_TOTAL_CONNECTIONS;
	private final int MAX_ROUTE_CONNECTIONS;
	private final int MAX_LOCALHOST_CONNECTIONS;
	private final long DEFAULT_KEEP_ALIVE_TIME;
	private final long IDLE_CONNECTION_WAIT_TIME;

	@Autowired
	public RestTemplateHttpClientConfiguration(ApplicationProperties appProperty) {
		this.appConfig = appProperty;
		this.MAX_TOTAL_CONNECTIONS = this.appConfig.getCape().getHttp().getMaxTotalConnections();
		this.MAX_ROUTE_CONNECTIONS = this.appConfig.getCape().getHttp().getMaxRouteConnections();
		this.MAX_LOCALHOST_CONNECTIONS = this.appConfig.getCape().getHttp().getMaxLocalhostConnections();
		this.DEFAULT_KEEP_ALIVE_TIME = this.appConfig.getCape().getHttp().getDefaultKeepAliveTime();
		this.IDLE_CONNECTION_WAIT_TIME = this.appConfig.getCape().getHttp().getIdleConnectionWaitTime();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingConnectionManager() {
		PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
		// set a total amount of connections across all HTTP routes
		poolingConnectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		// set a maximum amount of connections for each HTTP route in pool
		poolingConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
		// increase the amounts of connections if the host is localhost
		HttpHost localhost = new HttpHost("http://localhost");
		poolingConnectionManager.setMaxPerRoute(new HttpRoute(localhost), MAX_LOCALHOST_CONNECTIONS);
		return poolingConnectionManager;
	}

	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (httpResponse, httpContext) -> {
			HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
			HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

			while (elementIterator.hasNext()) {
				HeaderElement element = elementIterator.nextElement();
				String param = element.getName();
				String value = element.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000; // convert to ms
				}
			}

			return DEFAULT_KEEP_ALIVE_TIME;
		};
	}

	@Bean
	public Runnable idleConnectionMonitor(PoolingHttpClientConnectionManager pool) {
		return new Runnable() {
			@Override
			@Scheduled(fixedDelay = 20000)
			public void run() {
				// only if connection pool is initialised
				if (pool != null) {
					pool.closeExpiredConnections();
					pool.closeIdleConnections(IDLE_CONNECTION_WAIT_TIME, TimeUnit.MILLISECONDS);
				}
			}
		};
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("idleMonitor");
		scheduler.setPoolSize(5);
		return scheduler;
	}

	@Bean
	public CloseableHttpClient httpClient() {
//		
//		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
//				.setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		return HttpClients.custom()// .setDefaultRequestConfig(requestConfig)
				.setConnectionManager(poolingConnectionManager()).setKeepAliveStrategy(connectionKeepAliveStrategy())
				.build();
	}

}
