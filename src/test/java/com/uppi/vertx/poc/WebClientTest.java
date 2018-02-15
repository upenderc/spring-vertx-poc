package com.uppi.vertx.poc;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class WebClientTest {

	//call rest weather api client async
	@Test
	public void weatherInfo()throws Exception {
		 Vertx vertx = Vertx.vertx();
		 WebClient client = WebClient.create(vertx);
		 client.get("")
		 .addQueryParam("q","London")
		 .addQueryParam("mode","json")
		 .addQueryParam("appid","b6907d289e10d714a6e88b30761fae22")
		 .send(r->{
			 if (r.succeeded()) {
				 HttpResponse<Buffer> response = r.result();

			      System.out.println("Received response with status code" + response.statusCode());
			    }
			 else {
			      System.out.println("Something went wrong " + r.cause().getMessage());
			    }
		 });
		 
		 TimeUnit.SECONDS.sleep(5);
	}
	//call rest weather api client async
	@Test
	public void weatherRouter()throws Exception {
		 Vertx vertx = Vertx.vertx();
		 WebClient client = WebClient.create(vertx);
		 client.getAbs("http://samples.openweathermap.org/data/2.5/weather?q=London&mode=xml&appid=b6907d289e10d714a6e88b30761fae22")
		 .send(r->{
			 if (r.succeeded()) {
				 HttpResponse<Buffer> response = r.result();
			      System.out.println("Received response with status code" + response.statusCode());
			      System.out.println(response.bodyAsString());
			    }
			 else {
			      System.out.println("Something went wrong " + r.cause().getMessage());
			    }
		 });
		 
		 TimeUnit.SECONDS.sleep(5);
	}
	//call rest iso api client async
	@Test
	public void restClientForISO()throws Exception {
		Vertx vertx = Vertx.vertx();
		HttpClientOptions opt=new HttpClientOptions();
		opt.setMaxPoolSize(100);
		opt.setMaxWaitQueueSize(1000);
		opt.setConnectTimeout(1000);
		
		final HttpClient httpClient = vertx.createHttpClient(opt);
		

	    final String url = "http://services.groupkt.com/country/get/iso2code/IN";
	    httpClient.getAbs(url, response -> {
	        if (response.statusCode() != 200) {
	            System.err.println("fail");
	        } else {
	            response.bodyHandler(b -> System.out.println(b.toString()));
	        }
	    }).end();
		
		System.out.println("it is async");
		TimeUnit.SECONDS.sleep(5);
	}
	
	@Test
	public void studentByIdFromServiceDiscovery() throws InterruptedException {
		final Vertx vertx = Vertx.vertx();
		 ServiceDiscovery zookeeperDiscovery= ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
	                .setBackendConfiguration(
	                    new JsonObject()
	                        .put("connection", "127.0.0.1:2181")
	                        .put("ephemeral", true)
	                        .put("guaranteed", true)
	                        .put("basePath", "/services/my-api-backend")
	                ));
		 HttpEndpoint.getClient(zookeeperDiscovery, new JsonObject().put("name", "students-all"), ar -> {
			  if (ar.succeeded()) {
			    HttpClient client = ar.result();
			    if (ar.succeeded()) {
			    	System.out.println("Success");
			    }
			    // You need to path the complete path
			    client.getNow("/students/all", response -> {

			      System.out.println("Status Code===="+response.statusCode());
			      response.bodyHandler(bh->{
			    	  System.out.println("Rec "+new String(bh.getBytes()));
			    	  ServiceDiscovery.releaseServiceObject(zookeeperDiscovery, client);
			      });
			      

			    });
			  }
			});
		 TimeUnit.SECONDS.sleep(3);
	}
	
}
