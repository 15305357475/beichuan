package org.fh.controller.fhoa;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.fhoa.ScaffoldingService;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：热工作业申请 作者：陈 * 授权：内码* 师父：外码
 */
@Controller
@RequestMapping("/Scaffolding")
public class ScaffoldingController extends AcStartController {

	@Autowired
	private ScaffoldingService scaffoldingService;

	/**
	 * 保存脚手架作业单
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("toformal:add")
	@ResponseBody
	public Object add() {
		Map<String, Object> zmap = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// pd.put("MYLEAVE_ID", this.get32UUID()); // 主键
		pd.put("STATUS", "1");// 发起状态
		pd.put("APPLYUSER", Jurisdiction.getUsername()); // 发起人，当前系统用户
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		pd.put("COMMIT_TIME", formatter.format(date));// 发起时间
		try {
			/** 工作流的操作 **/
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			String USERNAME = Jurisdiction.getUsername();
			map.put("提交人", USERNAME); // 当前用户的姓名
			map.put("施工单位", pd.getString("WORK_ZOON"));
			map.put("搭设单位", pd.getString("ERECTION_ZOON"));
			map.put("报验时间", pd.getString("INSPECTION_TIME").replace("T", " ") + ":00");
			map.put("用途", pd.getString("USEFOR"));
			map.put("船号", pd.getString("PROJ_NO"));
			map.put("区域", pd.getString("ERECTION_LOCATION"));
			map.put("分段号", pd.getString("SUBSECTION"));
			map.put("CHECKPOINT", pd.getString("CHECK_POINT"));
			map.put("备注", pd.getString("MARK"));
			map.put("提请日期", formatter.format(date));
			map.put("USERNAME", USERNAME); // 指派代理人为当前用户
			String fk = startProcessInstanceByKeyHasVariables("Scaffolding", map, USERNAME);
			zmap.put("PROC_INST_ID_", fk);
			pd.put("PROC_INST_ID_", fk); // 当前启动的流程实例主键
			scaffoldingService.save(pd); // 记录存入数据库
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
	@RequiresPermissions("toformal:list")
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
		List<PageData> varList = scaffoldingService.list(page); // 列出hotwork列表
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
	@RequiresPermissions("toformal:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 根据主键查出数据
		pd = scaffoldingService.findById(pd);
		// 变更为删除状态
		pd.put("STATUS", "0");
		// 写入数据库
		scaffoldingService.edit(pd);
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
	@RequiresPermissions("toformal:del")
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
				now = scaffoldingService.findById(now);
				now.put("STATUS", "0");
				scaffoldingService.edit(now);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

}
