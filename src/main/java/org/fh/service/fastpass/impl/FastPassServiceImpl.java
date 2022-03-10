package org.fh.service.fastpass.impl;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fastpass.FastPassMapper;
import org.fh.service.fastpass.FastPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明：出门证服务接口实现类 创建人：f-sci 授权：bsic
 */
@Service(value = "FastPassServiceImpl")
@Transactional // 开启事物
public class FastPassServiceImpl implements FastPassService {

	@Autowired
	private FastPassMapper FastPassMapper;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		FastPassMapper.save(pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception {
		FastPassMapper.delete(pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		FastPassMapper.edit(pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page) throws Exception {
		return FastPassMapper.datalistPage(page);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception {
		return FastPassMapper.findById(pd);
	}
	
	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByCode(PageData pd) throws Exception {
		return FastPassMapper.findByCode(pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		FastPassMapper.deleteAll(ArrayDATA_IDS);
	}

}
