package org.fh.service.report.impl;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.report.ReportMapper;
import org.fh.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 隐患举报实现类
 * @Author Qiu
 * @Date 2022/2/17 11:16
 */
@Service(value="reportServiceImpl")
@Transactional //开启事物
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public void save(PageData pd) throws Exception {
        reportMapper.save(pd);
    }

    @Override
    public List<PageData> list(Page page) throws Exception {
        return reportMapper.datalistPage(page);
    }

    @Override
    public PageData getDetailById(PageData pd) throws Exception {
        return reportMapper.getDetailById(pd);
    }

    @Override
    public void edit(PageData pd) throws Exception {
        reportMapper.edit(pd);
    }
}
