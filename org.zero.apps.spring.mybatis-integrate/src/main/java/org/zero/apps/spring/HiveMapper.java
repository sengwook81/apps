package org.zero.apps.spring;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

public interface HiveMapper {
	@MapKey("CD_ID") //PK
	public Map<String,Map<String, Object>> getMapAll();
	
	
	public List<Map<String, Object>> getListAll();
}
