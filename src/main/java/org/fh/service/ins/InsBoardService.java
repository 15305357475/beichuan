package org.fh.service.ins;

import java.util.List;
import org.fh.entity.PageData;

/** 
 * 说明： 隐患排查看板接口
 * 作者：fsci
 * 时间：2021-08-30
 * 授权：bsic
 * @version
 */
public interface InsBoardService{
	
	/**隐患类型分布
	 * @param 
	 * @throws Exception
	 */
	public List<PageData> TypeBoard() throws Exception;
	
	/**隐患状态分布
	 * @param 
	 * @throws Exception
	 */
	public List<PageData> StatusBoard() throws Exception;
	
	/**隐患发起Top
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> LaunchTop(PageData pd) throws Exception;
	
	/**隐患整改Top
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> AbarbeitungTop(PageData pd) throws Exception;
}


