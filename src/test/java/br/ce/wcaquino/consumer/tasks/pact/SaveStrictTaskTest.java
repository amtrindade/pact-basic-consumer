package br.ce.wcaquino.consumer.tasks.pact;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import br.ce.wcaquino.consumer.tasks.model.Task;
import br.ce.wcaquino.consumer.tasks.service.TasksConsumer;
import io.pactfoundation.consumer.dsl.LambdaDsl;

public class SaveStrictTaskTest {
	
	@Rule
	public PactProviderRule mockProvider = new PactProviderRule("Tasks", this);
	
	@Pact(consumer = "BasicConsumer")
	public RequestResponsePact createPact(PactDslWithProvider builder) {
		DslPart bodyRequest = new PactDslJsonBody()
				.stringType("task", "Task with string")
				.date("dueDate", "yyyy-MM-dd", new Date());
		
		DslPart bodyResponse = new PactDslJsonBody()
				.numberType("id")
				.stringType("task")
				.stringType("dueDate");
		
		DslPart bodyLambda = LambdaDsl.newJsonArrayMinLike(1, (arr) -> {
			arr.object((obj) -> {
				obj.numberType("id", 1L);
				obj.stringType("task", "Task Demo");
				obj.date("dueDate", "yyyy-MM-dd", new Date());
			});
		}).build();	
						
		return builder				
				.uponReceiving("Retrieve all tasks")
					.path("/todo")
					.method("GET")
				.willRespondWith()
					.status(200)
					.body(bodyLambda)
				.uponReceiving("Save a task with string")
					.path("/todo")
					.method("POST")
					.body(bodyRequest)					
				.willRespondWith()
					.status(201)
					.body(bodyResponse)
				.toPact();
	}
	
	@Test
	@PactVerification
	public void testSaveStrict() throws ClientProtocolException, IOException {
		//Arrange
		TasksConsumer consumer = new TasksConsumer(mockProvider.getUrl());
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		//Act
		Task task = consumer.saveStrictTask("Task with string", dFormat.format(new Date()));
		
		//Assert
		assertThat(task.getId(), is(notNullValue()));
		assertThat(task.getTask(), is(notNullValue()));
	}
	

}



