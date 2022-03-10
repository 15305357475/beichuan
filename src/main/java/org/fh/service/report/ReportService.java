package org.fh.service.report;

import org.fh.entity.Page;
import org.fh.entity.PageData;

import java.util.List;

/**
 * @Description: 隐患举报接口
 * @Author Qiu
 * @Date 2022/2/17 11:13
 */
public interface ReportService {

    /**
    * 新增
    *@Author Qiu
    *@Date 2022-02-17 11:14
    *@param pd
    *@return
    *@throws Exception
    */
    void save(PageData pd)throws Exception;

    /**
    * 列表
    *@Author Qiu
    *@Date 2022-02-17 14:44
    *@param
    *@return
    */
    List<PageData> list(Page page)throws Exception;

    /**
    * 根据ID获取详情
    *@Author Qiu
    *@Date 2022-02-18 09:36
    *@param pd
    *@throws Exception
    *@return
    */
    PageData getDetailById(PageData pd)throws Exception;

    /**
    * 根据ID 修改状态
    *@Author Qiu
    *@Date 2022-02-18 10:21
    *@param pd
    *@throws Exception
    *@return
    */
    void edit(PageData pd)throws Exception;
}
