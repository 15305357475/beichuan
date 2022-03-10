package org.fh.service.sbxj.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.sbxj.SbxjSysMapper;
import org.fh.service.sbxj.SbxjSysService;

/** 
 * 说明： 隐患排查主表接口实现类
 * 作者：f-sci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class SbxjSysServiceImpl implements SbxjSysService{

	@Autowired
	private SbxjSysMapper sbxjsysMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		sbxjsysMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		sbxjsysMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		sbxjsysMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return sbxjsysMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return sbxjsysMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return sbxjsysMapper.findById(pd);
	}
	
	/**通过PROC_INST_ID_获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByPROC_INST_ID_(PageData pd)throws Exception{
		return sbxjsysMapper.findByPROC_INST_ID_(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		sbxjsysMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**根据设备编号查设备类型
	 * @param pd
	 * @throws Exception
	 */
	public PageData getTypeBySbno(PageData pd)throws Exception{
		return sbxjsysMapper.getTypeBySbno(pd);
	}
	/**设备待修状态设置
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatus(PageData pd)throws Exception{
		sbxjsysMapper.updateStatus(pd);
	}
	/**新增设备
	 * @param pd
	 * @throws Exception
	 */
	public void addSb(PageData pd)throws Exception{
		sbxjsysMapper.addSb(pd);
	}
	public List<PageData> getAllSblistPage(Page page) throws Exception {
		return sbxjsysMapper.getAllSblistPage(page);
			}	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void addQuestion(PageData pd)throws Exception{
		sbxjsysMapper.addQuestion(pd);
	}
	/**待办提醒
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getSbxjTaskCount(PageData pd) throws Exception {
		return sbxjsysMapper.getSbxjTaskCount(pd);
			}
}

