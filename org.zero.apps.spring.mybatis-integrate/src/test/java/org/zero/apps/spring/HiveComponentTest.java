package org.zero.apps.spring;

import static org.junit.Assert.*;

import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
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
	
	@Autowired
	DefaultSqlSessionFactory sessionFactory;
	
	@Test
	public void test() {
	
		for(String mapId : sessionFactory.getConfiguration().getMappedStatementNames()) {
			System.out.println("MAP ID : " + mapId);
		}

		System.out.println("List Return : " + component.getListAll());
		System.out.println("Map Return : " + component.getMapAll());
	}

}
