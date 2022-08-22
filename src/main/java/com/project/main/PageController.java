package com.project.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class PageController {
	@GetMapping("/")
	public String index() {
		return "index";
	}
}