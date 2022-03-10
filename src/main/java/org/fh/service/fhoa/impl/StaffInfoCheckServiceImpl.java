package org.fh.service.fhoa.impl;

import org.fh.entity.PageData;
import org.fh.entity.fhoa.StaffInfo;
import org.fh.mapper.dsno1.fhoa.StaffInfoCheckMapper;
import org.fh.service.fhoa.StaffInfoCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明： 注册白名单 创建人：f-sci 授权：bsic
 */
@Service(value = "StaffInfoCheckService")
@Transactional // 开启事物
public class StaffInfoCheckServiceImpl implements StaffInfoCheckService {

	@Autowired
	private StaffInfoCheckMapper StaffInfoCheckMapper;

	/**
	 * 新增或更新
	 * 
	 * @param Registered
	 * @throws Exception
	 */
	public void InsertOrUpdate(StaffInfo si) throws Exception {
		StaffInfoCheckMapper.InsertOrUpdate(si);
	}

	/**
	 * 通过card获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCard(String card) throws Exception {
		return (PageData) StaffInfoCheckMapper.findByCard(card);
	}
}
