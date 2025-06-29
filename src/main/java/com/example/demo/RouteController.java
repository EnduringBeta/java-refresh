package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }

	@GetMapping("/")
	public String index() {
		return "Nothing here: try /hello or /greeting";
	}

	/*
	@GetMapping("/error")
	public String error(@RequestParam() String error) {
		return String.format("Error: %s", error);
	}
	*/
}
