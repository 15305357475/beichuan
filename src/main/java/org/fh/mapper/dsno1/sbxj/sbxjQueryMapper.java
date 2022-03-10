package org.fh.mapper.dsno1.sbxj;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import com.fasterxml.jackson.annotation.JsonFormat;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
public interface sbxjQueryMapper{
	/**
	 * 统计与导出 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> DataStatisticslistPage(Page page) ;

	/**统计与导出--不分页
	* @param pd
	* @throws Exception
	*/
	List<PageData> DataStatisticsExport(PageData pd);
	
	/**获取图片路径
	* @param pd
	* @throws Exception
	*/
	List<PageData> getImgByID(PageData pd);
	/**
	 * 根据设备号查列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> listBySbNolistPage(Page page) ;
	 /**
	 * 根据XJ_ID查列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> findById(Page page) ;
	 /**
	 * 根据USER_ID查列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> findByUserId(Page page) ;
	 /**
	   * 根据设备号查列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> listBySbNo(PageData pd) ;
	 /**
	   * 查询问题列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> getAllQu(PageData pd) ;
	 /**封闭问题
	 * @param pd
	 * @throws Exception
	 */
	 void OverQst(PageData pd);
	 /**删除问题
		 * @param pd
		 * @throws Exception
		 */
		 void DelQst(PageData pd);
	 /**
	 * 问题清单 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> QuestionlistPage(Page page) ;
	 /**
	   * 查询问题列表 
	 * 
	 * @param pd
	 * @throws Exception
	 */
	 List<PageData> getAllQuBySbNo(PageData pd) ;
	 List<PageData> exportXjMain(PageData pd);
	 List<PageData> exportXjCpAll(PageData pd);
	 List<PageData> exportXjCpSelect(PageData pd);
	 List<PageData> exportXjQuestion(PageData pd);
}