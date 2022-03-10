package org.fh.service.fhoa.impl;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fhoa.LargeHoistingMapper;
import org.fh.service.fhoa.LargeHoistingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明：大型物件吊装服务接口实现类 创建人：f-sci 授权：bsic
 */
@Service(value = "LargeHoistingServiceImpl")
@Transactional // 开启事物
public class LargeHoistingServiceImpl implements LargeHoistingService {

	@Autowired
	private LargeHoistingMapper LargeHoistingMapper;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		LargeHoistingMapper.save(pd);
	}

	/**
	 * 删除
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception {
		LargeHoistingMapper.delete(pd);
	}

	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		LargeHoistingMapper.edit(pd);
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page) throws Exception {
		return LargeHoistingMapper.datalistPage(page);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception {
		return LargeHoistingMapper.findById(pd);
	}

	/**
	 * 批量删除
	 * 
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		LargeHoistingMapper.deleteAll(ArrayDATA_IDS);
	}

}
