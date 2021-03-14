package com.shop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.BaoxiaobillMapper;
import com.shop.pojo.Baoxiaobill;
import com.shop.service.BaoxiaoService;
@Service("baoxiaoService")
public class BaoxiaoServiceImpl implements BaoxiaoService {
	
	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper; 
	@Override
	public void saveStartBaoxiao(Baoxiaobill baoxiaobill) {
		this.baoxiaobillMapper.insert(baoxiaobill);
		
	}
	@Override
	public List<Baoxiaobill> selectByid(Long id) {
		return (List<Baoxiaobill>) baoxiaobillMapper.selectByPrimaryKey(id);
	}
	@Override
	public void deleteByPrimaryKey(Long id) {
		this.baoxiaobillMapper.deleteByPrimaryKey(id);
		
	}
   
}
