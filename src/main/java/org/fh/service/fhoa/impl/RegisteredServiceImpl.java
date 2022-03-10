package org.fh.service.fhoa.impl;

import java.util.List;

import org.fh.entity.PageData;
import org.fh.entity.fhoa.Registered;
import org.fh.mapper.dsno1.fhoa.RegisteredMapper;
import org.fh.service.fhoa.RegisteredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 说明： 注册白名单 创建人：f-sci 授权：bsic
 */
@Service(value = "RegisteredService")
@Transactional // 开启事物
public class RegisteredServiceImpl implements RegisteredService {

	@Autowired
	private RegisteredMapper RegisteredMapper;

	/**
	 * 新增
	 * 
	 * @param registered
	 * @throws Exception
	 */
	public void save(Registered registered) throws Exception {
		RegisteredMapper.save(registered);
	}

	/**
	 * 通过id获取数据
	 * 
	 * @param card
	 * @throws Exception
	 */
	public PageData findById(String card) throws Exception {
		return (PageData) RegisteredMapper.findById(card);
	}

	/**
	 * 通过身份证、姓名或工号查询数据
	 *
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByCodeOrName(PageData pd) throws Exception {
		return RegisteredMapper.findByCodeOrName(pd);
	}

	/**
	 * 获取全部数据
	 * 
	 * @throws Exception
	 */
	public List<Registered> getAll() throws Exception {
		return RegisteredMapper.getAll();
	}

	/**
	 * 更新状态
	 * 
	 * @param register
	 * @throws Exception
	 */
	public void updateStatus(Registered register) throws Exception {
		RegisteredMapper.updateStatus(register);
	}

	/**
	 * 通过身份证号批量修改状态为0
	 * 
	 * @param USER_IDS
	 * @throws Exception
	 */
	public void updateStatusByCardIdTo0(String[] USER_IDS) throws Exception {
		RegisteredMapper.updateStatusByCardIdTo0(USER_IDS);
	}

	/**
	 * 通过身份证号批量修改状态为1
	 * 
	 * @param USER_IDS
	 * @throws Exception
	 */
	public void updateStatusByCardIdTo1(String[] USER_IDS) throws Exception {
		RegisteredMapper.updateStatusByCardIdTo1(USER_IDS);
	}
	
	/**通过身份证号删除
	 * @param CARD
	 * @throws Exception
	 */
	public void delete(String CARD)throws Exception{
		RegisteredMapper.delete(CARD);
	}
	
	/**
	 * 通过身份证号更新白名单基础数据
	 * 
	 * @param register
	 * @throws Exception
	 */
	public void updateListInfo(Registered register) throws Exception {
		RegisteredMapper.updateListInfo(register);
	}

	/**
	 * 通过身份证号更新照片下载状态
	 *
	 * @param register
	 * @throws Exception
	 */
	public void updateALREADY_DOWN(Registered register) throws Exception {
		RegisteredMapper.updateALREADY_DOWN(register);
	}

	/**
	 * 随机选择一个数据
	 *
	 * @param pd
	 * @throws Exception
	 */
	public PageData shake(PageData pd) throws Exception {
		return (PageData) RegisteredMapper.shake(pd);
	}
}
