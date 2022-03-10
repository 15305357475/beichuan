package org.fh.controller.fhoa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.fhoa.LargeHoistingService;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.FileUpload;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 说明：大型物件吊装申请 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/LargeHoisting")
public class LargeHoistingController extends AcStartController {

	@Autowired
	private LargeHoistingService LargeHoistingService;

	/**
	 * 保存吊装作业单
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("purchase:add")
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
			map.put("申请人", USERNAME); // 当前用户的姓名
			map.put("施工项目", pd.getString("PROJECTNAME"));
			map.put("项目负责人", pd.getString("PROJECTLEADER"));
			map.put("施工单位", pd.getString("CONSTRUCTION"));
			map.put("确认地点", pd.getString("JOBLOCATION"));
			map.put("确认日期", pd.getString("ENTERTIME"));
			map.put("确认具体时间", pd.getString("TENTERTIME"));
			map.put("CHECKPOINT", pd.getString("CHECKPOINT"));
			map.put("问题及整改意见", pd.getString("PROBLEM"));
			map.put("提请日期", DateUtil.getTime());
			map.put("USERNAME", USERNAME); // 指派代理人为当前用户
			if (!pd.getString("FILE_PATH").toString().equals("NONE")) {
				map.put("FILE_PATH", pd.getString("FILE_PATH"));
			}
			String fk = startProcessInstanceByKeyHasVariables("LargeHoisting", map, USERNAME);
			zmap.put("PROC_INST_ID_", fk);
			pd.put("PROC_INST_ID_", fk); 
			LargeHoistingService.save(pd); // 记录存入数据库
			zmap.put("ASSIGNEE_", USERNAME); // 用于给待办人发送新任务消息
		} catch (Exception e) {
			errInfo = "errer" + e;
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
	@RequiresPermissions("purchase:list")
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
		List<PageData> varList = LargeHoistingService.list(page); // 列出hotwork列表
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
	@RequiresPermissions("purchase:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		// 根据主键查出数据
		pd = LargeHoistingService.findById(pd);
		// 变更为删除状态
		pd.put("STATUS", "0");
		// 写入数据库
		LargeHoistingService.edit(pd);
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
	@RequiresPermissions("purchase:del")
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
				now = LargeHoistingService.findById(now);
				now.put("STATUS", "0");
				LargeHoistingService.edit(now);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 上传探伤报告附件
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadFile")
	@ResponseBody
	public Object uploadAll(@RequestParam(value = "pdf", required = false) MultipartFile file) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		// 获取当前年
		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		String fileName = "";
		String OriginalFilename = "";
		String filePath = "";
		if (null != file && !file.isEmpty()) {
			filePath = PathUtil.getProjectpath() + Const.TSBG_FILEPATHFILE + year + "//" + month + "//"; // 文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
			OriginalFilename = file.getOriginalFilename();
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		map.put("path", Const.TSBG_FILEPATHFILE + year + "//" + month + "//" + fileName); // 返回文件路径
		// map.put("path", filePath + fileName);
		map.put("fileName", fileName);// 唯一文件名 + 文件路径
		map.put("OriginalFilename", OriginalFilename);// 初始文件名
		return map;
	}
}
