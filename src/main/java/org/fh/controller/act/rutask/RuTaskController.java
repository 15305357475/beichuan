package org.fh.controller.act.rutask;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.fh.controller.act.AcBusinessController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.act.HiprocdefService;
import org.fh.service.act.LivePhotoService;
import org.fh.service.act.RuprocdefService;
import org.fh.service.system.FhsmsService;
import org.fh.service.fhoa.AccompanyingTableService;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.ImageAnd64Binary;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：待办任务 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping(value = "/rutask")
public class RuTaskController extends AcBusinessController {

	@Autowired
	private RuprocdefService ruprocdefService;

	@Autowired
	private FhsmsService fhsmsService;

	@Autowired
	private HiprocdefService hiprocdefService;

	@Autowired
	private AccompanyingTableService accompanyingTableService;

	@Autowired
	private LivePhotoService livephotoService;

	/**
	 * 待办任务列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions("rutask:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		String PROC_INST_ID_ = pd.getString("PROC_INST_ID_");
		if (Tools.notEmpty(KEYWORDS)) {
			pd.put("keywords", KEYWORDS.trim());
		}
		String STRARTTIME = pd.getString("STRARTTIME"); // 开始时间
		String ENDTIME = pd.getString("ENDTIME"); // 结束时间
		if (Tools.notEmpty(STRARTTIME)) {
			pd.put("lastStart", STRARTTIME + " 00:00:00");
		}
		if (Tools.notEmpty(ENDTIME)) {
			pd.put("lastEnd", ENDTIME + " 00:00:00");
		}
		if (PROC_INST_ID_.equals("") || PROC_INST_ID_.equals("null")) {
			pd.put("USERNAME", Jurisdiction.getUsername()); // 查询当前用户的任务(用户名查询)
			pd.put("RNUMBERS", Jurisdiction.getRnumbers()); // 查询当前用户的任务(角色编码查询)
		}
		page.setPd(pd);
		List<PageData> varList = ruprocdefService.list(page); // 列出Rutask列表
		for (int i = 0; i < varList.size(); i++) {
			varList.get(i).put("INITATOR", getInitiator(varList.get(i).getString("PROC_INST_ID_")));// 流程申请人
		}
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 刚提交的申请任务
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getFrist")
	@ResponseBody
	public Object getFrist(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		// 改进后按流程实例PROC_INST_ID_精准查询以提高效率
		//pd.put("USERNAME", Jurisdiction.getUsername()); // 查询当前用户的任务(用户名查询)
		//pd.put("RNUMBERS", Jurisdiction.getRnumbers()); // 查询当前用户的任务(角色编码查询)
		page.setPd(pd);
		List<PageData> varList = ruprocdefService.list(page); // 列出Rutask列表
		for (int i = 0; i < varList.size(); i++) {
			varList.get(i).put("INITATOR", getInitiator(varList.get(i).getString("PROC_INST_ID_")));// 流程申请人
		}
		map.put("PROC_INST_ID_", varList.get(0).getString("PROC_INST_ID_"));
		map.put("ID_", varList.get(0).getString("ID_"));
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 待办任务列表(只显示5条,用于后台顶部小铃铛左边显示)
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getList")
	@ResponseBody
	public Object getList(Page page) throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd.put("USERNAME", Jurisdiction.getUsername()); // 查询当前用户的任务(用户名查询)
		pd.put("RNUMBERS", Jurisdiction.getRnumbers()); // 查询当前用户的任务(角色编码查询)
		page.setPd(pd);
		page.setShowCount(5);
		List<PageData> varList = ruprocdefService.list(page); // 列出Rutask列表
		List<PageData> pdList = new ArrayList<PageData>();
		for (int i = 0; i < varList.size(); i++) {
			PageData tpd = new PageData();
			tpd.put("NAME_", varList.get(i).getString("NAME_")); // 任务名称
			tpd.put("PNAME_", varList.get(i).getString("PNAME_")); // 流程名称
			pdList.add(tpd);
		}
		map.put("list", pdList);
		map.put("taskCount", page.getTotalResult());
		return map;
	}

	/**
	 * 待办任务数量
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTaskCount")
	@ResponseBody
	public Object getTaskCount(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("USERNAME", Jurisdiction.getUsername()); // 查询当前用户的任务(用户名查询)
		pd.put("RNUMBERS", Jurisdiction.getRnumbers()); // 查询当前用户的任务(角色编码查询)
		page.setPd(pd);
		page.setShowCount(5);
		ruprocdefService.list(page);
		map.put("taskCount", page.getTotalResult());
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去办理任务页面获取数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getHandleData")
	@ResponseBody
	public Object getHandleData() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varList = ruprocdefService.varList(pd); // 列出流程变量列表
		List<PageData> hitaskList = ruprocdefService.hiTaskList(pd); // 历史任务节点列表
		for (int i = 0; i < hitaskList.size(); i++) { // 根据耗时的毫秒数计算天时分秒
			if (null != hitaskList.get(i).get("DURATION_")) {
				Long ztime = Long.parseLong(hitaskList.get(i).get("DURATION_").toString());
				Long tian = ztime / (1000 * 60 * 60 * 24);
				Long shi = (ztime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
				Long fen = (ztime % (1000 * 60 * 60 * 24)) % (1000 * 60 * 60) / (1000 * 60);
				Long miao = (ztime % (1000 * 60 * 60 * 24)) % (1000 * 60 * 60) % (1000 * 60) / 1000;
				hitaskList.get(i).put("ZTIME", tian + "天" + shi + "时" + fen + "分" + miao + "秒");
			}
		}
		String FILENAME = URLDecoder.decode(pd.getString("FILENAME"), "UTF-8");
		createXmlAndPngAtNowTask(pd.getString("PROC_INST_ID_"), FILENAME, "RU");// 生成当前任务节点的流程图片
		String imgSrcPath = PathUtil.getProjectpath() + Const.FILEACTIVITI + FILENAME;
		map.put("imgSrc", "data:image/jpeg;base64," + ImageAnd64Binary.getImageStr(imgSrcPath)); // 解决图片src中文乱码，把图片转成base64格式显示(这样就不用修改tomcat的配置了)
		map.put("varList", varList);
		map.put("hitaskList", hitaskList);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 办理任务
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/handle")
	@RequiresPermissions("rutask:edit")
	@ResponseBody
	public Object handle() throws Exception {
		Map<String, Object> zmap = new HashMap<String, Object>();
		String errInfo = "success";
		Session session = Jurisdiction.getSession();
		PageData pd = new PageData();
		pd = this.getPageData();
		String taskId = pd.getString("ID_"); // 任务ID
		String PROC_INST_ID_ = pd.getString("PROC_INST_ID_");// 流程实例ID
		String sfrom = "";
		Object ofrom = getVariablesByTaskIdAsMap(taskId, "审批结果");
		if (null != ofrom) {
			sfrom = ofrom.toString();
		}
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String OPINION = sfrom + Jurisdiction.getName() + ",fh," + pd.getString("OPINION");// 审批人的姓名+审批意见
		String msg = pd.getString("msg");
		String LIVEPHOTO = "null";
		try {
			LIVEPHOTO = pd.getString("LIVEPHOTO");
		} catch (Exception e) {
			LIVEPHOTO = "null";
		}
		if (!LIVEPHOTO.equals("null")) {
			// 记录现场照
			PageData photoBean = new PageData();
			photoBean.put("LIVEPHOTO_ID", this.get32UUID());
			photoBean.put("PROC_INST_ID_", PROC_INST_ID_);
			photoBean.put("NODE", "");
			photoBean.put("PTOTO_TIME", DateUtil.date2Str(new Date()));
			photoBean.put("PTOTO_PATH", LIVEPHOTO);
			photoBean.put("PTOTO_USER", Jurisdiction.getUsername());
			photoBean.put("ISOK", msg);
			try {
				livephotoService.save(photoBean);
			} catch (Exception e) {
				System.out.println("记录现场图失败：" + e.getMessage());
			}
		}
		// 获取伴随表表名
		PageData pd3 = new PageData();
		pd3.put("PROC_INST_ID_", PROC_INST_ID_);
		pd3 = ruprocdefService.getTableNameById(pd3);
		if ("yes".equals(msg)) { // 批准
			map.put("审批结果", "【批准】" + OPINION); // 审批结果
			setVariablesByTaskIdAsMap(taskId, map); // 设置流程变量
			setVariablesByTaskId(taskId, "RESULT", "批准");
			completeMyPersonalTask(taskId);
			// 状态回写至伴随表
			pd3.put("STATUS", "1");
			updateStatusToAccompanyingTable(pd3);
		} else { // 驳回
			map.put("审批结果", "【驳回】" + OPINION); // 审批结果
			setVariablesByTaskIdAsMap(taskId, map); // 设置流程变量
			setVariablesByTaskId(taskId, "RESULT", "驳回");
			completeMyPersonalTask(taskId);
			trySendSms(zmap, pd, "turndown");
			// 状态回写至伴随表
			pd3.put("STATUS", "2");
			updateStatusToAccompanyingTable(pd3);
		}
		try {
			removeVariablesByPROC_INST_ID_(PROC_INST_ID_, "RESULT"); // 移除流程变量(从正在运行中)
		} catch (Exception e) {
			/* 此流程变量在历史中 **/
		}
		try {
			String ASSIGNEE_ = pd.getString("ASSIGNEE_"); // 下一待办对象
			if (Tools.notEmpty(ASSIGNEE_)) {
				setAssignee(session.getAttribute("TASKID").toString(), ASSIGNEE_); // 指定下一任务待办对象
			} else {
				Object os = session.getAttribute("YAssignee");
				if (null != os && !"".equals(os.toString())) {
					ASSIGNEE_ = os.toString(); // 没有指定就是默认流程的待办人
				} else {
					// 获取该流程的历史经办人列表
					PageData pd2 = new PageData();
					pd2.put("PROC_INST_ID_", PROC_INST_ID_);
					trySendSms(zmap, pd2, "finish"); // 没有任务监听时，默认流程结束，发送站内信给任务发起人
					// 状态回写至伴随表
					pd3.put("STATUS", "3");
					updateStatusToAccompanyingTable(pd3);
				}
			}
			zmap.put("ASSIGNEE_", ASSIGNEE_); // 用于给待办人发送新任务消息
		} catch (Exception e) {
			zmap.put("ASSIGNEE_", "null");
			/*
			 * 手动指定下一待办人，才会触发此异常。 任务结束不需要指定下一步办理人了,发送站内信通知任务发起人
			 **/
			// 获取该流程的历史经办人列表
			PageData pd2 = new PageData();
			pd2.put("PROC_INST_ID_", PROC_INST_ID_);
			trySendSms(zmap, pd2, "finish");
			// 状态回写至伴随表
			pd3.put("STATUS", "3");
			updateStatusToAccompanyingTable(pd3);
		}
		if (Tools.notEmpty(pd.getString("COPYTOER"))) {
			List<PageData> varList = ruprocdefService.varList(pd); // 列出流程变量列表
			this.sendSms(pd.getString("COPYTOER"), pd.getString("CREMARKS"), varList, map.get("审批结果").toString()); // 发站内信给抄送对象
		}
		zmap.put("result", errInfo); // 返回结果
		return zmap;
	}

	/**
	 * 办理任务--验证封闭,保修管理专用
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/handleBX")
	@RequiresPermissions("rutask:edit")
	@ResponseBody
	public Object handleBX() throws Exception {
		Map<String, Object> zmap = new HashMap<String, Object>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String errInfo = "success";
		Session session = Jurisdiction.getSession();
		PageData pd = new PageData();
		pd = this.getPageData();
		String taskId = pd.getString("ID_"); // 任务ID
		String PROC_INST_ID_ = pd.getString("PROC_INST_ID_");// 流程实例ID
		// 审批流程附件
		String SP_FILE_PATH = pd.getString("SP_FILE_PATH");
		// String SP_FILE_NAME = pd.getString("PROC_INST_ID_");
		if (!SP_FILE_PATH.equals("N/A")) {
			map.put("审批附件路径", "[FILE]" + SP_FILE_PATH); // 审批文件路径
			// map.put("审批附件文件名","[FILE]" + SP_FILE_NAME); // 审批文件名称
		}
		String sfrom = "";
		Object ofrom = getVariablesByTaskIdAsMap(taskId, "审批结果");
		if (null != ofrom) {
			sfrom = ofrom.toString();
		}
		String OPINION = sfrom + Jurisdiction.getName() + ",fh," + pd.getString("OPINION");// 审批人的姓名+审批意见
		String msg = pd.getString("msg");
		// 获取伴随表表名
		PageData pd3 = new PageData();
		pd3.put("PROC_INST_ID_", PROC_INST_ID_);
		pd3 = ruprocdefService.getTableNameById(pd3);
		if ("yes".equals(msg)) { // 批准
			map.put("审批结果", "【验证封闭】" + OPINION); // 审批结果
			setVariablesByTaskIdAsMap(taskId, map); // 设置流程变量
			setVariablesByTaskId(taskId, "RESULT", "批准");
			completeMyPersonalTask(taskId);
			// 状态回写至伴随表
			pd3.put("STATUS", "1");
			updateStatusToAccompanyingTable(pd3);
		} else { // 驳回
			map.put("审批结果", "【退回】" + OPINION); // 审批结果
			setVariablesByTaskIdAsMap(taskId, map); // 设置流程变量
			setVariablesByTaskId(taskId, "RESULT", "驳回");
			completeMyPersonalTask(taskId);
			trySendSms(zmap, pd, "turndown");
			// 状态回写至伴随表
			pd3.put("STATUS", "2");
			updateStatusToAccompanyingTable(pd3);
		}
		try {
			removeVariablesByPROC_INST_ID_(PROC_INST_ID_, "RESULT"); // 移除流程变量(从正在运行中)
		} catch (Exception e) {
			/* 此流程变量在历史中 **/
		}
		try {
			String ASSIGNEE_ = pd.getString("ASSIGNEE_"); // 下一待办对象
			if (Tools.notEmpty(ASSIGNEE_)) {
				setAssignee(session.getAttribute("TASKID").toString(), ASSIGNEE_); // 指定下一任务待办对象
			} else {
				Object os = session.getAttribute("YAssignee");
				if (null != os && !"".equals(os.toString())) {
					ASSIGNEE_ = os.toString(); // 没有指定就是默认流程的待办人
				} else {
					// 状态回写至伴随表
					pd3.put("STATUS", "3");
					updateStatusToAccompanyingTable(pd3);
				}
			}
			zmap.put("ASSIGNEE_", ASSIGNEE_); // 用于给待办人发送新任务消息
		} catch (Exception e) {
			zmap.put("ASSIGNEE_", "null");
			/*
			 * 手动指定下一待办人，才会触发此异常。 任务结束不需要指定下一步办理人了,发送站内信通知任务发起人
			 **/
			// 状态回写至伴随表
			pd3.put("STATUS", "3");
			updateStatusToAccompanyingTable(pd3);
		}
		if (Tools.notEmpty(pd.getString("COPYTOER"))) {
			List<PageData> varList = ruprocdefService.varList(pd); // 列出流程变量列表
			this.sendSms(pd.getString("COPYTOER"), pd.getString("CREMARKS"), varList, map.get("审批结果").toString()); // 发站内信给抄送对象
		}
		zmap.put("result", errInfo); // 返回结果
		return zmap;
	}

	/**
	 * 检查当前流程下一节点是不是结束节点
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/isfinish")
	@ResponseBody
	public Object isfinish() throws Exception {
		Map<String, Object> zmap = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String PROC_INST_ID_ = pd.getString("PROC_INST_ID_");// 流程实例ID
		boolean isEnd = false;
		// 获取下一节点信息
		List<FlowElement> flowElements = getNextNodeInfo(PROC_INST_ID_);
		// 遍历每一个节点，线性审批只会到达一个节点。并行审批可能到达许多节点，所以要遍历
		for (FlowElement flowElement : flowElements) {
			if (flowElement instanceof UserTask) {
				// nothing to do
			}
			// 如果下一个节点属性是排他网关，则检查这个排他网关的每一个出口走向何处
			else if (flowElement instanceof ExclusiveGateway) {
				isEnd = goToEnd(flowElement);
			} else if (flowElement instanceof EndEvent) {
				isEnd = true;
			}
		}
		zmap.put("result", errInfo); // 返回结果
		zmap.put("flag", isEnd); // 返回结果
		return zmap;
	}

	/**
	 * 发送站内信
	 * 
	 * @param mv
	 * @param pd
	 * @throws Exception
	 */
	public void trySendSms(Map<String, Object> zmap, PageData pd, String type) throws Exception {
		List<PageData> hivarList = hiprocdefService.hiidentitylink(pd); // 列出历史流程参与者列表
		if (type.equals("finish")) { // 批准了给所有经办人发消息
			for (int i = 0; i < hivarList.size(); i++) {
				if ("starter".equals(hivarList.get(i).getString("TYPE_"))) {
					continue;// 不要给starter属性的人发消息，因为保存并提交审批后，发起人会成为第一个经办人。给starter属性的人发消息会导致发起人连续收到两条消息
				} else {
					String USERNAME = hivarList.get(i).getString("USER_ID_");
					sendSms(USERNAME, pd.getString("PROC_INST_ID_"), type);
					zmap.put("FHSMS", hivarList.get(i).getString("TEXT_"));
				}
			}
		} else { // 其它状态只给发起人发消息
			for (int i = 0; i < hivarList.size(); i++) {
				if ("starter".equals(hivarList.get(i).getString("TYPE_"))) {
					String USERNAME = hivarList.get(i).getString("USER_ID_");
					sendSms(USERNAME, pd.getString("PROC_INST_ID_"), type);
					zmap.put("FHSMS", hivarList.get(i).getString("TEXT_"));
				}
			}
		}
	}

	/**
	 * 发站内信通知审批结束
	 * 
	 * @param USERNAME
	 * @throws Exception
	 */
	public void sendSms(String USERNAME, String PROC_INST_ID_, String type) throws Exception {
		PageData pd = new PageData();
		pd.put("PROC_INST_ID_", PROC_INST_ID_);// 该消息绑定到的流程实例ID
		pd.put("SANME_ID", this.get32UUID()); // ID
		pd.put("SEND_TIME", DateUtil.getTime()); // 发送时间
		pd.put("FHSMS_ID", this.get32UUID()); // 主键
		pd.put("TYPE", "1"); // 类型1：收信
		pd.put("FROM_USERNAME", USERNAME); // 收信人
		pd.put("TO_USERNAME", "系统消息"); // 发信人
		pd.put("STATUS", "2");
		switch (type) {
		case "turndown":
			pd.put("CONTENT", "您发起的流程已经被驳回，请到待办任务列表查看详情");
			pd.put("LINK_TAG", "UPCOMING");
			break;
		case "finish":
			pd.put("CONTENT", "您发起/经办的流程已经审批完毕，可以动工了。请到已办任务列表查看详情");
			pd.put("LINK_TAG", "DONE");
			break;
		default:
			break;
		}
		fhsmsService.save(pd);
	}

	/**
	 * 发站内信给抄送对象
	 * 
	 * @param USERNAME
	 * @throws Exception
	 */
	public void sendSms(String USERNAME, String CREMARKS, List<PageData> varList, String OPINION) throws Exception {
		PageData pd = new PageData();
		pd.put("SANME_ID", this.get32UUID()); // ID
		pd.put("SEND_TIME", DateUtil.getTime()); // 发送时间
		pd.put("FHSMS_ID", this.get32UUID()); // 主键
		pd.put("TYPE", "1"); // 类型1：收信
		pd.put("FROM_USERNAME", USERNAME); // 收信人
		pd.put("TO_USERNAME", "任务抄送");
		StringBuffer sb = new StringBuffer();
		sb.append("[任务抄送]<br>\n");
		sb.append(
				"-------------------------------------------------------------------------------------------------------------<br>\n");
		sb.append("任务由" + Jurisdiction.getName() + "(" + Jurisdiction.getUsername() + ")" + "抄送<br>\n");
		sb.append("备注说明：" + CREMARKS + "<br>\n");
		sb.append(
				"申请事项-------------------------------------------------------------------------------------------------<br>\n");
		for (int i = 0; i < varList.size(); i++) {
			sb.append(varList.get(i).get("NAME_") + "：" + varList.get(i).get("TEXT_") + "<br>\n");
		}
		sb.append(
				"审批意见-------------------------------------------------------------------------------------------------<br>\n");
		sb.append(OPINION.replace(",fh,", ""));
		pd.put("CONTENT", sb.toString());
		pd.put("STATUS", "2");
		fhsmsService.save(pd);
	}

	// 写入状态到伴随表
	public String updateStatusToAccompanyingTable(PageData pd) {
		String errInfo = "success";
		try {
			accompanyingTableService.updateStatusToAccompanyingTable(pd);
		} catch (Exception e) {
			errInfo = e.getMessage();
		}
		return errInfo;
	}
}
