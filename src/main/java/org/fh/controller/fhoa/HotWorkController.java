package org.fh.controller.fhoa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.fhoa.HotworkService;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：热工作业申请 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/hotwork")
public class HotWorkController extends AcStartController {

	@Autowired
	private HotworkService hotworkService;

	/**
	 * 保存热工作业单
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("reimbursement:add")
	@ResponseBody
	public Object add() {
		Map<String, Object> zmap = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("APPLYUSER", Jurisdiction.getUsername()); // 发起人，当前系统用户
		pd.put("STATUS", "1");// 发起状态
		pd.put("COMMITTIME", DateUtil.getTime());// 发起时间
		try {
			/** 工作流的操作 **/
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			String USERNAME = Jurisdiction.getUsername();
			map.put("作业区", pd.getString("WORKZOON"));
			map.put("施工区域", pd.getString("AREA"));
			map.put("施工单位", pd.getString("CONTRACTOR"));
			map.put("作业等级", pd.getString("LEVEL"));
			map.put("项目号", pd.getString("PROJ"));
			map.put("分段号", pd.getString("SEGMENT"));
			map.put("看火人", pd.getString("WATCHFIRE"));
			map.put("实际施工人员", pd.getString("OPERATORS"));
			map.put("施工区域和内容", pd.getString("DETIAL"));
			map.put("开始时间", pd.getString("STARTTIME").replace("T", " ") + ":00");
			map.put("结束时间", pd.getString("ENDTIME").replace("T", " ") + ":00");
			map.put("CHECKPOINT", pd.getString("CHECKPOINT"));
			if (!pd.getString("REMARKS").equals("")) {
				map.put("其它注意事项", pd.getString("REMARKS"));
			}
			map.put("提请日期", DateUtil.getTime());
			map.put("USERNAME", USERNAME); // 指派代理人为当前用户
			String fk = null;
			if (pd.getString("LEVEL").equals("2")) {
				fk = startProcessInstanceByKeyHasVariables("HotWorkL2", map, USERNAME);// 启动流程实例通过KEY
			} else if (pd.getString("LEVEL").equals("3")) {
				fk = startProcessInstanceByKeyHasVariables("HotWorkL3", map, USERNAME);// 启动流程实例通过KEY
			}
			zmap.put("PROC_INST_ID_", fk);
			pd.put("PROC_INST_ID_", fk); // 当前启动的流程实例主键作为 oa_hotwork 表外键
			hotworkService.save(pd); // 记录存入数据库
			zmap.put("ASSIGNEE_", USERNAME); // 用于给待办人发送新任务消息
		} catch (Exception e) {
			errInfo = "errer";
		}
		zmap.put("result", errInfo); // 返回结果
		return zmap;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions("reimbursement:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS)) {
			pd.put("keywords", KEYWORDS.trim());
		}
		// 除admin用户外，只能查看自己的数据
		if (!"admin".equals(Jurisdiction.getUsername())) {
			pd.put("APPLYUSER", Jurisdiction.getUsername());
		}
		page.setPd(pd);
		List<PageData> varList = hotworkService.list(page); // 列出hotwork列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("reimbursement:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 根据主键查出数据
		pd = hotworkService.findById(pd);
		// 变更为删除状态
		pd.put("STATUS", "0");
		// 写入数据库
		hotworkService.edit(pd);
		map.put("result", "success");
		return map;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@RequiresPermissions("reimbursement:del")
	@ResponseBody
	public Object deleteAll() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (Tools.notEmpty(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			for (int i = 0; i < ArrayDATA_IDS.length; i++) {
				PageData now = new PageData();
				now.put("ID", ArrayDATA_IDS[i]);
				now = hotworkService.findById(now);
				now.put("STATUS", "0");
				hotworkService.edit(now);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

}
