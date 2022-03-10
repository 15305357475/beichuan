package org.fh.service.fhoa.impl;

import java.util.ConcurrentModificationException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Mfolder;
import org.fh.mapper.dsno1.fhoa.MfolderMapper;
import org.fh.service.fhoa.MfolderService;

/** 
 * 说明： 文件管理接口实现类
 * 作者：f-sci
 * 授权：bsic
 * @version
 */
@Service
@Transactional //开启事物
public class MfolderServiceImpl implements MfolderService{

	@Autowired
	private MfolderMapper mfolderMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		mfolderMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		mfolderMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		mfolderMapper.edit(pd);
	}
	
	/**批量操作
	 * @param pd
	 * @throws Exception
	 */
	public void makeAll(PageData pd)throws Exception{
		mfolderMapper.makeAll(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return mfolderMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return mfolderMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return mfolderMapper.findById(pd);
	}

	/**
	 * 通过ID获取其子级列表
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<Mfolder> listByParentId(PageData pd) throws Exception {
		return mfolderMapper.listByParentId(pd);
	}
	
	/**
	 * 获取所有数据并填充每条数据的子级列表(递归处理)
	 * @param MENU_ID
	 * @return
	 * @throws Exception
	 */
	public List<Mfolder> listTree(PageData pd,String SHARE) throws Exception {
		List<Mfolder> valueList = this.listByParentId(pd);
		try {
			for(int n=0;n<valueList.size();n++){
				Mfolder fhentity = valueList.get(n);
				pd.put("parentId", fhentity.getMFOLDER_ID());
				fhentity.setTreeurl("0".equals(fhentity.getPARENT_ID())?("mfolder_list.html?MFOLDER_ID="+fhentity.getMFOLDER_ID()+"&SHARE="+("yes".equals(SHARE)?fhentity.getFSHARE():"")):"");
				fhentity.setSubMfolder(this.listTree(pd,SHARE));
				fhentity.setTarget("treeFrame");
			}
			
		}catch (ConcurrentModificationException e) {
		}
		return valueList;
	}
		
}

