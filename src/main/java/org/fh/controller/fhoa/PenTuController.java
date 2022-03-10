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
import org.fh.service.fhoa.PenTuService;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：喷涂作业申请 作者：X-sci 师傅：F-sci 授权：bsic
 */
@Controller
@RequestMapping("/pentu")
public class PenTuController extends AcStartController {
	@Autowired
	private PenTuService pentuService;

	/**
	 * 保存喷涂作业单
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("recruit:add")
	@ResponseBody
	public Object add() {
		Map<String, Object> zmap = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// pd.put("MYLEAVE_ID", this.get32UUID()); // 主键
		pd.put("APPLYUSER", Jurisdiction.getUsername()); // 发起人，当前系统用户
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		pd.put("COMMITTIME", formatter.format(date));// 发起时间
		pd.put("STATUS", "1");
		try {
			/** 工作流的操作 **/
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			String USERNAME = Jurisdiction.getUsername();
			map.put("喷涂部位", pd.getString("SPRAYPART"));
			map.put("挥发性油漆", pd.getString("OIL1"));
			map.put("非挥发性油漆", pd.getString("OIL2"));
			map.put("其他类油漆", pd.getString("OIL3"));
			map.put("施工单位", pd.getString("WORKZOON"));
			map.put("操作方式", pd.getString("OPERATE"));
			map.put("是否为有限空间作业", pd.getString("HAVESPACE"));
			map.put("防爆风机台数、型号", pd.getString("DRAUGHT"));
			map.put("风机监护人", pd.getString("FGUARDIAN"));
			if (pd.getString("HAVESPACE").equals("是")) {
				map.put("进舱人数", pd.getString("OPERATOR"));
			} else {
				map.put("喷漆操作工", pd.getString("OPERATOR"));
			}
			map.put("开始时间", pd.getString("STARTTIME").replace("T", " ") + ":00");
			map.put("结束时间", pd.getString("ENDTIME").replace("T", " ") + ":00");
			map.put("现场负责人（承包商）", pd.getString("PRINCIPAL"));
			map.put("安全监护人（承包商）", pd.getString("AGUARDIAN"));
			map.put("施工承包单位安全员", pd.getString("SAFETY"));
			map.put("CHECKPOINT", pd.getString("CHECKPOINT"));
			map.put("提请日期", formatter.format(date));
			map.put("USERNAME", USERNAME); // 指派代理人为当前用户
			String fk = null;
			fk = startProcessInstanceByKeyHasVariables("PenTu", map, USERNAME);
			zmap.put("PROC_INST_ID_", fk);
			pd.put("PROC_INST_ID_", fk);
			pentuService.save(pd); // 记录存入数据库
			zmap.put("ASSIGNEE_", USERNAME); // 用于给待办人发送新任务消息
		} catch (Exception e) {
			errInfo = "errer:" + e;
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
	@RequiresPermissions("recruit:list")
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
		List<PageData> varList = pentuService.list(page); // 列出pentu列表
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
	@RequiresPermissions("recruit:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 根据主键查出数据
		pd = pentuService.findById(pd);
		// 变更为删除状态
		pd.put("STATUS", "0");
		// 写入数据库
		pentuService.edit(pd);
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
	@RequiresPermissions("recruit:del")
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
				now = pentuService.findById(now);
				now.put("STATUS", "0");
				pentuService.edit(now);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

}
