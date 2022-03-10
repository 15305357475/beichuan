package org.fh.service.fhoa;
import org.fh.entity.PageData;

/**
 * 说明：流程伴随表服务接口
 * 作者：f-sci
 * 授权：bsic
 */
public interface AccompanyingTableService {
	
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
