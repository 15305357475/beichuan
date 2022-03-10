package org.fh.controller.sbxj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.fh.util.ObjectExcelView;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.flowable.engine.RuntimeService;
import org.fh.entity.PageData;
import org.fh.service.ins.InsSysService;
import org.fh.service.sbxj.SbxjMediaService;
import org.fh.service.sbxj.SbxjSysService;
import org.fh.service.system.DictionariesService;
import org.fh.service.system.FhsmsService;

/**
 * 说明：隐患排查主表 作者：fsci 时间：2021-04-13 授权：bsic
 */
@Controller
@RequestMapping("/sbxjssys")
public class SbxjSysController extends AcStartController {

	@Autowired
	private InsSysService inssysService;
	@Autowired
	private DictionariesService dicService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private FhsmsService fhsmsService;
	@Autowired
	private SbxjSysService SbxjSysService;
	@Autowired
	private SbxjMediaService SbxjMediaService;
	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
//		PageData pd_sb = new PageData();
//		pd_sb =this.getPageData();
		String xj_id = pd.getString("XJ_ID");
//		String sb_status = pd.getString("CHECK_CONCLUSION");
//		System.out.println(sb_status);
		// 设备状态为故障不可用时，设备表状态进行变更
//		if(sb_status.equals("3")) {
//			System.out.println("陈弢陈弢陈弢陈弢陈弢陈弢陈弢陈弢");
//			pd_sb.put("SB_NO", pd.getString("EQUIPMENT_ID"));
//			pd_sb.put("STATUS", "0");
//			SbxjSysService.updateStatus(pd_sb);
//		}
		pd.put("ID", xj_id);
		pd.put("CHECK_USER", Jurisdiction.getUsername()); // 发起人，当前系统用户
		pd.put("CHECK_DATE", DateUtil.getTime());// 发起时间
		pd.put("DEPT",Jurisdiction.getDEPARTMENT_ID());// 部门
		SbxjSysService.save(pd); // 记录存入数据库
		// 图片入库
		if (!pd.getString("INSMEDIA_I_PATHS").equals("NONE")) {
			String PATHS[] = pd.getString("SBXJMEDIA_I_PATHS").split(",");
			String NAMES[] = pd.getString("SBXJMEDIA_I_NAMES").split(",");
			List<String> INSMEDIA_IDs = new ArrayList<String>();
			int i = 0;
			for (String onePath : PATHS) {
				PageData tempPd = new PageData();
				String INSMEDIA_ID = this.get32UUID();
				tempPd.put("ID", INSMEDIA_ID);
				tempPd.put("XJ_ID", xj_id);
				tempPd.put("PROC_INST_ID_", "null");
				tempPd.put("NODE", "创建");
				tempPd.put("CREATE_USER", Jurisdiction.getUsername());
				tempPd.put("MEDIA_TYPE", "image/*");
				tempPd.put("MEDIA_NAME", NAMES[i]);
				tempPd.put("MEDIA_PATH", onePath);
				tempPd.put("STATUS", "1");
				i++;
			try {
				SbxjMediaService.save(tempPd);
				INSMEDIA_IDs.add(INSMEDIA_ID);
				} catch (Exception e) {
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
					audioEntity.put("ID", INSMEDIA_ID);
					audioEntity.put("XJ_ID", xj_id);
					audioEntity.put("PROC_INST_ID_", "null");
					audioEntity.put("NODE", "创建");
					audioEntity.put("CREATE_USER", Jurisdiction.getUsername());
					audioEntity.put("MEDIA_TYPE", "audio/*");
					audioEntity.put("MEDIA_NAME", pd.getString("SBXJMEDIA_A_NAME"));
					audioEntity.put("MEDIA_PATH", pd.getString("SBXJMEDIA_A_PATH"));
					audioEntity.put("STATUS", "1");
					try {
						SbxjMediaService.save(audioEntity);
					} catch (Exception e) {
						errInfo = "errer";
						map.put("errorMsg", e.getMessage());
						map.put("result", errInfo); // 返回结果
						return map;
					}
				}
				// 视频入库
				if (!pd.getString("INSMEDIA_V_PATH").equals("NONE")) {
					PageData audioEntity = new PageData();
					String INSMEDIA_ID = this.get32UUID();
					audioEntity.put("ID", INSMEDIA_ID);
					audioEntity.put("XJ_ID", xj_id);
					audioEntity.put("PROC_INST_ID_", "null");
					audioEntity.put("NODE", "创建");
					audioEntity.put("CREATE_USER", Jurisdiction.getUsername());
					audioEntity.put("MEDIA_TYPE", "video/*");
					audioEntity.put("MEDIA_NAME", pd.getString("SBXJMEDIA_V_NAME"));
					audioEntity.put("MEDIA_PATH", pd.getString("SBXJMEDIA_V_PATH"));
					audioEntity.put("STATUS", "1");
					try {
						SbxjMediaService.save(audioEntity);
					} catch (Exception e) {
						errInfo = "errer";
						map.put("errorMsg", e.getMessage());
						map.put("result", errInfo); // 返回结果
						return map;
					}
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
//	@RequiresPermissions("inssys:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		SbxjSysService.edit(pd);
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
	// @RequiresPermissions("inssys:list")
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
			pd.put("STATUS", STATUS.trim());
		}
		// 判断相关性
		if (Tools.notEmpty(ABOUT)) {
			switch (ABOUT) {
			case "my":// 发起
				pd.put("INIT_USER", Jurisdiction.getUsername());
				break;
			case "tome":// 主送
				pd.put("TO_", Jurisdiction.getUsername());
				break;
			case "ccme":// 抄送
				pd.put("CC_", Jurisdiction.getUsername());
				break;
			case "all":// 全部
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
	 * 去修改页面获取数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
//	@RequiresPermissions("inssys:edit")
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
//	@RequiresPermissions("inssys:del")
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
		titles.add("发起人"); // 1
		titles.add("发起日期"); // 2
		titles.add("隐患实例ID"); // 3
		titles.add("隐患区域"); // 4
		titles.add("责任单位"); // 5
		titles.add("主送人或主送角色"); // 6
		titles.add("抄送人或抄送角色"); // 7
		titles.add("隐患类别(问题定性)"); // 8
		titles.add("作业类型"); // 9
		titles.add("隐患描述"); // 10
		titles.add("隐患级别"); // 11
		titles.add("最初整改日期"); // 12
		titles.add("整改建议或要求"); // 13
		dataMap.put("titles", titles);
		List<PageData> varOList = inssysService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("INIT_USER")); // 1
			vpd.put("var2", varOList.get(i).getString("INIT_DATE")); // 2
			vpd.put("var3", varOList.get(i).getString("PROC_INST_ID_")); // 3
			vpd.put("var4", varOList.get(i).getString("AREA")); // 4
			vpd.put("var5", varOList.get(i).getString("RESPONSIBLE_UNIT")); // 5
			vpd.put("var6", varOList.get(i).getString("TO_")); // 6
			vpd.put("var7", varOList.get(i).getString("CC_")); // 7
			vpd.put("var8", varOList.get(i).getString("INS_TYPE")); // 8
			vpd.put("var9", varOList.get(i).getString("WORK_TYPE")); // 9
			vpd.put("var10", varOList.get(i).getString("INS_DETIAL")); // 10
			vpd.put("var11", varOList.get(i).getString("INS_LEVEL")); // 11
			vpd.put("var12", varOList.get(i).getString("LATEST_RECTIFY_DATE")); // 12
			vpd.put("var13", varOList.get(i).getString("RECTIFY_SUGGEST")); // 13
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
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
		List<PageData> varList = SbxjMediaService.findByProc(pd); // 列出媒体列表;
		if (varList.size() > 0) {
			// 将每一条记录转码成base64
			List<String> base64ListI = new ArrayList<String>();// 图片base64
			List<String> base64ListA = new ArrayList<String>();// 音频base64
			List<String> base64ListV = new ArrayList<String>();// 音频base64
			List<PageData> varListI = new ArrayList<PageData>();// 图片属性信息列表
			List<PageData> varListA = new ArrayList<PageData>();// 音频属性信息列表
			List<PageData> varListV = new ArrayList<PageData>();// 音频属性信息列表
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
				if (pageData.getString("MEDIA_TYPE").equals("video/*")) {
					base64ListV.add(Base64String);
					varListV.add(pageData);
				}
			}
			map.put("base64ListI", base64ListI);
			map.put("base64ListA", base64ListA);
			map.put("base64ListV", base64ListV);
			map.put("varListI", varListI);
			map.put("varListA", varListA);
			map.put("varListV", varListV);
		}
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 待办提醒
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/getSbxjTaskCount")
	@ResponseBody
	public Object getSbxjTaskCount() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("NEXT_USER", Jurisdiction.getUsername());
		List<PageData> varList = SbxjSysService.getSbxjTaskCount(pd); 
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}
	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/addQuestion")
	@ResponseBody
	public Object addQuestion() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String id = this.get32UUID();
		pd.put("ID", id);
		pd.put("STATUS", "0");
		pd.put("START_DATE", DateUtil.getTime());// 发起时间
		SbxjSysService.addQuestion(pd); // 记录存入数据库
		// 图片入库
		if (!pd.getString("INSMEDIA_I_PATHS").equals("NONE")) {
			String PATHS[] = pd.getString("SBXJMEDIA_I_PATHS").split(",");
			String NAMES[] = pd.getString("SBXJMEDIA_I_NAMES").split(",");
			List<String> INSMEDIA_IDs = new ArrayList<String>();
			int i = 0;
			for (String onePath : PATHS) {
				PageData tempPd = new PageData();
				String INSMEDIA_ID = this.get32UUID();
				tempPd.put("ID", INSMEDIA_ID);
				tempPd.put("XJ_ID", id);
				tempPd.put("PROC_INST_ID_", "null");
				tempPd.put("NODE", "创建");
				tempPd.put("CREATE_USER", Jurisdiction.getUsername());
				tempPd.put("MEDIA_TYPE", "image/*");
				tempPd.put("MEDIA_NAME", NAMES[i]);
				tempPd.put("MEDIA_PATH", onePath);
				tempPd.put("STATUS", "1");
				i++;
			try {
				SbxjMediaService.save(tempPd);
				INSMEDIA_IDs.add(INSMEDIA_ID);
				} catch (Exception e) {
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
					audioEntity.put("ID", INSMEDIA_ID);
					audioEntity.put("XJ_ID", id);
					audioEntity.put("PROC_INST_ID_", "null");
					audioEntity.put("NODE", "创建");
					audioEntity.put("CREATE_USER", Jurisdiction.getUsername());
					audioEntity.put("MEDIA_TYPE", "audio/*");
					audioEntity.put("MEDIA_NAME", pd.getString("SBXJMEDIA_A_NAME"));
					audioEntity.put("MEDIA_PATH", pd.getString("SBXJMEDIA_A_PATH"));
					audioEntity.put("STATUS", "1");
					try {
						SbxjMediaService.save(audioEntity);
					} catch (Exception e) {
						errInfo = "errer";
						map.put("errorMsg", e.getMessage());
						map.put("result", errInfo); // 返回结果
						return map;
					}
				}
				// 视频入库
				if (!pd.getString("INSMEDIA_V_PATH").equals("NONE")) {
					PageData audioEntity = new PageData();
					String INSMEDIA_ID = this.get32UUID();
					audioEntity.put("ID", INSMEDIA_ID);
					audioEntity.put("XJ_ID", id);
					audioEntity.put("PROC_INST_ID_", "null");
					audioEntity.put("NODE", "创建");
					audioEntity.put("CREATE_USER", Jurisdiction.getUsername());
					audioEntity.put("MEDIA_TYPE", "video/*");
					audioEntity.put("MEDIA_NAME", pd.getString("SBXJMEDIA_V_NAME"));
					audioEntity.put("MEDIA_PATH", pd.getString("SBXJMEDIA_V_PATH"));
					audioEntity.put("STATUS", "1");
					try {
						SbxjMediaService.save(audioEntity);
					} catch (Exception e) {
						errInfo = "errer";
						map.put("errorMsg", e.getMessage());
						map.put("result", errInfo); // 返回结果
						return map;
					}
				}
				map.put("QUESTION_ID",id);
				map.put("result", errInfo);
				return map;
	}

}
