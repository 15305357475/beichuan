package org.fh.service.fastpass.impl;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.fastpass.FastPassMediaMapper;
import org.fh.service.fastpass.FastPassMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明：出门证服务接口实现类 创建人：f-sci 授权：bsic
 */
@Service(value = "FastPassMediaServiceImpl")
@Transactional // 开启事物
public class FastPassMediaServiceImpl implements FastPassMediaService {

	@Autowired
	private FastPassMediaMapper FastPassMediaMapper;

	/**
	 * 新增
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		FastPassMediaMapper.save(pd);
	}


	/**
	 * 修改
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		FastPassMediaMapper.edit(pd);
	}

	

	/**
	 * 通过id获取数据
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findById(PageData pd) throws Exception {
		return FastPassMediaMapper.findById(pd);
	}

	

}
