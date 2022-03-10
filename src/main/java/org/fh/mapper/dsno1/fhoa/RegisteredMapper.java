package org.fh.mapper.dsno1.fhoa;

import java.util.List;

import org.fh.entity.PageData;
import org.fh.entity.fhoa.Registered;

/**
 * 说明： 注册白名单Mapper 创建人：f-sci 授权：bsic
 */
public interface RegisteredMapper {
	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(Registered registered) throws Exception;

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(String card) throws Exception;

	/**
	 * 通过身份证、姓名或工号查询数据
	 *
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByCodeOrName(PageData pd) throws Exception;

	/**
	 * 获取全部数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<Registered> getAll() throws Exception;

	/**
	 * 更新状态
	 * 
	 * @param register
	 * @throws Exception
	 */
	public void updateStatus(Registered register) throws Exception;

	/**
	 * 通过身份证号批量修改注册状态为0
	 * 
	 * @param pd
	 * @throws Exception
	 */
	void updateStatusByCardIdTo0(String[] USER_IDS);

	/**
	 * 通过身份证号批量修改注册状态为1
	 * 
	 * @param pd
	 * @throws Exception
	 */
	void updateStatusByCardIdTo1(String[] USER_IDS);
	
	/**通过身份证号删除
	 * @param CARD
	 * @throws Exception
	 */
	public void delete(String CARD)throws Exception;
	
	/**
	 * 通过身份证号更新白名单基础数据
	 * 
	 * @param register
	 * @throws Exception
	 */
	public void updateListInfo(Registered register) throws Exception;

	/**
	 * 通过身份证号更新照片下载状态
	 *
	 * @param register
	 * @throws Exception
	 */
	public void updateALREADY_DOWN(Registered register) throws Exception;

	/**
	 *  随机选择一个数据
	 *
	 * @param pd
	 * @throws Exception
	 */
	public PageData shake(PageData pd) throws Exception;
}
