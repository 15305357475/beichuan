package org.fh.service.fhoa;

import org.fh.entity.PageData;
import org.fh.entity.fhoa.StaffInfo;

/**
 * 说明： 注册授权白名单接口 创建人：f-sci 授权：bsic
 */
public interface StaffInfoCheckService {
	/**
	 * 新增或更新
	 * 
	 * @param Registered
	 * @throws Exception
	 */
	public void InsertOrUpdate(StaffInfo si) throws Exception;

	/**
	 * 通过card获取数据
	 * 
	 * @param card
	 * @throws Exception
	 */
	public PageData findByCard(String card) throws Exception;
}
