package com.uppi.vertx.poc.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uppi.vertx.poc.domain.Student;

import io.vertx.core.json.Json;

@Service
public class StudentService {

	private List<Student> students=new ArrayList<>();
	private final ObjectMapper mapper = Json.mapper;
	@PostConstruct
	public void initMockStudents( ) {
		students.add(new Student(1,"student-1"));
		students.add(new Student(2,"student-2"));
		students.add(new Student(3,"student-3"));
		students.add(new Student(4,"student-4"));
		students.add(new Student(5,"student-5"));
	}
	public String getStudentBy(String id) throws JsonProcessingException {
		Optional<Student> result=students.stream().filter(s->s.getId().toString().equals(id)).findFirst();
		if (result.isPresent())
			return mapper.writeValueAsString(result.get());
		else
			return mapper.writeValueAsString(new Student());
	}
	public String getAllStudent() throws JsonProcessingException{
		return mapper.writeValueAsString(students);
	}
}
