package org.fh.service.sbxj;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;

import com.fasterxml.jackson.annotation.JsonFormat;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
public interface sbxjQueryService{
	/**
	 * 统计与导出 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> DataStatisticslist(Page page) throws Exception ;
 
	/**
	 * 统计与导出 --不分页
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> DataStatisticsExport(PageData pd) throws Exception;
	/**
	 * 
	 * 获取图片路径
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> getImgByID(PageData pd) throws Exception;
	
	/**
	 * 
	 * 获取单个设备列表
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> listBySbNolistPage(Page page) throws Exception;
	/**
	 * 根据XJ_ID查列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> findById(Page page) throws Exception;
	/**
	 * 根据USER_ID查列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> findByUserId(Page page) throws Exception;
	/**
	 * 
	 * 获取单个设备列表
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> listBySbNo(PageData pd) throws Exception;
	/**
	 * 
	 * 查询问题列表
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> getAllQu(PageData pd) throws Exception;
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void OverQst(PageData pd)throws Exception;
	/**
	 * 问题清单
	 * 
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> QuestionlistPage(Page page) throws Exception ;
	/**
	 * 
	 * 查询问题列表
	 * @param pd
	 * @throws Exception
	 */
	
	public List<PageData> getAllQuBySbNo(PageData pd) throws Exception;
	public void DelQst(PageData pd)throws Exception;
	public List<PageData> exportXjMain(PageData pd) throws Exception;
	public List<PageData> exportXjCpAll(PageData pd) throws Exception;
	public List<PageData> exportXjCpSelect(PageData pd) throws Exception;
	public List<PageData> exportXjQuestion(PageData pd) throws Exception;
}