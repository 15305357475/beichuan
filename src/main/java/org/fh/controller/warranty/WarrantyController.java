package org.fh.controller.warranty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.FileUpload;
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.util.exportUtil.DataFerryObj;
import org.flowable.engine.RuntimeService;
import org.fh.entity.PageData;
import org.fh.service.warranty.WarrantyMailRelaService;
import org.fh.service.warranty.WarrantyNodeService;
import org.fh.service.warranty.WarrantyService;

/**
 * 说明：保修流程实例 作者：fsci 时间：2021-01-08 授权：bsic
 */
@Controller
@RequestMapping("/warranty")
public class WarrantyController extends AcStartController {

	@Autowired
	private WarrantyService warrantyService;

	@Autowired
	private WarrantyMailRelaService warrantymailrelaService;

	@Autowired
	private WarrantyNodeService warrantynodeService;

	@Autowired
	private RuntimeService runtimeService; // 与正在执行的流程实例和执行对象相关的Service

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("warranty:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		String msg = "";
		PageData pd = new PageData();
		pd = this.getPageData();
		boolean stepFlag = false;
		// 设置流程变量,启动流程引擎
		String PROC_INST_ID_ = "";
		try {
			/** 设置流程变量 **/
			Map<String, Object> flowmap = new LinkedHashMap<String, Object>();
			String USERNAME = Jurisdiction.getUsername();
			flowmap.put("项目号", pd.getString("PROJ"));
			flowmap.put("保修单号", pd.getString("WARRANTY_NO"));
			flowmap.put("发起日期", pd.getString("COMMINT_DATE"));
			flowmap.put("隶属系统", pd.getString("SYSTEM"));
			flowmap.put("问题原因", pd.getString("REASON"));
			flowmap.put("责任部门", pd.getString("RESPONSIBLE"));
			flowmap.put("问题描述", pd.getString("DETIAL"));
			flowmap.put("备注", pd.getString("MARK"));
			if (!pd.getString("FILEPATH").equals("")) {
				flowmap.put("附件", pd.getString("FILEPATH"));
			}
			flowmap.put("提请日期", DateUtil.getTime());
			flowmap.put("USERNAME", USERNAME); // 指派代理人为当前用户
			/** 启动流程引擎 **/
			PROC_INST_ID_ = startProcessInstanceByKeyHasVariables("warranty", flowmap, USERNAME);// 启动流程实例通过KEY
			map.put("ASSIGNEE_", USERNAME); // 用于给待办人发送新任务消息
			stepFlag = true;
		} catch (Exception e) {
			errInfo = "error";
			msg = "[引擎异常：" + e.getMessage() + "]保修流程启动失败";
		}

		// 数据写入伴随表
		String warrantyUUID = this.get32UUID();
		if (stepFlag) {
			stepFlag = false;
			pd.put("WARRANTY_ID", warrantyUUID); // 主键
			pd.put("PROC_INST_ID_", PROC_INST_ID_); // 流程引擎实例ID，外键
			pd.put("STATUS", "1"); // 流程状态：0：删除；1：正在审批；2：中途驳回；3：已批准；4：作废；5：即将被迫作废
			pd.put("CREATE_DATE", DateUtil.date2Str(new Date())); // 保单流程启动日期
			pd.put("COMMIT_USER", Jurisdiction.getUsername());// 流程发起人
			try {
				warrantyService.save(pd);
				stepFlag = true;
			} catch (Exception e) {
				errInfo = "error";
				msg = "[伴随表异常：" + e.getMessage() + "]保修流程启动失败";
			}
		}

		String WARRANTYNODE_ID = this.get32UUID();
		// 链入邮件
		if (!pd.getString("LINK_MAIL").equals("N/A") && stepFlag) {
			stepFlag = false;
			PageData mailPd = new PageData();
			mailPd.put("WARRANTYMAILRELA_ID", this.get32UUID());
			mailPd.put("MAIL_ID", pd.getString("LINK_MAIL"));
			mailPd.put("MAIL_TAG", pd.getString("MAIL_TAG"));
			mailPd.put("WARRANTY_ID", warrantyUUID);
			mailPd.put("STATUS", "1");
			mailPd.put("LINK_DATE", DateUtil.date2Str(new Date()));
			mailPd.put("LINK_TAG", "main");// 主发起邮件
			mailPd.put("LINK_NODE_ID", WARRANTYNODE_ID);// 当前邮件所处的节点ID
			try {
				warrantymailrelaService.save(mailPd);
				stepFlag = true;
			} catch (Exception e) {
				errInfo = "error";
				msg = "[链入异常：" + e.getMessage() + "]保修流程启动失败";
			}
		}

		// 设置初始节点
		if (stepFlag) {
			stepFlag = false;
			PageData nodePd = new PageData();
			nodePd.put("WARRANTYNODE_ID", WARRANTYNODE_ID);
			nodePd.put("WARRANTY_ID", warrantyUUID);
			nodePd.put("NODE_NAME", "启动");
			nodePd.put("NODE_ITEM", "启动保修流程");
			nodePd.put("NODE_DATE", DateUtil.date2Str(new Date()));
			nodePd.put("NODE_USER", Jurisdiction.getUsername());
			nodePd.put("STATUS", "1");
			try {
				warrantynodeService.save(nodePd);
				stepFlag = true;
			} catch (Exception e) {
				errInfo = "error";
				msg = "[节点异常：" + e.getMessage() + "]保修流程启动失败";
			}
		}

		// 返回结果
		map.put("result", errInfo);
		map.put("msg", msg);
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("warranty:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = warrantyService.findById(pd);
		pd.put("STATUS", "0");
		// 删除流程
		try {
			warrantyService.edit(pd);
		} catch (Exception e) {
			errInfo = "error";
		}
		// 作废流程实例
		String PROC_INST_ID_ = pd.getString("PROC_INST_ID_");
		try { // 作废流程实例
			runtimeService.deleteProcessInstance(PROC_INST_ID_, Jurisdiction.getUsername() + "删除了流程");
		} catch (Exception e) {// 流程已经被作废了
			// errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	@RequiresPermissions("warranty:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if (pd.getString("tag").equals("a")) {
			PageData temp = new PageData();
			temp = warrantyService.findById(pd);
			temp.put("ARCHIVE_DATE", pd.getString("ARCHIVE_DATE"));
			temp.put("ARCHIVE_MARK", pd.getString("ARCHIVE_MARK"));
			temp.put("STATUS", "5");
			warrantyService.edit(temp);
		} else {
			warrantyService.edit(pd);
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions("warranty:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS)) {
			pd.put("KEYWORDS", KEYWORDS.trim());
		}
		pd.put("COMMIT_USER", Jurisdiction.getUsername());
		page.setPd(pd);
		List<PageData> varList = warrantyService.list(page); // 列出Warranty列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 列表--正在处理的流程
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/listOnRun")
	@RequiresPermissions("warranty:list")
	@ResponseBody
	public Object listOnRun(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData> varList = warrantyService.listOnRun(page); // 列出Warranty列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 去修改页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	@RequiresPermissions("warranty:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = warrantyService.findById(pd); // 根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	@RequiresPermissions("toExcel")
	public ModelAndView exportExcel() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("COMMIT_USER", Jurisdiction.getUsername());
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("序号"); // 1
		titles.add("船号"); // 2
		titles.add("保单号"); // 3
		titles.add("保单日期"); // 4
		titles.add("接受日期"); // 5
		titles.add("保单内容"); // 6
		titles.add("隶属系统"); // 7
		titles.add("原因分类"); // 8
		titles.add("责任部门"); // 9
		titles.add("处理流程"); // 10
		titles.add("封闭日期"); // 11
		titles.add("备注"); // 12
		titles.add("确认与归档日期"); // 13
		titles.add("确认与归档批注"); // 14
		titles.add("流程创建人"); // 15
		dataMap.put("titles", titles);
		List<PageData> varOList = warrantyService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", Integer.toString(i + 1)); // 1
			vpd.put("var2", varOList.get(i).getString("PROJ")); // 2
			vpd.put("var3", varOList.get(i).getString("WARRANTY_NO")); // 3
			vpd.put("var4", varOList.get(i).getString("COMMINT_DATE")); // 4
			vpd.put("var5", varOList.get(i).getString("CREATE_DATE")); // 5
			vpd.put("var6", varOList.get(i).getString("DETIAL")); // 6
			vpd.put("var7", varOList.get(i).getString("SYSTEM")); // 7
			vpd.put("var8", varOList.get(i).getString("REASON")); // 8
			vpd.put("var9", varOList.get(i).getString("RESPONSIBLE")); // 9
			// 处理处理流程
			String warrantyID = varOList.get(i).getString("WARRANTY_ID");
			PageData temp = new PageData();
			temp.put("WARRANTY_ID", warrantyID);
			List<PageData> node = warrantyService.ListNodeMailByWarrantyId(temp);
			if (node.size() > 0) {
				String nodeList = "";
				int number = 1;
				for (PageData pageData : node) {
					nodeList = nodeList + Integer.toString(number) + "、" + pageData.getString("LINK_DATE") + "  "
							+ pageData.getString("NODE_ITEM") + ";" + String.valueOf((char) 10);
					number++;
				}
				vpd.put("var10", nodeList);
			} else {
				vpd.put("var10", "暂无处理流程");
			}
			// 处理封闭日期
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				vpd.put("var11", df.format(df.parse(varOList.get(i).get("CLOSE_DATE").toString()))); // 11
			} catch (Exception e) {
				vpd.put("var11", "");
			}
			vpd.put("var12", varOList.get(i).getString("MARK")); // 12
			vpd.put("var13", varOList.get(i).getString("ARCHIVE_DATE")); // 13
			vpd.put("var14", varOList.get(i).getString("ARCHIVE_MARK")); // 14
			vpd.put("var15", varOList.get(i).getString("COMMIT_USER")); // 15
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 上传保修单附件
	 * 报修流程审批过程中产生的审批附件
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadFile")
	@ResponseBody
	public Object uploadAll(@RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		// 获取当前年
		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		String fileName = "";
		String OriginalFilename = "";
		String filePath = "";
		if (null != file && !file.isEmpty()) {
			filePath = PathUtil.getProjectpath() + Const.WARRANTY_FILES + year + "//" + month + "//"; // 文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
			OriginalFilename = file.getOriginalFilename();
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		map.put("path", Const.WARRANTY_FILES + year + "//" + month + "//" + fileName); // 返回文件路径
		map.put("fileName", fileName);// UUID文件名 + 文件路径
		map.put("OriginalFilename", OriginalFilename);// 初始文件名
		return map;
	}

	/**
	 * 获取节点邮件数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getnodeMailList")
	@ResponseBody
	public Object getnodeMailList() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> nodeMailList = warrantyService.ListNodeMail(pd); // 列出节点邮件数据
		PageData temp = warrantyService.findByPROC_INST_ID_(pd);
		if (temp != null) {
			map.put("WARRANTY_ID", temp.getString("WARRANTY_ID"));
		}
		map.put("nodeMailList", nodeMailList);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 数据摆渡
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/DataFerry")
	@RequiresPermissions("DataFerry")
	public ModelAndView DataFerry() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		List<String> WARRANTY_IDS = new ArrayList<String>();
		// 构造Excel
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("NO"); // 0
		titles.add("WARRANTY_ID"); // 1
		titles.add("PROC_INST_ID_"); // 2
		titles.add("STATUS"); // 3
		titles.add("PROJ"); // 4
		titles.add("WARRANTY_NO"); // 5
		titles.add("COMMINT_DATE"); // 6
		titles.add("CREATE_DATE"); // 7
		titles.add("DETIAL"); // 8
		titles.add("SYSTEM"); // 9
		titles.add("REASON"); // 10
		titles.add("RESPONSIBLE"); // 11
		titles.add("CLOSE_DATE"); // 12
		titles.add("MARK"); // 13
		titles.add("COMMIT_USER"); // 14
		titles.add("ARCHIVE_DATE"); // 15
		titles.add("ARCHIVE_MARK"); // 16
		titles.add("SYNC"); // 17
		titles.add("SYNC_TIME"); // 18
		dataMap.put("titles", titles);
		List<PageData> varOList = warrantyService.listDataForFerry(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			WARRANTY_IDS.add(varOList.get(i).getString("WARRANTY_ID"));
			PageData vpd = new PageData();
			vpd.put("var1", Integer.toString(i + 1)); // 0
			vpd.put("var2", varOList.get(i).getString("WARRANTY_ID")); // 1
			vpd.put("var3", varOList.get(i).getString("PROC_INST_ID_")); // 2
			vpd.put("var4", varOList.get(i).getString("STATUS")); // 3
			vpd.put("var5", varOList.get(i).getString("PROJ")); // 4
			vpd.put("var6", varOList.get(i).getString("WARRANTY_NO")); // 5
			vpd.put("var7", varOList.get(i).getString("COMMINT_DATE")); // 6
			vpd.put("var8", varOList.get(i).getString("CREATE_DATE")); // 7
			vpd.put("var9", varOList.get(i).getString("DETIAL")); // 8
			vpd.put("var10", varOList.get(i).getString("SYSTEM")); // 9
			vpd.put("var11", varOList.get(i).getString("REASON")); // 10
			vpd.put("var12", varOList.get(i).getString("RESPONSIBLE")); // 11
			// 处理封闭日期
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				vpd.put("var13", df.format(df.parse(varOList.get(i).get("CLOSE_DATE").toString()))); // 12
			} catch (Exception e) {
				vpd.put("var13", "");
			}
			vpd.put("var14", varOList.get(i).getString("MARK"));// 13
			vpd.put("var15", varOList.get(i).getString("COMMIT_USER")); // 14
			vpd.put("var16", varOList.get(i).getString("ARCHIVE_DATE")); // 15
			vpd.put("var17", varOList.get(i).getString("ARCHIVE_MARK")); // 16
			vpd.put("var18", varOList.get(i).getString("SYNC")); // 17
			// 处理摆渡日期
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				vpd.put("var19", df.format(df.parse(varOList.get(i).get("SYNC_TIME").toString()))); // 18
			} catch (Exception e) {
				vpd.put("var19", "");
			}
			varList.add(vpd);
		}
		// 更新状态
		String ids = String.join(",", WARRANTY_IDS);
		String idsArray[] = ids.split(",");
		warrantyService.UpdateSync(idsArray);
		// 返回Excel实例
		dataMap.put("varList", varList);
		DataFerryObj erv = new DataFerryObj();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}
}
