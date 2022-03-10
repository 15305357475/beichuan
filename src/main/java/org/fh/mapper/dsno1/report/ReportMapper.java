package org.fh.mapper.dsno1.report;

import org.fh.entity.Page;
import org.fh.entity.PageData;

import java.util.List;

/**
 * @Description: 隐患举报Mapper
 * @Author Qiu
 * @Date 2022/2/17 11:10
 */
public interface ReportMapper {

    /**
    * 新增
    *@Author Qiu
    *@Date 2022-02-17 11:10
    *@param pd
    *@return
    */
    void save(PageData pd);

    /**
    * 获取列表
    *@Author Qiu
    *@Date 2022-02-17 14:28
    *@param page
    *@return
    */
    List<PageData> datalistPage(Page page);

    /**
    * 根据ID获取详情
    *@Author Qiu
    *@Date 2022-02-18 09:32
    *@param pd
    *@return
    */
    PageData getDetailById(PageData pd);

    /**
    * 根据ID修改状态
    *@Author Qiu
    *@Date 2022-02-18 10:18
    *@param
    *@return
    */
    void edit(PageData pd);

}
