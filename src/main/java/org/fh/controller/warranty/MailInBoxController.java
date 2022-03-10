package org.fh.controller.warranty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;

import org.fh.util.mail.MailSenderInfo;
import org.fh.util.mail.SimpleMailReceiver;
import org.fh.entity.PageData;
import org.fh.service.warranty.MailConfigService;
import org.fh.service.warranty.MailInBoxService;
import org.fh.service.warranty.WarrantyMailRelaService;
import org.fh.service.warranty.WarrantyNodeService;

/**
 * 说明：收件箱 作者：fsci 时间：2020-12-28 授权：bsic
 */
@Controller
@RequestMapping("/mailinbox")
public class MailInBoxController extends BaseController {

	@Autowired
	private MailInBoxService mailinboxService;
	@Autowired
	private MailConfigService mailconfigService;
	@Autowired
	private WarrantyMailRelaService warrantymailrelaService;
	@Autowired
	private WarrantyNodeService warrantynodeService;

	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("mailinbox:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("MAILINBOX_ID", this.get32UUID()); // 主键
		pd.put("MAIL_FK", ""); // 云端服务器的邮件ID，外键
		pd.put("FROM", ""); // 发件人
		pd.put("TO", ""); // 收件人，主送收件人
		pd.put("SUBJECT", ""); // 邮件主题
		pd.put("CONTENT", ""); // 邮件内容（最长存1000汉字*3字节=3000字节）
		pd.put("FILES", ""); // 邮件附件存放路径
		pd.put("SENT_TIME", DateUtil.date2Str(new Date())); // 云端记录的发件日期
		pd.put("REFESH_TIME", DateUtil.date2Str(new Date())); // 查新（下载到本地）的日期
		pd.put("STATE", ""); // 状态标记：1：正常；0：已删除
		pd.put("TAG", ""); // 信箱标记：发件箱SEND;收件箱IN。本表下全部为IN
		mailinboxService.save(pd);
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
	@RequiresPermissions("mailinbox:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailinboxService.findById(pd);
		pd.put("STATE", "0");
		mailinboxService.edit(pd);
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
	@RequiresPermissions("mailinbox:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		mailinboxService.edit(pd);
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
	@RequiresPermissions("mailinbox:list")
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
			pd.put("DEPT", Jurisdiction.getUSER_DEPT());
		}
		page.setPd(pd);
		List<PageData> varList = mailinboxService.list(page); // 列出MailInBox列表
		List<PageData> mailList = mailconfigService.listAll(pd);// 列出邮箱配置列表
		map.put("varList", varList);
		map.put("mailList", mailList);
		map.put("page", page);
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
	@RequiresPermissions("mailinbox:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailinboxService.findById(pd); // 根据ID读取
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
	@RequiresPermissions("mailinbox:del")
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
				temp.put("MAILINBOX_ID", ArrayDATA_IDS[i]);
				temp = mailinboxService.findById(temp);
				temp.put("STATE", "0");
				mailinboxService.edit(temp);
			}
			errInfo = "success";
		} else {
			errInfo = "error";
		}
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
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("发件人"); // 2
		titles.add("收件人"); // 3
		titles.add("邮件主题"); // 4
		titles.add("邮件内容"); // 5
		titles.add("发件日期"); // 7
		titles.add("查新日期"); // 8
		titles.add("已读人员列表"); // 9
		titles.add("状态标记：1：正常；0：已删除"); // 10
		dataMap.put("titles", titles);
		List<PageData> varOList = mailinboxService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var2", varOList.get(i).getString("FROM")); // 2
			vpd.put("var3", varOList.get(i).getString("TO")); // 3
			vpd.put("var4", varOList.get(i).getString("SUBJECT")); // 4
			vpd.put("var5", varOList.get(i).getString("CONTENT")); // 5
			vpd.put("var7", varOList.get(i).getString("SENT_TIME")); // 7
			vpd.put("var8", varOList.get(i).getString("REFESH_TIME")); // 8
			vpd.put("var9", varOList.get(i).getString("READS")); // 9
			vpd.put("var10", varOList.get(i).getString("STATE")); // 10
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 记录当前用户已读
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/markRead")
	@ResponseBody
	public Object markRead() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailinboxService.findById(pd);
		String READS_ = pd.getString("READS_");
		if (READS_ != null && !READS_.equals("")) {
			// 字符串转数组
			String[] arr = READS_.split(",");
			List<String> list = Arrays.asList(arr);
			// 判断当前用户是否已读
			if (!list.contains(Jurisdiction.getUsername())) {
				READS_ = READS_ + "," + Jurisdiction.getUsername();
			}
		} else {
			READS_ = Jurisdiction.getUsername();
		}
		pd.put("READS_", READS_);
		pd.put("STATE", "1");
		mailinboxService.edit(pd);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 检查新邮件
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getLatest")
	@ResponseBody
	public Object getLatest() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String MAIL = pd.getString("MAIL");
		String LEVEL = pd.getString("LEVEL");
		String INTERCEPT = pd.getString("INTERCEPT");// 是否检查被被拦截的邮件
		// 指定邮箱查新
		if (Tools.notEmpty(MAIL)) {
			PageData mailPd = new PageData();
			mailPd.put("ADDRESS", MAIL);
			mailPd = mailconfigService.findByAddress(mailPd);
			if (mailPd != null) {
				// 构造基本邮件登录信息:IMAP协议
				MailSenderInfo msi = new MailSenderInfo();
				msi.setUserName(mailPd.getString("ADDRESS"));
				msi.setPassword(mailPd.getString("PWD"));
				msi.setMailServerHost(mailPd.getString("IMAP"));
				msi.setMailServerPort(mailPd.getString("IMAPPORT"));
				// 查新邮件:未读邮件
				SimpleMailReceiver smr = new SimpleMailReceiver();
				List<PageData> beans = smr.StoreMailIMAPRB(msi, LEVEL);
				// 查新邮件:拦截邮件
				if (INTERCEPT.equals("CHECKED") || INTERCEPT.equals("true")) {
					beans.addAll(smr.StoreMailIMAPRB_INTERCEPT(msi, LEVEL));
				}
				// 数据入库
				if (beans != null && beans.size() > 0) {
					for (PageData pageData : beans) {
						try {
							mailinboxService.save(pageData);
						} catch (Exception e) {
							continue;
						}
					}
					// 记录总量
					map.put("count", beans.size());
				} else {
					errInfo = "warn";
				}
			}
		} else {
			// TODO: 全局查新
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 下载一封邮件
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getOneMail")
	@ResponseBody
	public Object getOneMail() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String MAILINBOX_ID = pd.getString("MAILINBOX_ID");
		String MAIL_FK = pd.getString("MAIL_FK");
		PageData mailData = mailinboxService.findById(pd);
		if (mailData != null) {
			if (mailData.getString("ISDOWN").equals("0")) {
				PageData mailPd = new PageData();
				if (!mailData.getString("TO_").split(",")[0].equals(pd.getString("TAGMAIL"))
						&& !pd.getString("TAGMAIL").equals("N/A")) {
					mailPd.put("ADDRESS", pd.getString("TAGMAIL"));
				} else {
					mailPd.put("ADDRESS", mailData.getString("TO_").split(",")[0]);
				}
				mailPd = mailconfigService.findByAddress(mailPd);
				if (mailPd != null) {
					// 构造基本邮件登录信息:IMAP协议
					MailSenderInfo msi = new MailSenderInfo();
					msi.setUserName(mailPd.getString("ADDRESS"));
					msi.setPassword(mailPd.getString("PWD"));
					msi.setMailServerHost(mailPd.getString("IMAP"));
					msi.setMailServerPort(mailPd.getString("IMAPPORT"));
					// 正常收件箱邮件还是垃圾邮件
					String INTERCEPT = mailData.getString("INTERCEPT");
					// 下载邮件
					SimpleMailReceiver smr = new SimpleMailReceiver();
					PageData result = smr.DownLoadOneMail(msi, Long.parseLong(MAIL_FK), MAILINBOX_ID, INTERCEPT);
					// 下载数据存档入库
					if (result != null && result.size() > 0) {
						if (!result.getString("CONTENT").equals("null")) {
							if(result.getString("CONTENT").length() > 900) {
								mailData.put("CONTENT", "文本过长，请下载附件查看");
							}else {
								mailData.put("CONTENT", result.getString("CONTENT"));
							}
						}
						if (!result.getString("HTML").equals("null")) {
							mailData.put("HTML", result.getString("HTML"));
							System.out.println("HTML：" + result.getString("HTML"));
						}
						if (!result.getString("FILES").equals("null")) {
							mailData.put("FILES", result.getString("FILES"));
						}
						if (!result.getString("IMG").equals("null")) {
							mailData.put("IMG", result.getString("IMG"));
						}
						if (!result.getString("TEXTFILE").equals("null")) {
							mailData.put("TEXTFILE", result.getString("TEXTFILE"));
						}
					}
					mailData.put("READS_", "");
					mailData.put("ISDOWN", "1");
					// 数据入库
					mailinboxService.edit(mailData);
				}
			}
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 链入当前邮件到流程
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/linkProc")
	@ResponseBody
	public Object linkProc() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		String msg = "";
		PageData pd = new PageData();
		pd = this.getPageData();
		boolean stepFlag = true;
		// 链入邮件
		String WARRANTYNODE_ID = this.get32UUID();
		if (!pd.getString("LINK_MAIL").equals("N/A") && stepFlag) {
			stepFlag = false;
			PageData mailPd = new PageData();
			mailPd.put("WARRANTYMAILRELA_ID", this.get32UUID());
			mailPd.put("MAIL_ID", pd.getString("LINK_MAIL"));
			mailPd.put("MAIL_TAG", pd.getString("MAIL_TAG"));
			mailPd.put("WARRANTY_ID", pd.getString("WARRANTY_ID"));
			mailPd.put("STATUS", "1");
			mailPd.put("LINK_DATE", DateUtil.date2Str(new Date()));
			mailPd.put("LINK_TAG", "link");// 链入邮件
			mailPd.put("LINK_NODE_ID", WARRANTYNODE_ID);// 当前邮件所处的节点ID
			try {
				warrantymailrelaService.save(mailPd);
				stepFlag = true;
			} catch (Exception e) {
				errInfo = "error";
				msg = "[链入异常：" + e.getMessage() + "]";
			}
		}
		// 设置节点
		if (stepFlag) {
			stepFlag = false;
			PageData nodePd = new PageData();
			nodePd.put("WARRANTYNODE_ID", WARRANTYNODE_ID);
			nodePd.put("WARRANTY_ID", pd.getString("WARRANTY_ID"));
			nodePd.put("NODE_NAME", "关联邮件");
			nodePd.put("NODE_ITEM", pd.getString("NODE"));
			nodePd.put("NODE_DATE", DateUtil.date2Str(new Date()));
			nodePd.put("NODE_USER", Jurisdiction.getUsername());
			nodePd.put("SUP", pd.getString("SUP"));
			nodePd.put("STATUS", "1");
			try {
				warrantynodeService.save(nodePd);
				stepFlag = true;
			} catch (Exception e) {
				errInfo = "error";
				msg = "[节点异常：" + e.getMessage() + "]";
			}
		}
		// 返回结果
		map.put("result", errInfo);
		map.put("msg", msg);
		return map;
	}
}
