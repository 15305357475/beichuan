package org.fh.service.ins.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.ins.InsBoardMapper;
import org.fh.service.ins.InsBoardService;

/** 
 * 说明： 隐患排查看板接口实现类
 * 作者：f-sci
 * 时间：2021-08-30
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class InsBoardServiceImpl implements InsBoardService{

	@Autowired
	private InsBoardMapper insboardMapper;
	
	
	/**隐患类型分布
	 * @param 
	 * @throws Exception
	 */
	public List<PageData> TypeBoard()throws Exception{
		return insboardMapper.TypeBoard();
	}
	
	/**隐患状态分布
	 * @param 
	 * @throws Exception
	 */
	public List<PageData> StatusBoard()throws Exception{
		return insboardMapper.StatusBoard();
	}
	
	/**隐患发起Top
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> LaunchTop(PageData pd)throws Exception{
		return insboardMapper.LaunchTop(pd);
	}
	
	/**隐患整改Top
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> AbarbeitungTop(PageData pd)throws Exception{
		return insboardMapper.AbarbeitungTop(pd);
	}
}

