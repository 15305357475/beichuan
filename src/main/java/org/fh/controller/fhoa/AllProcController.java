package org.fh.controller.fhoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.act.RuprocdefService;
import org.fh.service.fhoa.HotworkService;
import org.fh.service.fhoa.LargeHoistingService;
import org.fh.service.fhoa.PenTuService;
import org.fh.service.fhoa.ScaffoldingService;
import org.fh.service.system.FhsmsService;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：流程干预 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/allproc")
public class AllProcController extends AcStartController {

	@Autowired
	private HotworkService hotworkService;
	@Autowired
	private PenTuService pentuService;
	@Autowired
	private LargeHoistingService largeHoistingService;
	@Autowired
	private ScaffoldingService scaffoldingService;
	@Autowired
	private FhsmsService fhsmsService;
	@Autowired
	private RuprocdefService ruprocdefService;

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions("allproc:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		String PROC_TYPE = pd.getString("PROC_TYPE");// 流程类型
		String STATUS = pd.getString("STATUS");// 检索状态
		String ABOUTME = pd.getString("ABOUTME");// 与我相关勾选状态
		List<PageData> varList = new ArrayList<PageData>();
		PageData querypd = new PageData();
		if (Tools.notEmpty(KEYWORDS)) {
			querypd.put("keywords", KEYWORDS.trim());
		}
		if (Tools.notEmpty(STATUS)) {
			querypd.put("STATUS", STATUS.trim());
		}
		// admin用户可以看全部
		if (!"admin".equals(Jurisdiction.getUsername())) {
			// 是否勾选了仅看与我相关
			if (ABOUTME.equals("true")) {
				querypd.put("ABOUTUSER", Jurisdiction.getUsername());
			}
		}
		page.setPd(querypd);
		// 分支查询
		switch (PROC_TYPE) {
		case "hotwork":
			varList = hotworkService.list(page); // 热工作业
			break;
		case "pentu":
			varList = pentuService.list(page); // 涂装作业
			break;
		case "largeHoisting":
			varList = largeHoistingService.list(page); // 大型物件吊装
			break;
		case "scaffolding":
			varList = scaffoldingService.list(page); // 脚手架搭拆
			break;
		default:
			errInfo = "exception";
			break;
		}
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 发起干预作废流程
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/intervention")
	@RequiresPermissions("intervention")
	@ResponseBody
	public Object Intervention() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String PROC_TYPE = pd.getString("PROC_TYPE");// 流程类型
		String PROC_ID = pd.getString("PROC_ID");// 流程ID
		String REASON = Jurisdiction.getUsername() + "§" + pd.getString("REASON");// 干预原因
		PageData pd2 = new PageData();
		pd2.put("ID", PROC_ID);
		PageData pd3 = new PageData();
		pd3.put("STATUS", 2);
		// 根据具体的流程类型分支处理
		switch (PROC_TYPE) {
		case "hotwork":
			// 热工作业
			pd2 = hotworkService.findById(pd2);
			pd2.put("INTERVENTION", REASON);
			pd2.put("STATUS", 5);
			hotworkService.edit(pd2);
			// 挂起当前流程
			pd3.put("PROC_INST_ID_", pd2.getString("PROC_INST_ID_"));
			ruprocdefService.onoffTask(pd3);
			sendSms(pd2.getString("APPLYUSER"), pd2.getString("PROC_INST_ID_"), REASON, PROC_TYPE);
			break;
		case "pentu":
			// 涂装作业
			pd2 = pentuService.findById(pd2);
			pd2.put("INTERVENTION", REASON);
			pd2.put("STATUS", 5);
			pentuService.edit(pd2);
			// 挂起当前流程
			pd3.put("PROC_INST_ID_", pd2.getString("PROC_INST_ID_"));
			ruprocdefService.onoffTask(pd3);
			sendSms(pd2.getString("APPLYUSER"), pd2.getString("PROC_INST_ID_"), REASON, PROC_TYPE);
			break;
		case "largeHoisting":
			// 大型物件吊装
			pd2 = largeHoistingService.findById(pd2);
			pd2.put("INTERVENTION", REASON);
			pd2.put("STATUS", 5);
			largeHoistingService.edit(pd2);
			// 挂起当前流程
			pd3.put("PROC_INST_ID_", pd2.getString("PROC_INST_ID_"));
			ruprocdefService.onoffTask(pd3);
			sendSms(pd2.getString("APPLYUSER"), pd2.getString("PROC_INST_ID_"), REASON, PROC_TYPE);
			break;
		case "scaffolding":
			// 脚手架搭拆
			pd2 = scaffoldingService.findById(pd2);
			pd2.put("INTERVENTION", REASON);
			pd2.put("STATUS", 5);
			scaffoldingService.edit(pd2);
			// 挂起当前流程
			pd3.put("PROC_INST_ID_", pd2.getString("PROC_INST_ID_"));
			ruprocdefService.onoffTask(pd3);
			sendSms(pd2.getString("APPLYUSER"), pd2.getString("PROC_INST_ID_"), REASON, PROC_TYPE);
			break;
		default:
			errInfo = "warn";
			map.put("msg", "不能识别的流程类型");
			break;
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 发站内信通知审批结束
	 * 
	 * @param USERNAME
	 * @throws Exception
	 */
	public void sendSms(String USERNAME, String PROC_INST_ID_, String REASON, String PROC_TYPE) throws Exception {
		PageData pd = new PageData();
		pd.put("PROC_INST_ID_", PROC_INST_ID_); // 该消息绑定到的流程实例ID
		pd.put("SANME_ID", this.get32UUID()); // ID
		pd.put("SEND_TIME", DateUtil.getTime()); // 发送时间
		pd.put("FHSMS_ID", this.get32UUID()); // 主键
		pd.put("TYPE", "1"); // 类型1：收信
		pd.put("FROM_USERNAME", USERNAME); // 收信人
		pd.put("TO_USERNAME", Jurisdiction.getUsername());
		pd.put("CONTENT", "您有一个流程被干预请求作废。" + REASON + "；请点击【转到详情】按钮查看");
		pd.put("STATUS", "2");
		pd.put("LINK_TAG", PROC_TYPE);
		fhsmsService.save(pd);
	}
}
