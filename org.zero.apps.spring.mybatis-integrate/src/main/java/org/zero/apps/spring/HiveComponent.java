package org.zero.apps.spring;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HiveComponent {

	@Autowired
	HiveMapper mapper;
	
	public List<Map<String, Object>> getAll() {
		return mapper.getAll();
	}
}
