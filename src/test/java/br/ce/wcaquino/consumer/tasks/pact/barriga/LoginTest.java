package br.ce.wcaquino.consumer.tasks.pact.barriga;

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
import br.ce.wcaquino.consumer.barriga.service.BarrigaConsumer;

public class LoginTest {
	
	@Rule
	public PactProviderRule mockProvider = new PactProviderRule("Barriga", this);
	
	@Pact(consumer = "BasicConsumer")
	public RequestResponsePact createPact(PactDslWithProvider builder) {
		DslPart bodyRequest = new PactDslJsonBody()
				.stringType("email", "bozo@mail.com")
				.stringType("senha", "a");
		
		DslPart bodyResponse = new PactDslJsonBody()
				.stringType("token");
						
		return builder
				.given("Your user is created")
				.uponReceiving("Signin with a valid user")
					.path("/signin")
					.method("POST")
					.body(bodyRequest)
				.willRespondWith()
					.status(200)
					.body(bodyResponse)
				.toPact();
	}
	
	@Test
	@PactVerification
	public void shouldSignin() throws ClientProtocolException, IOException {
		//Arrange
		BarrigaConsumer consumer = new BarrigaConsumer(mockProvider.getUrl());
		System.out.println(mockProvider.getUrl());
		
		//Act
		String token = consumer.login("bozo@mail.com", "a");
				
		//Assert
		assertThat(token, CoreMatchers.is(notNullValue()));
	}

}
