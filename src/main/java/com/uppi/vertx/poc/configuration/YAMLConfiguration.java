package com.uppi.vertx.poc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vertx")
public class YAMLConfiguration {

	private Server server;
	private Zookeeper zookeeper;
	
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
		this.server = server;
	}
	public Zookeeper getZookeeper() {
		return zookeeper;
	}
	public void setZookeeper(Zookeeper zookeeper) {
		this.zookeeper = zookeeper;
	}
	public static class Server {
		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		private Integer port;
		
	}
	public static class Zookeeper {
		private String connect;
		private String basePath;
		public String getConnect() {
			return connect;
		}
		public void setConnect(String connect) {
			this.connect = connect;
		}
		public String getBasePath() {
			return basePath;
		}
		public void setBasePath(String basePath) {
			this.basePath = basePath;
		}
		
	}
}
