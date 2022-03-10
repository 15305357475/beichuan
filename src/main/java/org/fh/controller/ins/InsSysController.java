package org.fh.controller.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.ImageAnd64Binary;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.util.exportUtil.DataFerryObj;
import org.fh.util.exportUtil.InsExportToExcelObj;
import org.fh.util.exportUtil.InsExportToExcelObj2;
import org.flowable.engine.RuntimeService;
import org.fh.entity.PageData;
import org.fh.plugins.websocketOnline.OnlineChatServer;
import org.fh.service.act.RuprocdefService;
import org.fh.service.fhoa.DepartmentService;
import org.fh.service.ins.InsMediaService;
import org.fh.service.ins.InsSysService;
import org.fh.service.system.DictionariesService;
import org.fh.service.system.FhsmsService;
import org.fh.service.system.UsersService;

/**
 * 说明：隐患排查主表 作者：fsci 时间：2021-04-13 授权：bsic
 */
@Controller
@RequestMapping("/inssys")
public class InsSysController extends AcStartController {

	@Autowired
	private InsSysService inssysService;
	@Autowired
	private InsMediaService insMediaService;
	@Autowired
	private DictionariesService dicService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private FhsmsService fhsmsService;
	@Autowired
	private RuprocdefService ruprocdefService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private DepartmentService departmentService;

	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("inssys:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 启动实例
		String fk = null;
		Map<String, Object> variableMap = new LinkedHashMap<String, Object>();
		String USERNAME = Jurisdiction.getUsername();
		variableMap.put("检查类型", pd.getString("CHECK_TYPE"));
		variableMap.put("隐患级别", getNameByCode(pd.getString("INS_LEVEL")));
		if(!pd.getString("WORK_TYPE_BASE").equals("未填写") && !pd.getString("WORK_TYPE").equals("无")){
			variableMap.put("作业类型", pd.getString("WORK_TYPE_BASE") + "->" + getNameByCode(pd.getString("WORK_TYPE")));
		}
		variableMap.put("责任单位", getNameByCode(pd.getString("RESPONSIBLE_UNIT")).replace("区域定义", ""));
		variableMap.put("隐患区域", pd.getString("AREA"));
		/*
		if (!pd.getString("MP_UNIT").equals("") && !pd.getString("MP_UNIT").equals("null")) {
			variableMap.put("密配单位", pd.getString("MP_UNIT"));
		}
		*/
		if (!pd.getString("INS_PROJ").equals("") && !pd.getString("INS_PROJ").equals("null")) {
			variableMap.put("涉及项目", getNameByCode(pd.getString("INS_PROJ")));
		}
		variableMap.put("隐患描述", pd.getString("INS_DETIAL"));
		if(!pd.getString("RECTIFY_SUGGEST").equals("") && !pd.getString("RECTIFY_SUGGEST").equals("null")){
			variableMap.put("整改要求或建议", pd.getString("RECTIFY_SUGGEST"));
		}

		variableMap.put("最迟整改日期", pd.getString("LATEST_RECTIFY_DATE"));
		variableMap.put("隐患类型", getNameByCode(pd.getString("INS_TYPE_L1")));
		variableMap.put("隐患发现日期", DateUtil.getTime());
		if (!pd.getString("FINE").equals("0") && !pd.getString("FINE").equals("null")) {
			variableMap.put("罚款金额", pd.getString("FINE"));
		}
		variableMap.put("USERNAME", USERNAME); // 创建人
		variableMap.put("发起人部门", getDeptNameByCode(Jurisdiction.getUSER_DEPT())); // 发起人部门
		if (!pd.getString("CLOSE_USER").equals("") && !pd.getString("CLOSE_USER").equals("null")) {// 委派封闭人
			variableMap.put("委派封闭人", pd.getString("CLOSE_USER"));
		} else {
			variableMap.put("委派封闭人", USERNAME);
		}
		try {
			fk = startProcessInstanceByKeyHasVariables("InsProcess", variableMap, USERNAME);// 启动流程实例通过KEY
			map.put("PROC_INST_ID_", fk);// 流程实例主键返回
		} catch (Exception e) {
			errInfo = "error";
			map.put("errorMsg", e.getMessage());
			map.put("result", errInfo); // 返回结果
			return map;
		}

		// 数据入库
		String INSSYS_ID = this.get32UUID();
		pd.put("INSSYS_ID", INSSYS_ID);
		pd.put("INIT_USER", USERNAME);
		pd.put("INIT_DEPT", Jurisdiction.getUSER_DEPT());
		pd.put("PROC_INST_ID_", fk);
		pd.put("TO_", pd.getString("ASSIGNEE_"));
		pd.put("STATUS", "1");
		// 委派封闭人
		if (!pd.getString("CLOSE_USER").equals("") && !pd.getString("CLOSE_USER").equals("null")) {// 委派封闭人
			pd.put("CLOSE_USER", pd.getString("CLOSE_USER"));
		} else {
			pd.put("CLOSE_USER", USERNAME);
		}
		try {
			inssysService.save(pd);
		} catch (Exception e) {
			System.out.println(e);
			errInfo = "errer";
			// 回滚刚刚创建的流程实例
			runtimeService.deleteProcessInstance(fk, "伴随表写入失败,事务回滚");
			map.put("errorMsg", e.getMessage());
			map.put("result", errInfo); // 返回结果
			return map;
		}

		// 图片入库
		if (!pd.getString("INSMEDIA_I_PATHS").equals("NONE")) {
			String PATHS[] = pd.getString("INSMEDIA_I_PATHS").split(",");
			String NAMES[] = pd.getString("INSMEDIA_I_NAMES").split(",");
			List<String> INSMEDIA_IDs = new ArrayList<String>();
			int i = 0;
			for (String onePath : PATHS) {
				PageData tempPd = new PageData();
				String INSMEDIA_ID = this.get32UUID();
				tempPd.put("INSMEDIA_ID", INSMEDIA_ID);
				tempPd.put("INS_ID", INSSYS_ID);
				tempPd.put("PROC_INST_ID_", fk);
				tempPd.put("NODE", "创建");
				tempPd.put("CREATE_USER", USERNAME);
				tempPd.put("MEDIA_TYPE", "image/*");
				tempPd.put("MEDIA_NAME", NAMES[i]);
				tempPd.put("MEDIA_PATH", onePath);
				tempPd.put("STATUS", "1");
				i++;
				try {
					insMediaService.save(tempPd);
					INSMEDIA_IDs.add(INSMEDIA_ID);
				} catch (Exception e) {
					errInfo = "errer";
					// 回滚刚刚创建的流程实例
					runtimeService.deleteProcessInstance(fk, "伴随表写入失败,事务回滚");
					// 回滚隐患实例数据
					PageData bak = new PageData();
					bak.put("INSSYS_ID", INSSYS_ID);
					inssysService.delete(bak);
					// 回滚上传成功的图片
					for (String mediaEntity : INSMEDIA_IDs) {
						PageData bak2 = new PageData();
						bak2.put("INSMEDIA_ID", mediaEntity);
						insMediaService.delete(bak2);
					}
					map.put("errorMsg", e.getMessage());
					map.put("result", errInfo); // 返回结果
					return map;
				}
			}
		}

		// 音频入库
		if (!pd.getString("INSMEDIA_A_PATH").equals("NONE")) {
			PageData audioEntity = new PageData();
			String INSMEDIA_ID = this.get32UUID();
			audioEntity.put("INSMEDIA_ID", INSMEDIA_ID);
			audioEntity.put("INS_ID", INSSYS_ID);
			audioEntity.put("PROC_INST_ID_", fk);
			audioEntity.put("NODE", "创建");
			audioEntity.put("CREATE_USER", USERNAME);
			audioEntity.put("MEDIA_TYPE", "audio/*");
			audioEntity.put("MEDIA_NAME", pd.getString("INSMEDIA_A_NAME"));
			audioEntity.put("MEDIA_PATH", pd.getString("INSMEDIA_A_PATH"));
			audioEntity.put("STATUS", "1");
			try {
				insMediaService.save(audioEntity);
			} catch (Exception e) {
				errInfo = "errer";
				map.put("errorMsg", e.getMessage());
				map.put("result", errInfo); // 返回结果
				return map;
			}
		}

		// 给抄送对象发站内信
		try {
			if (Tools.notEmpty(pd.getString("CC_"))) {
				PageData tempPd = new PageData();
				tempPd.put("PROC_INST_ID_", fk);
				List<PageData> varList = ruprocdefService.varList(tempPd); // 列出流程变量列表
				if (pd.getString("CC_TYPE").equals("P")) {// 抄送到个人
					this.sendSms(pd.getString("CC_"), "一个新隐患需您留意", varList, "请留意", fk);
				}
				if (pd.getString("CC_TYPE").equals("G")) {// 抄送到组
					PageData tempPd2 = new PageData();
					tempPd2.put("RNUMBER", pd.getString("CC_"));
					List<PageData> ccGroupList = new ArrayList<PageData>();
					ccGroupList = usersService.listAllUserByRNUMBER(tempPd2);
					if (ccGroupList.size() > 0 && ccGroupList != null) {
						for (PageData pageData : ccGroupList) {
							String one = pageData.getString("USERNAME");
							this.sendSms(one, "一个新隐患需您留意", varList, "请留意", fk);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("隐患发起完毕抄送失败");
		}
		// 发送 Websocket消息通知待办人
		try {
			OnlineChatServer.senFhTask(pd.getString("ASSIGNEE_"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		// 结果返回
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 新增，仅登记
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveRegOnly")
	@RequiresPermissions("inssys:add")
	@ResponseBody
	public Object addRegOnly() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();

		// 数据入库
		String INSSYS_ID = this.get32UUID();
		pd.put("INSSYS_ID", INSSYS_ID);
		pd.put("INIT_USER", Jurisdiction.getUsername());
		pd.put("INIT_DEPT", Jurisdiction.getUSER_DEPT());
		pd.put("PROC_INST_ID_", "");
		pd.put("TO_", "");
		pd.put("STATUS", "5");// 仅登记隐患
		pd.put("CLOSE_USER", "");
		try {
			inssysService.save(pd);
		} catch (Exception e) {
			errInfo = "errer";
			map.put("errorMsg", e.getMessage());
			map.put("result", errInfo); // 返回结果
			return map;
		}

		// 图片入库
		if (!pd.getString("INSMEDIA_I_PATHS").equals("NONE")) {
			String PATHS[] = pd.getString("INSMEDIA_I_PATHS").split(",");
			String NAMES[] = pd.getString("INSMEDIA_I_NAMES").split(",");
			List<String> INSMEDIA_IDs = new ArrayList<String>();
			int i = 0;
			for (String onePath : PATHS) {
				PageData tempPd = new PageData();
				String INSMEDIA_ID = this.get32UUID();
				tempPd.put("INSMEDIA_ID", INSMEDIA_ID);
				tempPd.put("INS_ID", INSSYS_ID);
				tempPd.put("PROC_INST_ID_", "");
				tempPd.put("NODE", "创建");
				tempPd.put("CREATE_USER", Jurisdiction.getUsername());
				tempPd.put("MEDIA_TYPE", "image/*");
				tempPd.put("MEDIA_NAME", NAMES[i]);
				tempPd.put("MEDIA_PATH", onePath);
				tempPd.put("STATUS", "1");
				i++;
				try {
					insMediaService.save(tempPd);
					INSMEDIA_IDs.add(INSMEDIA_ID);
				} catch (Exception e) {
					errInfo = "errer";
					// 回滚隐患实例数据
					PageData bak = new PageData();
					bak.put("INSSYS_ID", INSSYS_ID);
					inssysService.delete(bak);
					// 回滚上传成功的图片
					for (String mediaEntity : INSMEDIA_IDs) {
						PageData bak2 = new PageData();
						bak2.put("INSMEDIA_ID", mediaEntity);
						insMediaService.delete(bak2);
					}
					map.put("errorMsg", e.getMessage());
					map.put("result", errInfo); // 返回结果
					return map;
				}
			}
		}

		// 音频入库
		if (!pd.getString("INSMEDIA_A_PATH").equals("NONE")) {
			PageData audioEntity = new PageData();
			String INSMEDIA_ID = this.get32UUID();
			audioEntity.put("INSMEDIA_ID", INSMEDIA_ID);
			audioEntity.put("INS_ID", INSSYS_ID);
			audioEntity.put("PROC_INST_ID_", "");
			audioEntity.put("NODE", "创建");
			audioEntity.put("CREATE_USER", Jurisdiction.getUsername());
			audioEntity.put("MEDIA_TYPE", "audio/*");
			audioEntity.put("MEDIA_NAME", pd.getString("INSMEDIA_A_NAME"));
			audioEntity.put("MEDIA_PATH", pd.getString("INSMEDIA_A_PATH"));
			audioEntity.put("STATUS", "1");
			try {
				insMediaService.save(audioEntity);
			} catch (Exception e) {
				errInfo = "errer";
				map.put("errorMsg", e.getMessage());
				map.put("result", errInfo); // 返回结果
				return map;
			}
		}
		// 结果返回
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("inssys:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = inssysService.findById(pd);
		String PROC_INST_ID_ = pd.getString("PROC_INST_ID_");
		pd.put("STATUS", "0");
		// 删除数据
		try {
			inssysService.edit(pd);
		} catch (Exception e) {
			errInfo = "error";
			map.put("errorMsg", e.getMessage());
			return map;
		}
		// 结束流程
		try {
			runtimeService.deleteProcessInstance(PROC_INST_ID_, "创建人删除");
		} catch (Exception e) {
			errInfo = "error";
			map.put("errorMsg", e.getMessage());
			return map;
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
	@RequiresPermissions("inssys:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		inssysService.edit(pd);
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
	@RequiresPermissions("inssys:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		String STATUS = pd.getString("STATUS"); // 状态
		String ABOUT = pd.getString("ABOUT"); // 相关性
		pd.put("RNUMBERS", Jurisdiction.getRnumbers());// 当前用户角色组
		if (Tools.notEmpty(KEYWORDS)) {
			pd.put("KEYWORDS", KEYWORDS.trim());
		}
		if (Tools.notEmpty(STATUS)) {
			if (STATUS.equals("outdate")) {// 超时未改
				pd.put("STATUS", "1");
				pd.put("LATEST_RECTIFY_DATE", DateUtil.getDay());
			} else {
				pd.put("STATUS", STATUS.trim());
			}
		}
		// 判断相关性
		if (Tools.notEmpty(ABOUT)) {
			switch (ABOUT) {
			case "my":// 发起
				if (!Jurisdiction.getUsername().equals("admin")) {
					pd.put("INIT_USER", Jurisdiction.getUsername());
				}
				break;
			case "tome":// 主送
				if (!Jurisdiction.getUsername().equals("admin")) {
					pd.put("TO_", Jurisdiction.getUsername());
				}
				break;
			case "ccme":// 抄送
				if (!Jurisdiction.getUsername().equals("admin")) {
					pd.put("CC_", Jurisdiction.getUsername());
				}
				break;
			case "mydept":// 我部隐患
				if (!Jurisdiction.getUsername().equals("admin")) {
					pd.put("RESPONSIBLE_UNIT", "AREA" + Jurisdiction.getUSER_DEPT());
				}
				break;
			default:
				break;
			}
		}
		page.setPd(pd);
		List<PageData> varList = inssysService.list(page); // 列出InsSys列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 统计与导出
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/statistics")
	@RequiresPermissions("inssys:list")
	@ResponseBody
	public Object Statistics(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String ABOUT = pd.getString("ABOUT");
		String STATUS = pd.getString("STATUS");
		if (ABOUT.equals("I")) {// 查询发起单位
			String RESPONSIBLE_UNIT = pd.getString("RESPONSIBLE_UNIT");
			RESPONSIBLE_UNIT = RESPONSIBLE_UNIT.replaceAll("AREA", "");
			pd.put("INIT_DEPT", RESPONSIBLE_UNIT);
			pd.remove("RESPONSIBLE_UNIT");// 移除责任单位属性
		}
		if (ABOUT.equals("ALL")) {// 查询全部
			pd.remove("RESPONSIBLE_UNIT");// 移除责任单位属性
		}
		if (Tools.notEmpty(STATUS)) {
			if (STATUS.equals("outdate")) {// 超时未改
				pd.put("STATUS", "1");
				pd.put("LATEST_RECTIFY_DATE", DateUtil.getDay());
			}
		}
		page.setPd(pd);
		List<PageData> varList = inssysService.DataStatisticslist(page);
		map.put("varList", varList);
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
	@RequiresPermissions("inssys:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = inssysService.findById(pd); // 根据ID读取
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
	@RequiresPermissions("inssys:del")
	@ResponseBody
	public Object deleteAll() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (Tools.notEmpty(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			inssysService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 通过编码获取名字
	 * 
	 * @throws Exception
	 * 
	 */
	public String getNameByCode(String code) throws Exception {
		String name = "";
		PageData pd = new PageData();
		pd.put("BIANMA", code);
		pd = dicService.findByBianma(pd);
		if (null != pd) {
			name = pd.getString("NAME");
		}
		return name;
	}

	/**
	 * 通过部门号获取部门名
	 * 
	 * @throws Exception
	 * 
	 */
	public String getDeptNameByCode(String code) throws Exception {
		String name = "";
		PageData pd = new PageData();
		pd.put("DEPARTMENT_ID", code);
		pd = departmentService.findById(pd);
		if (null != pd) {
			name = pd.getString("NAME");
		}
		return name;
	}

	/**
	 * 隐患现场录音和照片
	 * 
	 * *
	 */
	@RequestMapping(value = "/getMedia")
	@ResponseBody
	public Object getMedia() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String TYPE = pd.getString("TYPE");
		if (!TYPE.equals("ALL")) {
			pd.put("MEDIA_TYPE", pd.getString("TYPE"));
		}
		List<PageData> varList = insMediaService.findByProc(pd); // 列出媒体列表;
		if (varList.size() > 0) {
			// 将每一条记录转码成base64
			List<String> base64ListI = new ArrayList<String>();// 图片base64
			List<String> base64ListA = new ArrayList<String>();// 音频base64
			List<PageData> varListI = new ArrayList<PageData>();// 图片属性信息列表
			List<PageData> varListA = new ArrayList<PageData>();// 音频属性信息列表
			for (PageData pageData : varList) {
				String onePath = PathUtil.getProjectpath() + pageData.getString("MEDIA_PATH");
				String Base64String = ImageAnd64Binary.getImageStr(onePath);
				if (pageData.getString("MEDIA_TYPE").equals("image/*")) {
					base64ListI.add(Base64String);
					varListI.add(pageData);
				}
				if (pageData.getString("MEDIA_TYPE").equals("audio/*")) {
					base64ListA.add(Base64String);
					varListA.add(pageData);
				}

			}
			map.put("base64ListI", base64ListI);
			map.put("base64ListA", base64ListA);
			map.put("varListI", varListI);
			map.put("varListA", varListA);
		}
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 隐患现场录音和照片
	 * 
	 * *
	 */
	@RequestMapping(value = "/getMediaByInsId")
	@ResponseBody
	public Object getMediaByInsId() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String TYPE = pd.getString("TYPE");
		if (!TYPE.equals("ALL")) {
			pd.put("MEDIA_TYPE", pd.getString("TYPE"));
		}
		List<PageData> varList = insMediaService.findByINS_ID(pd); // 列出媒体列表;
		if (varList.size() > 0) {
			// 将每一条记录转码成base64
			List<String> base64ListI = new ArrayList<String>();// 图片base64
			List<String> base64ListA = new ArrayList<String>();// 音频base64
			List<PageData> varListI = new ArrayList<PageData>();// 图片属性信息列表
			List<PageData> varListA = new ArrayList<PageData>();// 音频属性信息列表
			for (PageData pageData : varList) {
				String onePath = PathUtil.getProjectpath() + pageData.getString("MEDIA_PATH");
				String Base64String = ImageAnd64Binary.getImageStr(onePath);
				if (pageData.getString("MEDIA_TYPE").equals("image/*")) {
					base64ListI.add(Base64String);
					varListI.add(pageData);
				}
				if (pageData.getString("MEDIA_TYPE").equals("audio/*")) {
					base64ListA.add(Base64String);
					varListA.add(pageData);
				}

			}
			map.put("base64ListI", base64ListI);
			map.put("base64ListA", base64ListA);
			map.put("varListI", varListI);
			map.put("varListA", varListA);
		}
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 标记解决
	 * 
	 * *
	 */
	@RequestMapping(value = "/Solve")
	@ResponseBody
	public Object Solve() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 主键查询
		PageData temp = new PageData();
		temp = inssysService.findById(pd);
		// 设置更新信息
		temp.put("RECTIFY_SUGGEST", pd.getString("OPINION"));// 借用整改建议字段填写实际的整改结果
		temp.put("REASON_ANALYSIS", pd.getString("REASON"));// 原因分析
		temp.put("CLOSE_USER", Jurisdiction.getUsername());// 借用委派封闭人字段填写实际的整改人
		temp.put("TO_", Jurisdiction.getUsername());// 借用主送人字段填写实际的整改人
		temp.put("CLOSE_DATE", DateUtil.getTime());// 封闭日期
		temp.put("STATUS", "6");// 已解决状态
		// 更新数据
		try {
			inssysService.edit(temp);
		} catch (Exception e) {
			// TODO: handle exception
			errInfo = "error:" + e.getMessage();
		}
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 转线上
	 * 
	 * *
	 */
	@RequestMapping(value = "/gotoOnline")
	@ResponseBody
	public Object gotoOnline() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 主键查询
		PageData temp = new PageData();
		temp = inssysService.findById(pd);
		// 启动流程实例
		String fk = null;
		Map<String, Object> variableMap = new LinkedHashMap<String, Object>();
		String USERNAME = Jurisdiction.getUsername();
		variableMap.put("隐患级别", getNameByCode(pd.getString("INS_LEVEL")));
		variableMap.put("作业类型", pd.getString("WORK_TYPE_BASE") + "->" + getNameByCode(pd.getString("WORK_TYPE")));
		variableMap.put("责任单位", getNameByCode(pd.getString("RESPONSIBLE_UNIT")).replace("区域定义", ""));
		variableMap.put("隐患区域", getNameByCode(pd.getString("AREA")));
		if (!pd.getString("MP_UNIT").equals("") && !pd.getString("MP_UNIT").equals("null")) {
			variableMap.put("密配单位", pd.getString("MP_UNIT"));
		}
		if (!pd.getString("INS_PROJ").equals("") && !pd.getString("INS_PROJ").equals("null")) {
			variableMap.put("涉及项目", getNameByCode(pd.getString("INS_PROJ")));
		}
		variableMap.put("隐患描述", temp.getString("INS_DETIAL"));
		variableMap.put("整改要求或建议", pd.getString("RECTIFY_SUGGEST"));
		variableMap.put("最迟整改日期", pd.getString("LATEST_RECTIFY_DATE"));
		variableMap.put("隐患类型", getNameByCode(pd.getString("INS_TYPE_L1")));
		variableMap.put("隐患发现日期", DateUtil.getTime());
		variableMap.put("USERNAME", USERNAME); // 创建人
		variableMap.put("发起人部门", getDeptNameByCode(Jurisdiction.getUSER_DEPT())); // 发起人部门
		if (!pd.getString("CLOSE_USER").equals("") && !pd.getString("CLOSE_USER").equals("null")) {// 委派封闭人
			variableMap.put("委派封闭人", pd.getString("CLOSE_USER"));
		} else {
			variableMap.put("委派封闭人", USERNAME);
		}
		try {
			fk = startProcessInstanceByKeyHasVariables("InsProcess", variableMap, USERNAME);// 启动流程实例通过KEY
			map.put("PROC_INST_ID_", fk);// 流程实例主键返回
		} catch (Exception e) {
			errInfo = "errer";
			map.put("errorMsg", e.getMessage());
			map.put("result", errInfo); // 返回结果
			return map;
		}
		// 流程实例主键绑定到媒体表
		PageData mediaPd = new PageData();
		mediaPd.put("PROC_INST_ID_",fk);
		mediaPd.put("INS_ID",pd.getString("INSSYS_ID"));
		try {
			insMediaService.edit(mediaPd);
		} catch (Exception e) {
			errInfo = "errer";
			// 回滚刚刚创建的流程实例
			runtimeService.deleteProcessInstance(fk, "媒体表写入失败,事务回滚");
			map.put("errorMsg", e.getMessage());
			map.put("result", errInfo); // 返回结果
			return map;
		}
		// 更新隐患主表数据
		pd.put("PROC_INST_ID_", fk);
		pd.put("TO_", pd.getString("ASSIGNEE_"));
		pd.put("STATUS", "1");
		// 委派封闭人
		if (!pd.getString("CLOSE_USER").equals("") && !pd.getString("CLOSE_USER").equals("null")) {// 委派封闭人
			pd.put("CLOSE_USER", pd.getString("CLOSE_USER"));
		} else {
			pd.put("CLOSE_USER", temp.getString("INIT_USER"));
		}
		try {
			inssysService.edit(pd);
		} catch (Exception e) {
			errInfo = "errer";
			// 回滚刚刚创建的流程实例
			runtimeService.deleteProcessInstance(fk, "主表写入失败,事务回滚");
			map.put("errorMsg", e.getMessage());
			map.put("result", errInfo); // 返回结果
			return map;
		}
		map.put("result", errInfo);
		return map;
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
		pd.put("TO_USERNAME", Jurisdiction.getUsername());
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
		pd.put("LINK_TAG", "UPCOMING");
		pd.put("STATUS", "2");
		pd.put("PROC_INST_ID_", PROC_INST_ID_);
		fhsmsService.save(pd);
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
		titles.add("INSSYS_ID"); // 1
		titles.add("INIT_USER"); // 2
		titles.add("INIT_DEPT"); // 3
		titles.add("INIT_DATE"); // 4
		titles.add("PROC_INST_ID_"); // 5
		titles.add("AREA"); // 6
		titles.add("MP_UNIT"); // 7
		titles.add("RESPONSIBLE_UNIT"); // 8
		titles.add("TO_"); // 9
		titles.add("CC_"); // 10
		titles.add("INS_TYPE_L1"); // 11
		titles.add("INS_TYPE_L2"); // 12
		titles.add("WORK_TYPE"); // 13
		titles.add("INS_DETIAL"); // 14
		titles.add("INS_LEVEL"); // 15
		titles.add("LATEST_RECTIFY_DATE"); // 16
		titles.add("RECTIFY_SUGGEST"); // 17
		titles.add("STATUS"); // 18
		titles.add("SYNC_DATE"); // 19
		dataMap.put("titles", titles);
		List<PageData> varOList = inssysService.listDataForFerry(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			WARRANTY_IDS.add(varOList.get(i).getString("INSSYS_ID"));
			PageData vpd = new PageData();
			vpd.put("var1", Integer.toString(i + 1)); // 0
			vpd.put("var2", varOList.get(i).getString("INSSYS_ID")); // 1
			vpd.put("var3", varOList.get(i).getString("INIT_USER")); // 2
			vpd.put("var4", varOList.get(i).getString("INIT_DEPT_NAME")); // 3
			try {
				DateFormat df = DateUtil.createDateFormat();
				vpd.put("var5", df.format(varOList.get(i).get("INIT_DATE"))); // 4
			} catch (Exception e) {
				vpd.put("var5", "");
			}
			vpd.put("var6", varOList.get(i).getString("PROC_INST_ID_")); // 5
			vpd.put("var7", varOList.get(i).getString("AREA_NAME")); // 6
			vpd.put("var8", varOList.get(i).getString("MP_UNIT")); // 7
			vpd.put("var9", varOList.get(i).getString("RESPONSIBLE_UNIT_NAME")); // 8
			vpd.put("var10", varOList.get(i).getString("TO_")); // 9
			vpd.put("var11", varOList.get(i).getString("CC_")); // 10
			vpd.put("var12", varOList.get(i).getString("INS_TYPE_L1_NAME")); // 11
			vpd.put("var13", varOList.get(i).getString("INS_TYPE_L2_NAME")); // 12
			vpd.put("var14", varOList.get(i).getString("WORK_TYPE_NAME")); // 13
			vpd.put("var15", varOList.get(i).getString("INS_DETIAL")); // 14
			vpd.put("var16", varOList.get(i).getString("INS_LEVEL_NAME")); // 15
			vpd.put("var17", varOList.get(i).getString("LATEST_RECTIFY_DATE")); // 16
			vpd.put("var18", varOList.get(i).getString("RECTIFY_SUGGEST")); // 17
			vpd.put("var19", varOList.get(i).getString("STATUS")); // 18
			// 摆渡日期
			vpd.put("var20", DateUtil.getTime()); // 19
			varList.add(vpd);
		}
		// 更新状态
		String ids = String.join(",", WARRANTY_IDS);
		String idsArray[] = ids.split(",");
		inssysService.UpdateSync(idsArray);
		// 返回Excel实例
		dataMap.put("varList", varList);
		DataFerryObj erv = new DataFerryObj();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 统计与导出
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/insExport")
	@RequiresPermissions("toExcel")
	public ModelAndView insExport() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String ABOUT = pd.getString("ABOUT");
		String STATUS = pd.getString("STATUS");
		if (ABOUT.equals("I")) {// 查询发起单位
			String RESPONSIBLE_UNIT = pd.getString("RESPONSIBLE_UNIT");
			RESPONSIBLE_UNIT = RESPONSIBLE_UNIT.replaceAll("AREA", "");
			System.out.println("部门号:" + RESPONSIBLE_UNIT);
			pd.put("INIT_DEPT", RESPONSIBLE_UNIT);
			pd.remove("RESPONSIBLE_UNIT");// 移除责任单位属性
		}
		if (ABOUT.equals("ALL")) {// 查询全部
			pd.remove("RESPONSIBLE_UNIT");// 移除责任单位属性
		}
		if (Tools.notEmpty(STATUS)) {
			if (STATUS.equals("outdate")) {// 超时未改
				pd.put("STATUS", "1");
				pd.put("LATEST_RECTIFY_DATE", DateUtil.getDay());
			}
		}
		// 构造Excel
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("序号"); // 0
		titles.add("隐患类别"); // 1
		titles.add("排查人"); // 2
		titles.add("排查时间"); // 3
		titles.add("隐患区域"); // 4
		titles.add("承包商单位"); // 5
		titles.add("隐患描述"); // 6
		titles.add("原因分析"); // 7
		titles.add("整改措施"); // 8
		titles.add("整改责任人"); // 9
		titles.add("整改监督人"); // 10
		titles.add("整改前照片"); // 11
		titles.add("整改后照片"); // 12
		titles.add("整改期限"); // 13
		titles.add("验收时间"); // 14
		titles.add("验收人"); // 15
		titles.add("罚款金额"); // 16
		titles.add("是否上交"); // 17
		dataMap.put("titles", titles);
		List<PageData> varOList = inssysService.DataStatisticsExport(pd);
		List<PageData> varList = new ArrayList<PageData>();
		List<String> beforeImg = new ArrayList<String>();
		List<String> afterImg = new ArrayList<String>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", Integer.toString(i + 1)); // 0
			vpd.put("var2", varOList.get(i).getString("INS_TYPE_L1_NAME")); // 1
			vpd.put("var3", varOList.get(i).getString("INIT_USER")); // 2
			try {
				DateFormat df = DateUtil.createDateFormat();
				vpd.put("var4", df.format(varOList.get(i).get("INIT_DATE"))); // 3
			} catch (Exception e) {
				vpd.put("var4", "");
			}
			vpd.put("var5", varOList.get(i).getString("AREA_NAME")); // 4
			vpd.put("var6", varOList.get(i).getString("CONTRACTOR")); // 5
			vpd.put("var7", varOList.get(i).getString("INS_DETIAL")); // 6
			vpd.put("var8", varOList.get(i).getString("REASON_ANALYSIS")); // 7
			vpd.put("var9", varOList.get(i).getString("RECTIFY_SUGGEST")); // 8
			vpd.put("var10", varOList.get(i).getString("TO_")); // 9
			vpd.put("var11", varOList.get(i).getString("CC_")); // 10
			vpd.put("var12", "整改前"); // 11
			vpd.put("var13", "整改后"); // 12
			vpd.put("var14", varOList.get(i).getString("LATEST_RECTIFY_DATE")); // 13
			try {
				DateFormat df = DateUtil.createDateFormat();
				vpd.put("var15", df.format(varOList.get(i).get("CLOSE_DATE"))); // 14
			} catch (Exception e) {
				vpd.put("var15", "");
			}
			//vpd.put("var16", varOList.get(i).getString("CLOSE_USER")); // 15
			vpd.put("var16", varOList.get(i).getString("INIT_USER")); // 15
			vpd.put("var17", "0"); // 16
			vpd.put("var18", "否"); // 17
			varList.add(vpd);
			// 获取整改前后照片
			PageData imgTempPd = new PageData();
			imgTempPd.put("PROC_INST_ID_", varOList.get(i).getString("PROC_INST_ID_"));
			// 查询整改前后照片
			List<PageData> BAIMGS = new ArrayList<PageData>();
			BAIMGS = insMediaService.getImgByProc(imgTempPd);
			if (BAIMGS.size() == 0) {// 没有照片
				beforeImg.add("NONE");
				afterImg.add("NONE");
			} else if (BAIMGS.size() == 1) {
				PageData one = BAIMGS.get(0);
				if (one.getString("NODE").equals("创建")) {
					beforeImg.add(one.getString("MEDIA_PATH"));
					afterImg.add("NONE");
				}
				if (one.getString("NODE").equals("整改")) {
					beforeImg.add("NONE");
					afterImg.add(one.getString("MEDIA_PATH"));
				}
			} else if (BAIMGS.size() == 2) {
				PageData one = BAIMGS.get(0);
				if (one.getString("NODE").equals("创建")) {
					beforeImg.add(one.getString("MEDIA_PATH"));
				}
				if (one.getString("NODE").equals("整改")) {
					afterImg.add(one.getString("MEDIA_PATH"));
				}
				PageData two = BAIMGS.get(1);
				if (two.getString("NODE").equals("创建")) {
					beforeImg.add(two.getString("MEDIA_PATH"));
				}
				if (two.getString("NODE").equals("整改")) {
					afterImg.add(two.getString("MEDIA_PATH"));
				}
			}
		}
		dataMap.put("varList", varList);
		dataMap.put("filename", pd.getString("FILENAME"));
		dataMap.put("beforeImg", beforeImg);
		dataMap.put("afterImg", afterImg);
		// 返回Excel实例
		InsExportToExcelObj erv = new InsExportToExcelObj();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 统计与导出
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/insExport2")
	@RequiresPermissions("toExcel")
	public ModelAndView DeptInsSubtotalsToExcel() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 查询部门隐患分类汇总
		List<PageData> resultList = inssysService.DeptInsSubtotals(pd);
		// 分类汇总处理：一维表转二维表
		Map<String, Object> returnMap = DeptInsSubtotals(resultList);
		dataMap.put("uniqueInsDept", returnMap.get("uniqueInsDept"));// 去重部门类型列表
		dataMap.put("uniqueInsType", returnMap.get("uniqueInsType"));// 去重隐患类型
		dataMap.put("rows", returnMap.get("rows"));// 数据行列表嵌套列表
		// 返回Excel实例
		InsExportToExcelObj2 erv = new InsExportToExcelObj2();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 部门隐患分类汇总
	 * 
	 * @param resultList<PageData> pd
	 * @return List<Map<String, Object>>
	 * @throws Exception
	 */
	public Map<String, Object> DeptInsSubtotals(List<PageData> resultList) {
		List<String> allInsDept = new ArrayList<String>();
		List<String> allInsType = new ArrayList<String>();
		List<String> insCount = new ArrayList<String>();
		PageData resultPd = new PageData();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<List<String>> rows = new ArrayList<List<String>>();
		for (PageData tempOne : resultList) {
			allInsDept.add(tempOne.getString("INS_DEPT"));
			allInsType.add(tempOne.getString("INS_TYPE"));
			insCount.add(tempOne.get("INS_COUNT").toString());
			resultPd.put(tempOne.getString("INS_DEPT") + tempOne.getString("INS_TYPE"),
					tempOne.get("INS_COUNT").toString());
		}
		// 部门和隐患类型去重
		List<String> uniqueInsDept = allInsDept.stream().distinct().collect(Collectors.toList());
		List<String> uniqueInsType = allInsType.stream().distinct().collect(Collectors.toList());
		returnMap.put("uniqueInsDept", uniqueInsDept);
		returnMap.put("uniqueInsType", uniqueInsType);
		// 构造数据表
		/***
		 * 类型A 类型B 类型C 部门a 2 3 8 部门b 1 2 9 部门c 11 9 5
		 */
		for (int i = 0; i < uniqueInsDept.size(); i++) {
			List<String> oneRow = new ArrayList<String>();
			for (int j = 0; j < uniqueInsType.size(); j++) {
				String value = "0";
				value = resultPd.getString(uniqueInsDept.get(i) + uniqueInsType.get(j));
				if (value.equals("null")) {
					oneRow.add("0");
				} else {
					oneRow.add(value);
				}
			}
			rows.add(oneRow);
		}
		returnMap.put("rows", rows);
		return returnMap;
	}
}
