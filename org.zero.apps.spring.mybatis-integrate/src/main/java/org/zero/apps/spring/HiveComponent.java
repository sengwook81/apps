package org.zero.apps.spring;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HiveComponent {

	@Autowired(required=false)
	HiveMapper mapper;
	
	public Map<String,Map<String, Object>> getMapAll() {
		return mapper.getMapAll();
	}
	
	public List<Map<String, Object>> getListAll() {
		return mapper.getListAll();
	}
}
