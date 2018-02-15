package com.uppi.vertx.poc.application;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.uppi.vertx.poc.ConsumerVerticle;
import com.uppi.vertx.poc.PublisherVerticle;

import io.vertx.core.Vertx;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"com.uppi.vertx.poc"})
public class VertxZookeeperSpringBootApplication {

	@Autowired
	private ConsumerVerticle consumerVerticle;
	@Autowired
	private PublisherVerticle publishVerticle;
	public static void main(String ...args) {
		SpringApplication.run(VertxZookeeperSpringBootApplication.class, args);
	}
	@PostConstruct
	public void deployVerticles() {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(publishVerticle);
		vertx.deployVerticle(consumerVerticle);
		
	}
}
