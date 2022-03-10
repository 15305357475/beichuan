package org.fh.controller.warranty;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.warranty.MailConfigService;

/**
 * 说明：邮箱基本配置 作者：fsci 时间：2020-12-28 授权：bsic
 */
@Controller
@RequestMapping("/mailconfig")
public class MailConfigController extends BaseController {

	@Autowired
	private MailConfigService mailconfigService;

	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("mailconfig:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 检查该地址是否已经配置
		PageData pd2 = new PageData();
		pd2 = mailconfigService.findByAddress(pd);
		if (pd2 != null) {
			errInfo = "当前账户已经存在";
		} else {
			pd.put("MAILCONFIG_ID", this.get32UUID()); // 主键
			pd.put("DEPT", Jurisdiction.getUSER_DEPT()); // 当前邮箱隶属部门，配置人所在部门
			pd.put("CONFIG_USER", Jurisdiction.getUsername()); // 当前记录配置人
			pd.put("CONFIG_DATE", DateUtil.date2Str(new Date())); // 当前记录配置日期
			pd.put("STATE", "1"); // 状态：1：有效；0：删除
			mailconfigService.save(pd);
		}
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("mailconfig:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailconfigService.findById(pd);
		pd.put("STATE", "0");
		pd.put("CONFIG_USER", Jurisdiction.getUsername()); // 当前记录配置人
		pd.put("CONFIG_DATE", DateUtil.date2Str(new Date())); // 当前记录配置日期
		mailconfigService.edit(pd);
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
	@RequiresPermissions("mailconfig:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("STATE", "1");
		pd.put("DEPT", Jurisdiction.getUSER_DEPT()); // 当前邮箱隶属部门，配置人所在部门
		pd.put("CONFIG_USER", Jurisdiction.getUsername());
		pd.put("CONFIG_DATE", DateUtil.date2Str(new Date()));
		mailconfigService.edit(pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions("mailconfig:list")
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
		if (!Jurisdiction.getUsername().equals("admin")) {
			pd.put("DEPT", Jurisdiction.getUSER_DEPT()); // 当前邮箱隶属部门，配置人所在部门
		}
		page.setPd(pd);
		List<PageData> varList = mailconfigService.list(page); // 列出MailConfig列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 列表--下拉框用
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/maillist")
	@ResponseBody
	public Object mailList() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if (!Jurisdiction.getUsername().equals("admin")) {
			pd.put("DEPT", Jurisdiction.getUSER_DEPT()); // 当前邮箱隶属部门，配置人所在部门
		}
		List<PageData> mailList = mailconfigService.listAll(pd);// 列出邮箱配置列表
		map.put("mailList", mailList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去修改页面获取数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	@RequiresPermissions("mailconfig:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailconfigService.findById(pd); // 根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@RequiresPermissions("mailconfig:del")
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
				PageData temp = new PageData();
				temp.put("MAILCONFIG_ID", ArrayDATA_IDS[i]);
				temp = mailconfigService.findById(temp);
				temp.put("STATE", "0");
				temp.put("CONFIG_USER", Jurisdiction.getUsername()); // 当前记录配置人
				temp.put("CONFIG_DATE", DateUtil.date2Str(new Date())); // 当前记录配置日期
				mailconfigService.edit(temp);
			}
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

}
