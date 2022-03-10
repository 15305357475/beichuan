package org.fh.controller.report;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.fhoa.RegisteredService;
import org.fh.service.ins.InsMediaService;
import org.fh.service.report.ReportService;
import org.fh.service.system.UsersService;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.util.wx.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @Description: 隐患举报控制器
 * @Author Qiu
 * @Date 2022/2/17 13:31
 */
@Controller
@RequestMapping("/report")
public class ReportController extends BaseController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private InsMediaService insMediaService;
    @Autowired
    private UsersService usersService;

    @Autowired
    private RegisteredService registeredService;

    /**
    * 新增
    *@Author Qiu
    *@Date 2022-02-17 13:34
    *@param
    *@return
    */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() throws Exception{

        Map<String,Object> map = new HashMap<>();
        String errInfo = "success";
        PageData pd;
        pd = this.getPageData();
        pd.put("id",this.get32UUID());
        pd.put("insert_time", DateUtil.date2Str(new Date()));
        pd.put("status", '0');
        reportService.save(pd);

        if(!pd.getString("INSMEDIA_I_PATHS").equals("NONE")){
            //图片入库
            String USERNAME = Jurisdiction.getUsername();
            String PATHS[] = pd.getString("INSMEDIA_I_PATHS").split(",");
            String NAMES[] = pd.getString("INSMEDIA_I_NAMES").split(",");
            List<String> INSMEDIA_IDs = new ArrayList<String>();
            int i = 0;
            for(String onePath: PATHS){
                PageData tempPd = new PageData();
                tempPd.put("INSMEDIA_ID", this.get32UUID());
                tempPd.put("INS_ID", pd.getString("id"));
                tempPd.put("PROC_INST_ID_", "");
                tempPd.put("NODE", "举报");
                tempPd.put("CREATE_USER", USERNAME);
                tempPd.put("MEDIA_TYPE", "image/*");
                tempPd.put("MEDIA_NAME", NAMES[i]);
                tempPd.put("MEDIA_PATH", onePath);
                tempPd.put("STATUS", "1");
                i++;
                try {
                    insMediaService.save(tempPd);
                } catch (Exception e) {
                    errInfo = "errer";
                    map.put("errorMsg", e.getMessage());
                    map.put("result", errInfo);
                    return map;
                }

            }
        }
        String openid = pd.getString("openid");
        String insert_time = pd.getString("insert_time");
        String describes = pd.getString("describes");
        String id = pd.getString("id");
        //发送消息
        WxUtil.pushOneUser(openid,insert_time,describes,id);
        map.put("result",errInfo);
        return  map;

    }

    /**
    * 列表
    *@Author Qiu
    *@Date 2022-02-17 14:27
    *@param
    *@return
    */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page page) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        String errInfo = "success";
        PageData pd = new PageData();
        pd = this.getPageData();
        //区域类型检索
        String AREA_VALUE = pd.getString("AREA_VALUE");
        if(Tools.notEmpty(AREA_VALUE)){
            pd.put("area_value",AREA_VALUE);
        }

        page.setPd(pd);
        List<PageData> list = reportService.list(page);
        map.put("varList", list);
        map.put("page", page);
        map.put("result", errInfo);
        return map;
    }

    /**
    * 获取详情
    *@Author Qiu
    *@Date 2022-02-18 09:29
    *@param
    *@return
    */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        String errInfo = "success";
        PageData pd = new PageData();
        pd = this.getPageData();
        PageData detail = reportService.getDetailById(pd);
        String openid = detail.getString("openid");
        PageData pd2 = new PageData();
        pd2.put("OPEN_ID",openid);
        PageData byOpenId = usersService.findByOpenId(pd2);
        if(detail.getString("status").equals("0")){
            reportService.edit(pd);
        }

        map.put("detail",detail);
        map.put("openid",byOpenId);
        map.put("result", errInfo);
        return map;
    }

    /**
    * 根据身份证获取用户的注册信息 因为没有单独的控制器写着这里
    *@Author Qiu
    *@Date 2022-03-08 09:55
    *@param
    *@return
    */
    @RequestMapping(value = "/getInfoByID")
    @ResponseBody
    public Object getInfoByID() throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        String errInfo = "success";
        PageData pd = new PageData();
        pd = this.getPageData();
        List<PageData> info = registeredService.findByCodeOrName(pd);
        map.put("info",info);
        map.put("result",errInfo);
        return map;
    }



}
