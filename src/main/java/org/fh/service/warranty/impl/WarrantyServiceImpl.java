package org.fh.service.warranty.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.warranty.WarrantyMapper;
import org.fh.service.warranty.WarrantyService;

/**
 * 说明： 保修流程实例接口实现类 作者：f-sci 时间：2021-01-08 授权：bsic
 * 
 * @version
 */
@Service
@Transactional // 开启事物
public class WarrantyServiceImpl implements WarrantyService {

	@Autowired
	private WarrantyMapper warrantyMapper;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		warrantyMapper.save(pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception {
		warrantyMapper.delete(pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		warrantyMapper.edit(pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page) throws Exception {
		return warrantyMapper.datalistPage(page);
	}

	/**
	 * 列表--正在处理的流程
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> listOnRun(Page page) throws Exception {
		return warrantyMapper.datalistPageOnRun(page);
	}

	/**
	 * 列表(全部)
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd) throws Exception {
		return warrantyMapper.listAll(pd);
	}

	/**
	 * 获取节点邮件数据
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> ListNodeMail(PageData pd) throws Exception {
		return warrantyMapper.ListNodeMail(pd);
	}

	/**
	 * 根据保单号获取节点邮件数据
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> ListNodeMailByWarrantyId(PageData pd) throws Exception {
		return warrantyMapper.ListNodeMailByWarrantyId(pd);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception {
		return warrantyMapper.findById(pd);
	}

	/**
	 * 通过流程实例id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByPROC_INST_ID_(PageData pd) throws Exception {
		return warrantyMapper.findByPROC_INST_ID_(pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		warrantyMapper.deleteAll(ArrayDATA_IDS);
	}

	/**
	 * 未进行数据摆渡的数据源
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listDataForFerry(PageData pd) throws Exception {
		return warrantyMapper.listDataForFerry(pd);
	}
	
	/**
	 * 批量将状态更新为已摆渡 
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void UpdateSync(String[] ArrayDATA_IDS) throws Exception {
		warrantyMapper.UpdateSync(ArrayDATA_IDS);
	}
}
