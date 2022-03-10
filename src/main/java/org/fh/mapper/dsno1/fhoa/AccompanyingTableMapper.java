package org.fh.mapper.dsno1.fhoa;
import org.fh.entity.PageData;

/** 
 * 说明： 流程伴随表Mapper
 * 作者：fsci
 * 授权：bsic
 * @version
 */
public interface AccompanyingTableMapper{
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatusToAccompanyingTable(PageData pd)throws Exception;
	
	/**根据流程实例ID修改伴随表状态和任意指定的一个字段的字段值
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatusAndFieldToAccompanyingTable(PageData pd)throws Exception;
	
	/**通过id获取状态数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData getStatusFromAccompanyingTable(PageData pd)throws Exception;
}

