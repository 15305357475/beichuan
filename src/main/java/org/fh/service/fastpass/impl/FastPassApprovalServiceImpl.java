package org.fh.service.fastpass.impl;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fastpass.FastPassApprovalMapper;
import org.fh.service.fastpass.FastPassApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明：出门证服务接口实现类 创建人：f-sci 授权：bsic
 */
@Service(value = "FastPassApprovalServiceImpl")
@Transactional // 开启事物
public class FastPassApprovalServiceImpl implements FastPassApprovalService {

	@Autowired
	private FastPassApprovalMapper FastPassApprovalMapper;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		FastPassApprovalMapper.save(pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception {
		FastPassApprovalMapper.delete(pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		FastPassApprovalMapper.edit(pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page) throws Exception {
		return FastPassApprovalMapper.datalistPage(page);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception {
		return FastPassApprovalMapper.findById(pd);
	}
	
	/**
	 * 通过process_id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByProcessId(PageData pd) throws Exception {
		return FastPassApprovalMapper.findByProcessId(pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		FastPassApprovalMapper.deleteAll(ArrayDATA_IDS);
	}

}
