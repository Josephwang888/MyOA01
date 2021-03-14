package com.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.LeavebillMapper;
import com.shop.pojo.Leavebill;
import com.shop.service.LeavebillService;


@Service("leavebillService")
public class LeavebillServiceImpl implements LeavebillService {

	
	@Autowired
	private LeavebillMapper leaveBillMapper;
	@Override
	public void saveLeaveBill(Leavebill leaveBill) {
		
		this.leaveBillMapper.insert(leaveBill);
	}

}
