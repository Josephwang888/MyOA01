package com.shop.service;

import java.util.List;

import com.shop.pojo.Baoxiaobill;
import com.sun.org.apache.bcel.internal.generic.Select;

public interface BaoxiaoService {
	void saveStartBaoxiao(Baoxiaobill baoxiaobill);
	
	List<Baoxiaobill> selectByid(Long id);
	
	void deleteByPrimaryKey(Long id);
}
