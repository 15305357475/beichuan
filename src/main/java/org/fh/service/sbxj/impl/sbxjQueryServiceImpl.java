package org.fh.service.sbxj.impl;

import java.util.List;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.sbxj.sbxjQueryMapper;
import org.fh.service.sbxj.sbxjQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.annotation.JsonFormat;

@Service
@Transactional //开启事物
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
public class sbxjQueryServiceImpl implements sbxjQueryService{

	@Autowired
	private sbxjQueryMapper sbxjqueryMapper;


public List<PageData> DataStatisticslist(Page page) throws Exception {
	return sbxjqueryMapper.DataStatisticslistPage(page);
	}

public List<PageData> DataStatisticsExport(PageData pd) throws Exception {
	return sbxjqueryMapper.DataStatisticsExport(pd);
	
}

public List<PageData> getImgByID(PageData pd) throws Exception {
	return sbxjqueryMapper.getImgByID(pd);
	
}
public List<PageData> listBySbNolistPage(Page page) throws Exception {
	return sbxjqueryMapper.listBySbNolistPage(page);
		}	
public List<PageData> findById(Page page) throws Exception {
	return sbxjqueryMapper.findById(page);
		}	
public List<PageData> findByUserId(Page page) throws Exception {
	return sbxjqueryMapper.findByUserId(page);
		}
public List<PageData> listBySbNo(PageData pd) throws Exception {
	return sbxjqueryMapper.listBySbNo(pd);
		}
public List<PageData> getAllQu(PageData pd) throws Exception {
	return sbxjqueryMapper.getAllQu(pd);
		}
/**修改
 * @param pd
 * @throws Exception
 */
public void OverQst(PageData pd)throws Exception{
	sbxjqueryMapper.OverQst(pd);
}
public List<PageData> QuestionlistPage(Page page) throws Exception {
	return sbxjqueryMapper.QuestionlistPage(page);
	}
public List<PageData> getAllQuBySbNo(PageData pd) throws Exception {
	return sbxjqueryMapper.getAllQuBySbNo(pd);
		}
public void DelQst(PageData pd)throws Exception{
	sbxjqueryMapper.DelQst(pd);
}
public List<PageData> exportXjMain(PageData pd) throws Exception {
	return sbxjqueryMapper.exportXjMain(pd);
	
}
public List<PageData> exportXjCpAll(PageData pd) throws Exception {
	return sbxjqueryMapper.exportXjCpAll(pd);
	
}
public List<PageData> exportXjCpSelect(PageData pd) throws Exception {
	return sbxjqueryMapper.exportXjCpSelect(pd);
	
}
public List<PageData> exportXjQuestion(PageData pd) throws Exception {
	return sbxjqueryMapper.exportXjQuestion(pd);
	
}
}