package br.ce.wcaquino.consumer.tasks.pact.barriga;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.ConsumerPactRunnerKt;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.model.MockProviderConfig;
import au.com.dius.pact.core.model.RequestResponsePact;
import br.ce.wcaquino.consumer.barriga.service.BarrigaConsumer;

public class SaveAccountTest {
	private final String TOKEN = "JWT invalid";
	
	@Test
	public void test() {
		PactDslJsonBody requestBody = new PactDslJsonBody()
				.stringValue("nome", "Acc test");
		
		PactDslJsonBody responseBody = new PactDslJsonBody()
				.numberType("id")
				.stringValue("nome", "Acc test");
		
		
		RequestResponsePact pact = ConsumerPactBuilder
				.consumer("BasicConsumer")
				.hasPactWith("Barriga")
				.given("I have a valid token")
				.uponReceiving("Insert account 'Acc test'")
					.path("/contas")
					.method("POST")
					//.matchHeader("Authorization", "JWT .*", TOKEN)
					.headerFromProviderState("Authorization", "${token}", TOKEN)
					.body(requestBody)
				.willRespondWith()
					.status(201)
					.body(responseBody)
				.toPact();
		
		MockProviderConfig config = MockProviderConfig.createDefault();
		PactVerificationResult result = ConsumerPactRunnerKt.runConsumerTest(pact, config, (mockServer, context) -> {
			BarrigaConsumer consumer = new BarrigaConsumer(mockServer.getUrl());
			String insertAccount = consumer.insertAccount("Acc test", TOKEN);
			assertThat(insertAccount, is(notNullValue()));
			return null;
		});
		
		if (result instanceof PactVerificationResult.Error)
			throw new RuntimeException(((PactVerificationResult.Error) result).getError());
		assertThat(result, is(instanceOf(PactVerificationResult.Ok.class)));
	}

}
