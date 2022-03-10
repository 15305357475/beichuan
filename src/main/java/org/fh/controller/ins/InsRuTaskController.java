package org.fh.controller.ins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.fh.controller.act.AcBusinessController;
import org.fh.entity.PageData;
import org.fh.plugins.websocketOnline.OnlineChatServer;
import org.fh.service.act.HiprocdefService;
import org.fh.service.act.RuprocdefService;
import org.fh.service.system.FhsmsService;
import org.fh.service.system.UsersService;
import org.fh.service.fhoa.AccompanyingTableService;
import org.fh.service.ins.InsMediaService;
import org.fh.service.ins.InsSysService;
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
@RequestMapping(value = "/insRutask")
public class InsRuTaskController extends AcBusinessController {

	@Autowired
	private RuprocdefService ruprocdefService;

	@Autowired
	private FhsmsService fhsmsService;

	@Autowired
	private HiprocdefService hiprocdefService;

	@Autowired
	private AccompanyingTableService accompanyingTableService;

	@Autowired
	private InsMediaService insMediaService;

	@Autowired
	private InsSysService inssysService;

	@Autowired
	private UsersService usersService;

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
		String NEXTSTATUS = pd.getString("NEXTSTATUS"); // 伴随表的下一个状态
		String REASON = pd.getString("REASON");// 原因分析
		String sfrom = "";
		Object ofrom = getVariablesByTaskIdAsMap(taskId, "审批结果");
		if (null != ofrom) {
			sfrom = ofrom.toString() + "；";
		}
		// 通过流程实例ID查询隐患实例ID
		PageData temp = new PageData();
		temp = inssysService.findByPROC_INST_ID_(pd);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String OPINION = sfrom + "[" + Jurisdiction.getUsername() + "]：,fh," + pd.getString("OPINION");// 审批人的姓名+审批意见
		String msg = pd.getString("msg");
		String LIVEPHOTO = "null";
		String CONTRACTOR = "null";// 承包商公司
		String TEAM_LEADER = "null";// 承包商班组长姓名
		// 现场图
		try {
			LIVEPHOTO = pd.getString("LIVEPHOTO");
		} catch (Exception e) {
			LIVEPHOTO = "null";
		}
		// 承包商公司
		try {
			CONTRACTOR = pd.getString("CONTRACTOR");
		} catch (Exception e) {
			CONTRACTOR = "null";
		}
		// 承包商班组长姓名
		try {
			TEAM_LEADER = pd.getString("TEAM_LEADER");
		} catch (Exception e) {
			TEAM_LEADER = "null";
		}
		if (!LIVEPHOTO.equals("null")) {
			String[] PHOTO_PATHS = LIVEPHOTO.split("∮");
			for (String string : PHOTO_PATHS) {
				if (!string.equals("N/A")) {
					// 记录现场照
					PageData photoBean = new PageData();
					String INSMEDIA_ID = this.get32UUID();
					photoBean.put("INSMEDIA_ID", INSMEDIA_ID);
					if (temp != null) {
						photoBean.put("INS_ID", temp.getString("INSSYS_ID"));
					}
					photoBean.put("PROC_INST_ID_", PROC_INST_ID_);
					photoBean.put("TASK_ID_", taskId);
					photoBean.put("NODE", "整改");
					photoBean.put("CREATE_USER", Jurisdiction.getUsername());
					photoBean.put("MEDIA_TYPE", "image/*");
					photoBean.put("MEDIA_NAME", "整改后.jpg");
					photoBean.put("MEDIA_PATH", string);
					photoBean.put("STATUS", "1");
					try {
						insMediaService.save(photoBean);
					} catch (Exception e) {
						System.out.println("记录隐患排查现场图失败：" + e.getMessage());
						continue;
					}
				}
			}
		}
		// 获取伴随表表名
		PageData pd3 = new PageData();
		pd3.put("PROC_INST_ID_", PROC_INST_ID_);
		pd3 = ruprocdefService.getTableNameById(pd3);
		if ("yes".equals(msg)) { // 批准
			map.put("审批结果", OPINION); // 审批结果
			setVariablesByTaskIdAsMap(taskId, map); // 设置流程变量
			setVariablesByTaskId(taskId, "RESULT", "批准");
			completeMyPersonalTask(taskId);
			// 状态回写至伴随表
			if (NEXTSTATUS.equals("2")) {
				pd3.put("STATUS", NEXTSTATUS);
				pd3.put("FIELDNAME0", "CONTRACTOR_TEAM_LEADER");
				pd3.put("FIELDVALUE0", TEAM_LEADER);
				pd3.put("FIELDNAME1", "CONTRACTOR");
				pd3.put("FIELDVALUE1", CONTRACTOR);
				if (!REASON.equals("null") && !REASON.equals("无")) {
					pd3.put("FIELDNAME2", "REASON_ANALYSIS");
					pd3.put("FIELDVALUE2", "[" + Jurisdiction.getUsername() + "]：" + REASON + "；");
				}
				updateStatusAndFieldToAccompanyingTable(pd3);
			} else if (NEXTSTATUS.equals("3")) {
				pd3.put("STATUS", NEXTSTATUS);
				pd3.put("FIELDNAME2", "REASON_ANALYSIS");
				pd3.put("FIELDVALUE2", "[" + Jurisdiction.getUsername() + "]：" + OPINION.replaceAll("fh,", ""));
				updateStatusAndFieldToAccompanyingTable(pd3);
			}
		} else { // 驳回
			map.put("审批结果", "[驳回]" + OPINION); // 审批结果
			setVariablesByTaskIdAsMap(taskId, map); // 设置流程变量
			setVariablesByTaskId(taskId, "RESULT", "驳回");
			completeMyPersonalTask(taskId);
			trySendSms(zmap, pd, "turndown");
			// 状态回写至伴随表
			pd3.put("STATUS", "2");
			updateStatusAndFieldToAccompanyingTable(pd3);
		}
		try {
			removeVariablesByPROC_INST_ID_(PROC_INST_ID_, "RESULT"); // 移除流程变量(从正在运行中)
		} catch (Exception e) {
			/* 此流程变量在历史中 **/
		}
		// 结束节点就不需要指定下一任办理人
		if (!NEXTSTATUS.equals("3")) {
			// 如果明确指派了下一任办理人，则设置办理人为指派对象
			// 如果没有指派，则从任务监听器中取出下一任办理人(即在流程图中设置的固定办理人)
			// 如果没有任务监听时，则认为到了结束节点
			try {
				String ASSIGNEE_ = pd.getString("ASSIGNEE_"); // 下一待办对象
				if (Tools.notEmpty(ASSIGNEE_)) {
					setAssignee(session.getAttribute("TASKID").toString(), ASSIGNEE_); // 指定下一任务待办对象
				} else {
					Object os = session.getAttribute("YAssignee");
					if (null != os && !"".equals(os.toString())) {
						ASSIGNEE_ = os.toString(); // 没有指定就是默认流程的待办人
						setAssignee(session.getAttribute("TASKID").toString(), ASSIGNEE_);
					} else {// 没有任务监听时，默认流程结束，发送站内信给任务发起人
						// 获取该流程的历史经办人列表
						PageData pd2 = new PageData();
						pd2.put("PROC_INST_ID_", PROC_INST_ID_);
						trySendSms(zmap, pd2, "finish");
						// 发站内信给抄送对象
						try {
							List<PageData> varList = ruprocdefService.varList(pd); // 列出流程变量列表
							String ccTarget = temp.getString("CC_");
							// 以R开头，且长度是X位的是抄送到角色
							char firstChar = ccTarget.charAt(0);
							if (ccTarget.length() == 15 && firstChar == 'R') {
								PageData tempPd2 = new PageData();
								tempPd2.put("RNUMBER", pd.getString("CC_"));
								List<PageData> ccGroupList = new ArrayList<PageData>();
								ccGroupList = usersService.listAllUserByRNUMBER(tempPd2);
								if (ccGroupList.size() > 0 && ccGroupList != null) {
									for (PageData pageData : ccGroupList) {
										String one = pageData.getString("USERNAME");
										this.sendSms(one, "一个新隐患整改完毕", varList, "请留意", PROC_INST_ID_);
									}
								}
							} else {// 抄送到人
								this.sendSms(ccTarget, "一个新隐患整改完毕", varList, "请留意", PROC_INST_ID_);
							}
						} catch (Exception e) {
							System.out.println("隐患整改完毕抄送失败");
						}

					}
				}
				zmap.put("ASSIGNEE_", ASSIGNEE_); // 用于给待办人发送新任务消息
				// 发送 Websocket消息通知待办人
				try {
					OnlineChatServer.senFhTask(ASSIGNEE_);
				} catch (Exception e) {
					// TODO: handle exception
				}
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
				// 状态回写至伴随表并更新原因
				pd3.put("STATUS", "3");
				pd3.put("FIELDNAME2", "REASON_ANALYSIS");
				pd3.put("FIELDVALUE2", "[" + Jurisdiction.getUsername() + "]：" + OPINION.replaceAll("fh,", ""));
				updateStatusAndFieldToAccompanyingTable(pd3);
			}
		}

