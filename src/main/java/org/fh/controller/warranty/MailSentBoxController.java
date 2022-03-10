package org.fh.controller.warranty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.apache.shiro.session.Session;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.FileUpload;
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.util.mail.MailSenderInfo;
import org.fh.util.mail.SimpleMailSender;
import org.fh.entity.PageData;
import org.fh.entity.system.User;
import org.fh.service.system.UeditorService;
import org.fh.service.warranty.MailConfigService;
import org.fh.service.warranty.MailSentBoxService;
import org.fh.service.warranty.WarrantyMailRelaService;
import org.fh.service.warranty.WarrantyNodeService;

/**
 * 说明：保修管理模块邮件发件箱 作者：fsci 时间：2021-01-04 授权：bsic
 */
@Controller
@RequestMapping("/mailsentbox")
public class MailSentBoxController extends BaseController {

	@Autowired
	private MailSentBoxService mailsentboxService;
	@Autowired
	private MailConfigService mailconfigService;
	@Autowired
	private UeditorService ueditorService;
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
	@RequiresPermissions("mailsentbox:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("MAILSENTBOX_ID", this.get32UUID()); // 主键
		pd.put("SENT_TIME", DateUtil.date2Str(new Date())); // 发件日期
		pd.put("STATE", ""); // 状态:1:正常；0：删除
		pd.put("TAG", ""); // 信箱标记，本表为：SENT
		mailsentboxService.save(pd);
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
	@RequiresPermissions("mailsentbox:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailsentboxService.findById(pd);
		pd.put("STATE", "0");
		mailsentboxService.edit(pd);
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
	@RequiresPermissions("mailsentbox:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		mailsentboxService.edit(pd);
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
	@RequiresPermissions("mailsentbox:list")
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
		if(!Jurisdiction.getUsername().equals("admin")) {
			pd.put("DEPT", Jurisdiction.getUSER_DEPT());
		}
		page.setPd(pd);
		List<PageData> varList = mailsentboxService.list(page); // 列出MailSentBox列表
		List<PageData> mailList = mailconfigService.listAll(pd);// 列出邮箱配置列表
		map.put("varList", varList);
		map.put("mailList", mailList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 预览邮件接口
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goView")
	@RequiresPermissions("mailsentbox:edit")
	@ResponseBody
	public Object goView() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = mailsentboxService.findById(pd); // 根据ID读取
		// 把获取的该条ID的富文本内容设置到sys_ueditor表
		String Ueditor = pd.getString("CONTENT");
		PageData Ue = new PageData();
		Session session = Jurisdiction.getSession();
		User user = (User) session.getAttribute(Const.SESSION_USER);
		Ue.put("UEDITOR_ID", this.get32UUID()); // 主键
		Ue.put("USER_ID", user.getUSER_ID()); // 用户ID
		Ue.put("USERNAME", user.getUSERNAME()); // 用户名
		Ue.put("CONTENT", Ueditor);
		Ue.put("CONTENT2", Ueditor);
		Ue.put("TYPE", "email");
		ueditorService.edit(Ue);
		// 把查询到的当前邮件的基本数据返回
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
	@RequiresPermissions("mailsentbox:del")
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
				temp.put("MAILSENTBOX_ID", ArrayDATA_IDS[i]);
				temp = mailsentboxService.findById(temp);
				temp.put("STATE", "0");
				mailsentboxService.edit(temp);
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
		titles.add("发件人"); // 1
		titles.add("主送人"); // 2
		titles.add("抄送人"); // 3
		titles.add("密送人"); // 4
		titles.add("邮件主题"); // 5
		titles.add("发件日期"); // 7
		titles.add("状态:1:正常；0：删除"); // 8
		dataMap.put("titles", titles);
		List<PageData> varOList = mailsentboxService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FROM_")); // 1
			vpd.put("var2", varOList.get(i).getString("TO_")); // 2
			vpd.put("var3", varOList.get(i).getString("CC")); // 3
			vpd.put("var4", varOList.get(i).getString("BCC")); // 4
			vpd.put("var5", varOList.get(i).getString("SUBJECT")); // 5
			vpd.put("var7", varOList.get(i).getString("SENT_TIME")); // 7
			vpd.put("var8", varOList.get(i).getString("STATE")); // 8
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 发送电子邮件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sendEmail")
	@ResponseBody
	public Object sendEmail() throws Exception {
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> map = new HashMap<String, Object>();
		MailSenderInfo msi = new MailSenderInfo();
		String errInfo = "success"; // 发送状态
		String msg = "NONE";
		int count = 0; // 统计发送成功条数
		int zcount = 0; // 理论条数
		PageData mailboxProtocol = new PageData();
		mailboxProtocol.put("ADDRESS", pd.getString("FROM_"));
		mailboxProtocol = mailconfigService.findByAddress(mailboxProtocol);
		if (mailboxProtocol.size() > 2) {
			// 构造基本邮件登录信息:SMTP协议
			msi.setUserName(mailboxProtocol.getString("ADDRESS"));
			msi.setPassword(mailboxProtocol.getString("PWD"));
			msi.setMailServerHost(mailboxProtocol.getString("SMTP"));
			msi.setMailServerPort(mailboxProtocol.getString("SMTPPORT"));
			msi.setValidate(true);
			// 设置邮件内容
			String FROM_ = pd.getString("FROM_");
			String TO_ = pd.getString("TO_"); // 主送人
			String CC = pd.getString("CC"); // 抄送
			String SUBJECT = pd.getString("SUBJECT"); // 标题
			String CONTENT = pd.getString("CONTENT"); // 内容
			String FILESPATH = pd.getString("FILESPATH");// 附件路径
			String FILESNAME = pd.getString("FILESNAME");// 附件名称
			String REPLYID = pd.getString("REPLYID");// 原始邮件ID
			// String TYPE = pd.getString("TYPE"); // 类型
			msi.setFromAddress(FROM_);
			msi.setToAddress(TO_);
			msi.setSubject(SUBJECT);
			msi.setContent(CONTENT);
			// 取到每一个抄送人
			CC = CC.replaceAll("；", ";");
			CC = CC.replaceAll(" ", "");
			String[] arrTITLE = CC.split(";");
			msi.setCcAddress(CC);
			// 处理附件
			msi.setAttachFilePath(FILESPATH);
			msi.setAttachFileNames(FILESNAME);
			// 构造bean
			PageData bean = new PageData();
			bean.put("MAILSENTBOX_ID", this.get32UUID());
			bean.put("FROM_", FROM_);
			bean.put("TO_", TO_);
			bean.put("CC", CC);
			bean.put("BCC", null);
			bean.put("SUBJECT", SUBJECT);
			bean.put("CONTENT", CONTENT);
			bean.put("SENT_TIME", DateUtil.date2Str(new Date()));
			bean.put("STATE", "1");
			bean.put("TAG", "SENT");
			bean.put("FILEPATH", FILESPATH);
			bean.put("FILENAME", FILESNAME);
			bean.put("REPLYID", REPLYID);
			try {
				zcount = arrTITLE.length + 1;// n个抄送人+1个主送人
				SimpleMailSender.sendFileMail(msi);// 尝试发送邮件
				errInfo = "success";
				count = zcount;
				// 数据入库
				mailsentboxService.save(bean);
				// 是否关联邮件
				// 绑定到流程
				boolean stepFlag = true;
				String WARRANTYNODE_ID = this.get32UUID();
				if (pd.getString("LINK").equals("1") && stepFlag) {
					stepFlag = false;
					msg = "N/A";
					PageData mailPd = new PageData();
					mailPd.put("WARRANTYMAILRELA_ID", this.get32UUID());
					mailPd.put("MAIL_ID", bean.getString("MAILSENTBOX_ID"));
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
						// errInfo = "error";
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
					nodePd.put("STATUS", "1");
					nodePd.put("SUP", pd.getString("SUP"));
					try {
						warrantynodeService.save(nodePd);
						stepFlag = true;
					} catch (Exception e) {
						// errInfo = "error";
						msg = "[节点异常：" + e.getMessage() + "]";
					}
				}
			} catch (Exception e) {
				errInfo = "error：" + e.getMessage();
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo);
		map.put("msg", msg);
		map.put("count", count); // 成功数
		map.put("ecount", zcount - count); // 失败数
		return map;
	}

	/**
	 * 上传邮件附件
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
			filePath = PathUtil.getProjectpath() + Const.MAIL_FILES_SENT + year + "//" + month + "//"; // 文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
			OriginalFilename = file.getOriginalFilename();
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		map.put("path", Const.MAIL_FILES_SENT + year + "//" + month + "//" + fileName); // 返回文件路径
		map.put("fileName", fileName);// UUID文件名 + 文件路径
		map.put("OriginalFilename", OriginalFilename);// 初始文件名
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
