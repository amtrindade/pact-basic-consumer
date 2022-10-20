package br.ce.wcaquino.consumer.tasks.pact;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.ConsumerPactRunnerKt;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.model.MockProviderConfig;
import au.com.dius.pact.core.model.RequestResponsePact;
import br.ce.wcaquino.consumer.tasks.service.DoubleDependency;

public class DoubleDependencyTest {
	
	@Test
	public void test() {
		DslPart bodyRequest = new PactDslJsonBody()
				.stringType("email", "bozo@mail.com")
				.stringType("senha", "a");
		
		DslPart bodyResponse = new PactDslJsonBody()
				.stringType("token");
		
		RequestResponsePact bPact = ConsumerPactBuilder
				.consumer("BasicConsumer")
				.hasPactWith("Barriga")
				.given("Your user is created")
				.uponReceiving("Signin with a valid user")
					.path("/signin")
					.method("POST")
					.body(bodyRequest)
				.willRespondWith()
					.status(200)
					.body(bodyResponse)
				.toPact();
		
		DslPart bodyTaskRequest = new PactDslJsonBody()
				.stringType("task", "Task with string")
				.date("dueDate", "yyyy-MM-dd", new Date());
		
		DslPart bodyTaskResponse = new PactDslJsonBody()
				.numberType("id")
				.stringType("task")
				.date("dueDate", "yyyy-MM-dd", new Date());
						
		RequestResponsePact	tPact = ConsumerPactBuilder	
				.consumer("BasicConsumer")
				.hasPactWith("Tasks")
				.uponReceiving("Save a task with string")
					.path("/todo")
					.method("POST")
					.body(bodyTaskRequest)					
				.willRespondWith()
					.status(201)
					.body(bodyTaskResponse)
				.toPact();
		
		MockProviderConfig config = MockProviderConfig.createDefault();
		
		PactVerificationResult bResult = ConsumerPactRunnerKt.runConsumerTest(bPact, config, (mockServer, context) -> {
			PactVerificationResult tResult = ConsumerPactRunnerKt.runConsumerTest(tPact, config, (tMockServer, tContext) -> {
				
				DoubleDependency dd = new DoubleDependency(mockServer.getUrl(), tMockServer.getUrl());
				String task = dd.getTokenAndCreateTask("bozo@mail.com", "a");
				System.out.println("##" + task);
				Assert.assertNotNull(task);
				return null;
			});
			if (tResult instanceof PactVerificationResult.Error)
				throw new RuntimeException(((PactVerificationResult.Error) tResult).getError());
			assertThat(tResult, is(instanceOf(PactVerificationResult.Ok.class)));
			
			return null;
		});
		
		if (bResult instanceof PactVerificationResult.Error)
			throw new RuntimeException(((PactVerificationResult.Error) bResult).getError());
		assertThat(bResult, is(instanceOf(PactVerificationResult.Ok.class)));
	}

}