		// 发站内信给抄送对象
		if (Tools.notEmpty(pd.getString("COPYTOER"))) {
			List<PageData> varList = ruprocdefService.varList(pd); // 列出流程变量列表
			this.sendSms(pd.getString("COPYTOER"), pd.getString("CREMARKS"), varList, map.get("审批结果").toString(),
					PROC_INST_ID_);
		}
		zmap.put("result", errInfo); // 返回结果
		return zmap;
	}

	/**
	 * 隐患排查模块上传现场照片(手机端)
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/LivePhoto")
	@ResponseBody
	public Object LivePhoto() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String UUID = this.get32UUID();

		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		String pathimg = PathUtil.getProjectpath() + Const.INS_IMGS + year + "//" + month + "//" + UUID + ".jpg";
		if (pd.getString("PHOTODATA").indexOf("data:image/") != -1) {
			String imgData = pd.getString("PHOTODATA").split(",")[1];
			boolean flag = ImageAnd64Binary.generateImage(imgData, pathimg);
			if (!flag) {
				errInfo = "error";
			}
			map.put("img64", imgData);
		} else {
			errInfo = "error";
		}
		map.put("imgpath", Const.INS_IMGS + year + "//" + month + "//" + UUID + ".jpg");
		map.put("result", errInfo);
		return map;
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
			pd.put("CONTENT", "您发起的隐患排查整改流程已经被驳回，请到待办任务列表查看详情");
			pd.put("LINK_TAG", "UPCOMING");
			break;
		case "finish":
			pd.put("CONTENT", "您发起/经办的隐患排查整改流程已经整改完毕。请到已办任务列表查看详情");
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
	public void sendSms(String USERNAME, String CREMARKS, List<PageData> varList, String OPINION, String PROC_INST_ID_)
			throws Exception {
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
		sb.append("批注说明：" + CREMARKS + "<br>\n");
		sb.append(
				"隐患事项----------------------------------------------------------------------------------------------<br>\n");
		for (int i = 0; i < varList.size(); i++) {
			sb.append(varList.get(i).get("NAME_") + "：" + varList.get(i).get("TEXT_") + "<br>\n");
		}
		sb.append(
				"审批意见----------------------------------------------------------------------------------------------<br>\n");
		sb.append(OPINION.replace(",fh,", ""));
		pd.put("CONTENT", sb.toString());
		pd.put("LINK_TAG", "DONE");
		pd.put("STATUS", "2");
		pd.put("PROC_INST_ID_", PROC_INST_ID_);
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

	// 根据流程实例ID修改伴随表状态和任意指定的一个字段的字段值
	public String updateStatusAndFieldToAccompanyingTable(PageData pd) {
		String errInfo = "success";
		try {
			accompanyingTableService.updateStatusAndFieldToAccompanyingTable(pd);
		} catch (Exception e) {
			errInfo = e.getMessage();
		}
		return errInfo;
	}
}
