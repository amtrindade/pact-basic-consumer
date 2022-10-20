package br.ce.wcaquino.consumer.tasks.service;

import java.io.IOException;

import br.ce.wcaquino.consumer.barriga.service.BarrigaConsumer;
import br.ce.wcaquino.consumer.tasks.model.Task;

public class DoubleDependency {
	
	private String barrigaURL;
	private String tasksURL;
	
	public DoubleDependency(String barrigaURL, String tasksURL) {
		this.barrigaURL = barrigaURL;
		this.tasksURL = tasksURL;
	}
	
	public String getTokenAndCreateTask(String user, String passwd) throws IOException {
		BarrigaConsumer barrigaConsumer = new BarrigaConsumer(barrigaURL);
		TasksConsumer tasksConsumer = new TasksConsumer(tasksURL);
		
		String token = barrigaConsumer.login(user, passwd);
		Task saveTask = tasksConsumer.saveTask("Expire token: "+token, "2050-10-20");
		
		return saveTask.getTask();
	}
	
	public static void main(String[] args) throws IOException {
		DoubleDependency dd = new DoubleDependency("https://barrigarest.wcaquino.me", "http://localhost:8005");
		String task = dd.getTokenAndCreateTask("bozo@mail.com", "a");
		System.out.println(task);
	}
}
