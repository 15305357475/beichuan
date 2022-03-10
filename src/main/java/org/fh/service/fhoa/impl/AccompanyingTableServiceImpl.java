package org.fh.service.fhoa.impl;

import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.AccompanyingTableMapper;
import org.fh.service.fhoa.AccompanyingTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明：流程伴随表服务接口实现类
 * 创建人：f-sci 
 * 授权：bsic
 */
@Service(value = "AccompanyingTableServiceImpl")
@Transactional // 开启事物
public class AccompanyingTableServiceImpl implements AccompanyingTableService {

	@Autowired
	private AccompanyingTableMapper accompanyingTableMapper;

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatusToAccompanyingTable(PageData pd) throws Exception {
		accompanyingTableMapper.updateStatusToAccompanyingTable(pd);
	}
	
	/**
	 * 根据流程实例ID修改伴随表状态和任意指定的一个字段的字段值
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatusAndFieldToAccompanyingTable(PageData pd) throws Exception {
		accompanyingTableMapper.updateStatusAndFieldToAccompanyingTable(pd);
	}
	
	/**通过id获取状态数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData getStatusFromAccompanyingTable(PageData pd)throws Exception{
		return (PageData)accompanyingTableMapper.getStatusFromAccompanyingTable(pd);
	}
}
