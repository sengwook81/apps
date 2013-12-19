package org.zero.apps.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class HiveComponentTest {

	@Autowired
	HiveComponent component;
	
	@Autowired
	ApplicationContext context;
	@Test
	public void test() {
		
		for(String name : context.getBeanDefinitionNames()) { 
			System.out.println("Bean Name : " +name);
		}
		System.out.println("HEllo");
		System.out.println(component.getAll());
	}

}
