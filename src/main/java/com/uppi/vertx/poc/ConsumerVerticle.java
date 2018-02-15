package com.uppi.vertx.poc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uppi.vertx.poc.service.StudentService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
@Component
public class ConsumerVerticle extends AbstractVerticle {

	public static final String STUDENT_ALL="all";
	public static final String STUDENT_BY_ID="student-id";
	public static final String STUDENT_REQ_PARAM_NAME="studentId";
	
	@Autowired
	private StudentService studentService;
	@Override
	public void start()throws Exception {
		super.start();
        vertx.eventBus()
                .<String>consumer(STUDENT_BY_ID)
                .handler(getStudentById(studentService));
        
        vertx.eventBus()
        .<String>consumer(STUDENT_ALL)
        .handler(getStudentAllHandler(studentService));
	}
	private Handler<Message<String>> getStudentById(StudentService studentService) {
		return msg->vertx.<String>executeBlocking(hf->{
			String studentId=msg.headers().get(STUDENT_REQ_PARAM_NAME);
			try {
				hf.complete(studentService.getStudentBy(studentId));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				hf.fail(e);
			}
		}, rh->{
			if (rh.succeeded()) {
			    msg.reply(rh.result());
			} else {
				msg.reply(rh.cause().toString());
			}
		});
	}
	
	private Handler<Message<String>> getStudentAllHandler(StudentService studentService) {
		return msg->vertx.<String>executeBlocking(hf->{
			try {
				hf.complete(studentService.getAllStudent());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				hf.fail(e);
			}
		}, rh->{
			if (rh.succeeded()) {
			    msg.reply(rh.result());
			} else {
				msg.reply(rh.cause().toString());
			}
		});
	}
}
