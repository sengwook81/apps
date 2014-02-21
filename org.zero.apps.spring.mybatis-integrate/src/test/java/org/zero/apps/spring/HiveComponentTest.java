package org.zero.apps.spring;

import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zero.apps.common.CommonMapper;
import org.zero.apps.common.HiveVo;
import org.zero.commons.spring.support.proxy.ExtInfoProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class HiveComponentTest {

	@Autowired
	HiveComponent component;

	@Autowired
	ApplicationContext context;

	@Autowired
	DefaultSqlSessionFactory sessionFactory;

	@Autowired
	HiveMapper hiveMapper;

	public static interface Marker {
		public String getItem();
	}

	public static class Item implements Marker {
		private String item;

		public String getItem() {
			return item;
		}

		@Override
		public String toString() {
			return "Item [item=" + item + "]";
		}

		public void setItem(String item) {
			this.item = item;
		}

	}

	@Autowired
	CommonMapper commonMapper;

	@Test
	public void test() {

		for (String mapId : sessionFactory.getConfiguration()
				.getMappedStatementNames()) {
			System.out.println("MAP ID : " + mapId);
		}
		HiveVo hv = new HiveVo();
		hv.setName("SengWook");
		Item item = new Item();
		item.setItem("123123");;
		HiveVo createProxy = new ExtInfoProxy().createProxy(hv, item);

		Marker a = ((Marker)createProxy);
		//System.out.println(hiveMapper.getListAll());
		//commonMapper.getListCommonsAll(createProxy);
		
		System.out.println(a.getItem());
		System.out.println(a.toString());
	}

}
