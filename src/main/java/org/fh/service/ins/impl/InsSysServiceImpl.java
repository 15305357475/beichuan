package org.fh.service.ins.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.ins.InsSysMapper;
import org.fh.service.ins.InsSysService;

/** 
 * 说明： 隐患排查主表接口实现类
 * 作者：f-sci
 * 时间：2021-04-13
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class InsSysServiceImpl implements InsSysService{

	@Autowired
	private InsSysMapper inssysMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		inssysMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		inssysMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		inssysMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return inssysMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return inssysMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return inssysMapper.findById(pd);
	}
	
	/**通过PROC_INST_ID_获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByPROC_INST_ID_(PageData pd)throws Exception{
		return inssysMapper.findByPROC_INST_ID_(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		inssysMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**
	 * 未进行数据摆渡的数据源
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listDataForFerry(PageData pd) throws Exception {
		return inssysMapper.listDataForFerry(pd);
	}
	
	/**
	 * 统计与导出 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> DataStatisticslist(Page page) throws Exception {
		return inssysMapper.DataStatisticslistPage(page);
	}
	
	/**
	 * 统计与导出 -- 不分页
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> DataStatisticsExport(PageData pd) throws Exception {
		return inssysMapper.DataStatisticsExport(pd);
	}
	
	/**
	 * 部门隐患分类汇总 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> DeptInsSubtotals(PageData pd) throws Exception {
		return inssysMapper.DeptInsSubtotals(pd);
	}
	
	/**
	 * 批量将状态更新为已摆渡 
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void UpdateSync(String[] ArrayDATA_IDS) throws Exception {
		inssysMapper.UpdateSync(ArrayDATA_IDS);
	}
}

