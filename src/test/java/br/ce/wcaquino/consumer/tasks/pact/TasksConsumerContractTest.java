package br.ce.wcaquino.consumer.tasks.pact;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.hamcrest.CoreMatchers;
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

public class TasksConsumerContractTest {
	
	@Rule
	public PactProviderRule mockProvider = new PactProviderRule("Tasks", this);
	
	@Pact(consumer = "BasicConsumer")
	public RequestResponsePact createPact(PactDslWithProvider builder) {
		DslPart body = new PactDslJsonBody()
				.numberType("id", 1L)
				.stringType("task")
				.stringType("dueDate");
						
		return builder
				.given("There is a task with id = 1")
				.uponReceiving("Retrieve Task #1")
					.path("/todo/1")
					.method("GET")
				.willRespondWith()
					.status(200)
					.body(body)
//					.body("{\"id\": 1, \"task\": \"Task from pact\", \"dueDate\": \"2020-01-01\"}")
				.toPact();
	}
	
	@Test
	@PactVerification
	public void test() throws ClientProtocolException, IOException {
		//Arrange
		TasksConsumer consumer = new TasksConsumer(mockProvider.getUrl());
		System.out.println(mockProvider.getUrl());
		
		//Act
		Task task = consumer.getTask(1L);
		System.out.println(task);
		
		//Assert
		assertThat(task.getId(), is(notNullValue()));
		assertThat(task.getId(), is(1L));
		assertThat(task.getTask(), is(notNullValue()));
	}
	

}



