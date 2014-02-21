package org.zero.apps.spring;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zero.apps.common.HiveVo;

@Component
public class HiveComponent {

	@Autowired(required=false)
	HiveMapper mapper;
	
	public Map<String,Map<String, Object>> getMapAll(HiveVo item) {
		return mapper.getMapAll();
	}
	
	public List<Map<String, Object>> getListAll() {
		return mapper.getListAll();
	}
}
