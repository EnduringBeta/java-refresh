package com.example.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
	private static final String template = "Howdy, %s!";
	private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
	public Greeting greeting(@RequestParam(defaultValue = "User") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
}
