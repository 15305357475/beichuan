package org.fh.mapper.dsno1.fhoa;

import org.fh.entity.PageData;
import org.fh.entity.fhoa.StaffInfo;

/**
 * 说明： 注册白名单Mapper 创建人：f-sci 授权：bsic
 */
public interface StaffInfoCheckMapper {
	/**
	 * 新增或更新
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void InsertOrUpdate(StaffInfo si) throws Exception;

	/**
	 * 通过card获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCard(String card) throws Exception;
}
