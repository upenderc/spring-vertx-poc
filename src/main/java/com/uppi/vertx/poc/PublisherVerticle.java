package com.uppi.vertx.poc;

import static com.uppi.vertx.poc.ConsumerVerticle.STUDENT_REQ_PARAM_NAME;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uppi.vertx.poc.configuration.YAMLConfiguration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
@Component
public class PublisherVerticle extends AbstractVerticle {
	
	private static final Logger LOG = Logger.getLogger(PublisherVerticle.class);
	@Autowired
	private YAMLConfiguration yamlConfiguration;
	@Override
	public void start()throws Exception {
		super.start();
		
		 Router router = Router.router(vertx);
	        router.get("/students/:studentId/courses")
	            .handler(this::getStudentById);
	        router.get("/students/all")
            .handler(this::getAllStudents);
	        
	        vertx.createHttpServer()
	               .requestHandler(router::accept)
	               .listen(config().getInteger("http.port",yamlConfiguration.getServer().getPort()));
	        publishStudentByIdToZookeeperRepo();
	        publishAllStudentsToZookeeperRepo();
	}
	private void getStudentById(RoutingContext rc) {
		DeliveryOptions options = new DeliveryOptions();
		options.addHeader(STUDENT_REQ_PARAM_NAME ,rc.request().getParam(STUDENT_REQ_PARAM_NAME));
		
		vertx.eventBus().<String>send(ConsumerVerticle.STUDENT_BY_ID, "",options,response->{
			if (response.succeeded()) {
				rc.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(200)
                .end(response.result()
                .body());
			} else {
				rc.response()
                .setStatusCode(500)
                .end();
			}
		});
	}

  private void getAllStudents(RoutingContext rc) {
		
		vertx.eventBus().<String>send(ConsumerVerticle.STUDENT_ALL, "",response->{
			if (response.succeeded()) {
				rc.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(200)
                .end(response.result()
                .body());
			} else {
				rc.response()
                .setStatusCode(500)
                .end();
			}
		});
	}
  private void publishStudentByIdToZookeeperRepo() {
	  
      Record record=HttpEndpoint.createRecord("studentid-api-service", "localhost",
    		  9999,"/students");
      ServiceDiscovery discovery= ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
              .setBackendConfiguration(
                  buildZookeeperConnection()
              ));
      discovery.publish(record, r->{
      	if(r.succeeded()) {
      		LOG.info("successfully published to zookeeper>>>>> "+r.result().toJson());
      	} else {
      		r.cause().printStackTrace();
      	}
      });
  }
  private JsonObject buildZookeeperConnection() {
		return new JsonObject()
		      .put("connection", yamlConfiguration.getZookeeper().getConnect())
		      .put("ephemeral", true)
		      .put("guaranteed", true)
		      .put("basePath", yamlConfiguration.getZookeeper().getBasePath());
  }
  private void publishAllStudentsToZookeeperRepo() {
	  ServiceDiscovery discovery= ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
              .setBackendConfiguration(
            		  buildZookeeperConnection()
              ));
      Record record=HttpEndpoint.createRecord("students-all", "localhost",
    		  9999,"/students");
      discovery.publish(record, r->{
      	if(r.succeeded()) {
      		LOG.info("successfully published to zookeeper>>>>> "+r.result().toJson());
      	} else {
      		r.cause().printStackTrace();
      	}
      });
  }
}
