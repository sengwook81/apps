package org.zero.app.spring.mvc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zero.app.spring.mvc.dto.TestDto;

@Controller
public class ContentNegotiationCTR {

	private static Logger logger =  LoggerFactory.getLogger(ContentNegotiationCTR.class);
	
	private static List<TestDto> sampleData = new ArrayList<TestDto>();
	
	static {
		for(int i = 0;i<10;i++)
		{
			sampleData.add(new TestDto(i, "str1-"+ i , "str2-" +i));	
		}
		
	}
	@RequestMapping("contentnagotiation")
	public Model contentNegotiation(Model model)
	{
		model.addAttribute("result",sampleData);
		return model;
	}
	
	@RequestMapping("contentnagotiation_input")
	public Model contentNegotiationInput(TestDto dto ,Model model)
	{
		logger.debug("Mvc Input : {}",dto);
		model.addAttribute("input",dto);
		model.addAttribute("result",sampleData);
		return model;
	}
	
	@RequestMapping("contentnagotiation_model")
	public Model contentNegotiationModel(@ModelAttribute TestDto dto ,Model model)
	{
		logger.debug("Mvc Input : {}",dto);
		model.addAttribute("input",dto);
		model.addAttribute("result",sampleData);
		return model;
	}
	
	
	
	@RequestMapping("contentnagotiation_body")
	public @ResponseBody Model contentNegotiationBody(@RequestBody TestDto dto ,Model model)
	{
		logger.debug("Mvc Input : {}",dto);
		model.addAttribute("input",dto);
		model.addAttribute("result",sampleData);
		return model;
	}
	
	@RequestMapping("contentnagotiation_dual")
	public @ResponseBody Model contentNegotiationDual(@RequestBody TestDto dto ,Model model)
	{
		logger.debug("Mvc Input : {}",dto);
		model.addAttribute("input",dto);
		model.addAttribute("result",sampleData);
		return model;
	}
	
	
	
}
