package com.vinod.nlp.ner;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NERController {

	@Autowired
	private NERService nerService; 
	
	@RequestMapping(method=RequestMethod.POST, value="/find")
	public Map<String, Set<String>> getNER(@RequestBody String text) {
		System.out.println("text :"+ text);
		return nerService.findNER(text);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/test")
	public String test() {
		return "Success";
	}
}
