package org.fh.mapper.dsno1.ins;

import java.util.List;
import org.fh.entity.PageData;

/** 
 * 说明： 隐患排查看板Mapper
 * 作者：fsci
 * 时间：2021-08-30
 * 授权：bsic
 * @version
 */
public interface InsBoardMapper{

	
	/**隐患类型分布
	 * @param 
	 * @throws Exception
	 */
	List<PageData> TypeBoard();
	
	/**隐患状态分布
	 * @param 
	 * @throws Exception
	 */
	List<PageData> StatusBoard();
	
	/**隐患发起Top
	 * @param 
	 * @throws Exception
	 */
	List<PageData> LaunchTop(PageData pd);
	
	/**隐患整改Top
	 * @param 
	 * @throws Exception
	 */
	List<PageData> AbarbeitungTop(PageData pd);
}

