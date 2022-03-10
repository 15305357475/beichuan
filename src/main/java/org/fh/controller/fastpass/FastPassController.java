package org.fh.controller.fastpass;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.fastpass.FastPassService;
import org.fh.service.system.UsersService;
import org.fh.service.fastpass.FastPassMediaService;
import org.fh.service.fastpass.FastPassApprovalService;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.FileUpload;
import org.fh.util.ImageAnd64Binary;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 说明：申请 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/FastPass")
public class FastPassController extends AcStartController {

	@Autowired
	private FastPassService FastPassService;
	@Autowired
	private FastPassMediaService FastPassMediaService;
	@Autowired
	private FastPassApprovalService FastPassApprovalService;
	@Autowired
	private UsersService usersService;
	/**
	 * 保存单
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	//@RequiresPermissions("reimbursement:add")
	@ResponseBody
	public Object add() {
		// 步骤0：接收前端数据
		Map<String, Object> zmap = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 步骤1：构造主表数据，并存入主表
		pd.put("user_id", Jurisdiction.getUsername()); // 发起人，当前系统用户
		pd.put("state", "1");// 发起状态
		pd.put("department", Jurisdiction.getUSER_DEPT());
		pd.put("committime", DateUtil.getTime());// 提交日期
		String code = String.valueOf(System.currentTimeMillis());//13位时间戳
		code = code.substring(7, code.length());//取8-13位
		String process_id = this.get32UUID()+"&"+code;//拼接uuid和时间戳作为流程id
		pd.put("process_id",process_id);
		try {
			FastPassService.save(pd); // 记录存入数据库
		} catch (Exception e) {
			errInfo = "error";
			zmap.put("errorMsg", e.getMessage());
		}
		// 步骤2：构造文字数据，并写入到媒体表
		String items = pd.getString("items");
		// 字符串转对象数组
		JSONArray json = JSONArray.fromObject(items);
		for (int i = 0; i < json.size(); i++) {
			JSONObject temp = json.getJSONObject(i);//取第i个obj
			PageData mediaPd = new PageData();
			mediaPd.put("id",this.get32UUID());
			mediaPd.put("process_id", process_id);
			mediaPd.put("media_type","TEXT");//path是？
			mediaPd.put("media_path",temp.get("name").toString() + "⊕" +  temp.get("dw").toString() + "⊕" + temp.get("sl").toString());
			mediaPd.put("status","1");
			try {
				FastPassMediaService.save(mediaPd);
			} catch (Exception e) {
				errInfo = "error";
				zmap.put("errorMsg", e.getMessage());
			}
		}
		// 步骤3：构造图片数据，并写入到媒体表
		String path = pd.getString("path");
		String[] result = path.split(",");
		for (String r : result) {
			PageData mediaPh = new PageData();
			mediaPh.put("id",this.get32UUID());
			mediaPh.put("process_id", process_id);
			mediaPh.put("media_type","IMAGE");
			mediaPh.put("media_path", r);
			mediaPh.put("status","1");
			try {
				FastPassMediaService.save(mediaPh);
			} catch (Exception e) {
				errInfo = "error";
				zmap.put("errorMsg", e.getMessage());
		}
		}
		// 步骤4：构造任务数据，并写入任务表
		// 4.1 构造发起数据，并写入任务表 我→我
		PageData mission0 = new PageData();
		mission0.put("node", "发起流程");
		mission0.put("process_id", process_id);//是全局的，不用联表查
		mission0.put("create_user", Jurisdiction.getUsername());
		mission0.put("create_suggest", "发起流程");
		mission0.put("approval_user", Jurisdiction.getUsername());
		mission0.put("create_time", DateUtil.getTime());
		mission0.put("approval_suggest", "发起");
		mission0.put("approval_time", DateUtil.getTime());
		mission0.put("status", "2");
		mission0.put("parent_node", "0");
		String mission0ID = "";
		try {
			FastPassApprovalService.save(mission0);
			// 反查出来missionID
			List<PageData> now_mission = FastPassApprovalService.findByProcessId(mission0);
			if(now_mission.size() == 1) {
				mission0ID = now_mission.get(0).get("id").toString();
			}
		} catch (Exception e) {
			errInfo = "error";
			zmap.put("errorMsg", e.getMessage());
		}
		// 4.2 构造任务数据，并写入任务表 我→分管部长
		PageData mission1 = new PageData();
		mission1.put("node", "分管领导审批");
		mission1.put("process_id", process_id);
		mission1.put("create_user", Jurisdiction.getUsername());
		mission1.put("create_suggest", "发起流程");
		mission1.put("approval_user", pd.getString("ASSIGNEE_"));
		mission1.put("create_time", DateUtil.getTime());
		mission1.put("approval_suggest","");
		mission1.put("status", "1");
		mission1.put("parent_node", mission0ID);
		try {
			FastPassApprovalService.save(mission1);
		} catch (Exception e) {
			errInfo = "error";
			zmap.put("errorMsg", e.getMessage());
		}
		zmap.put("code",process_id);
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
	//@RequiresPermissions("reimbursement:list")
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
		pd.put("USERNAME", Jurisdiction.getUsername());
		page.setPd(pd);
		List<PageData> varList = FastPassService.list(page); 
		map.put("varLists", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}
	
	/**
	 * 已办任务/待办任务列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/littlelist")
	//@RequiresPermissions("reimbursement:list")
	@ResponseBody
	public Object littlelist(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS)) {
			pd.put("keywords", KEYWORDS.trim());
		}
		String STATUS = pd.getString("STATUS");
		if(STATUS.equals("1")) {
			pd.put("db", "db");//待办
			pd.put("USERNAME", Jurisdiction.getUsername());
		}else {
			pd.put("yb", "yb");//已办
			pd.put("USERNAME", Jurisdiction.getUsername());
		}
		page.setPd(pd);
		List<PageData> varList = FastPassApprovalService.list(page); 
		map.put("varLists", varList);
		map.put("page", page);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 删除
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("reimbursement:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "删除成功";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = FastPassService.findById(pd);
		// 身份验证
		String commitUser = pd.getString("user_id");
		String userName = Jurisdiction.getUsername();
		if(userName.equals(commitUser)) {
			pd.put("state", "0");
			FastPassService.edit(pd);
			PageData pd2 = new PageData();
			pd2.put("process_id", pd.getString("process_id"));
			List<PageData> aprovalList = FastPassApprovalService.findByProcessId(pd2);
			for (PageData temp : aprovalList) {
				temp.put("status", "0");
				FastPassApprovalService.edit(temp);
			}
		}else {
			errInfo = "您不是该流程的创建人，不能删除！";
		}
		map.put("errInfo", errInfo);
		map.put("result", "success");
		return map;
	}
	
	/**
	 * 去修改页面获取数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/findById")
	//@RequiresPermissions("insmedia:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = FastPassService.findById(pd); // 根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**
	 * 去修改页面获取数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/findByCode")
	//@RequiresPermissions("insmedia:edit")
	@ResponseBody
	public Object findSubID() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		pdList = FastPassService.findByCode(pd); // 根据code(六位id)读取搜完整id
		if(pdList.size()!= 0){
			if(pdList.size()>1) {
			errInfo="无法确定唯一出厂证，请扫码出厂";
		}
		}else {
			errInfo="无法查询到出门证";
		}
		map.put("pd", pdList);
		map.put("result", errInfo);
		return map;
	}
	
	

	/**
	 * 获取媒体数据-纯文字类
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getMediaDataById")
	//@RequiresPermissions("insmedia:edit")
	@ResponseBody
	public Object getMediaDataById() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		pdList = FastPassMediaService.findById(pd); // 根据ID读取
		map.put("pdList", pdList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 获取媒体数据--图类
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getMediaDataById2")
	//@RequiresPermissions("insmedia:edit")
	@ResponseBody
	public Object getMediaDataById2() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();//pdlist里放固定为<PageData>类型的值；ArrayList是List的一个实现类，List本身是一个虚类
		pdList = FastPassMediaService.findById(pd); // 根据ID读取
		List<String> base64List = new ArrayList<String>();//这个list将用来放一个一个的base64编码字符串
		// base64编码图片
		for (PageData pageData : pdList) {
			// 拿到图片路径
			String photoPath = pageData.getString("media_path");
			// 编码成base64
			String Base64String = ImageAnd64Binary.getImageStr(PathUtil.getProjectpath() + photoPath);//先加上前面的本地路径，在通过地址找到图片进行64编码
			// 记录下这个路径
			base64List.add(Base64String);//加到list里
		}
		map.put("varListI", base64List);
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
	//@RequiresPermissions("reimbursement:del")
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
				now = FastPassService.findById(now);
				now.put("STATUS", "0");
				FastPassService.edit(now);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}
	
	/**
	 * 审批节点数据回显
	 *
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/approvalNode")
	@ResponseBody
	public Object approvalNode() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> nodeList = FastPassApprovalService.findByProcessId(pd);
		map.put("nodeList", nodeList); 
		map.put("result", errInfo); // 返回结果
		return map;
	}
	
	
	/**
	 * 上传图片
	 */
	@RequestMapping(value = "/uploadImgs")
	@ResponseBody
	public Object uploadImgs(HttpServletResponse response, MultipartHttpServletRequest request) throws Exception {
		int fileCount = Integer.parseInt(request.getParameter("fileCount"));
		List<MultipartFile> MultipartFiles = new ArrayList<MultipartFile>();
		for (int i = 0; i < fileCount; i++) {
			MultipartFiles.add(request.getFile("file[" + i + "]"));
		}

		List<String> INSMEDIA_PATHS = new ArrayList<String>(); // 附件路径
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		// 获取当前年月
		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		// 构造存储路径
		String filePath = PathUtil.getProjectpath() + Const.FASTPASS_IMGS + year + "//" + month + "//"; // 文件上传绝对路径
		String shortFilePath = Const.FASTPASS_IMGS + year + "//" + month + "//"; // 文件上传相对路径
		// 初始化变量
		String fileName = "";
		if (null != MultipartFiles && MultipartFiles.size() > 0) {
			for (MultipartFile file : MultipartFiles) {
				fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
				INSMEDIA_PATHS.add(shortFilePath + fileName);
			}
		} else {
			errInfo = "error";
		}
		// 返回结果
		map.put("result", errInfo);
		map.put("INSMEDIA_PATHS", INSMEDIA_PATHS);
		return map;
	}
	
	

	/**
	 * 创建人撤销
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/withDraw")
	@ResponseBody
	public Object withDraw() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errorInfo = "撤销完成";
		// 验证权限
		String userName = Jurisdiction.getUsername();
		PageData mission = FastPassApprovalService.findById(pd);
		if(mission!=null) {
			if(mission.getString("create_user").equals(userName)) {
				// 主表状态更新为6
				PageData main = FastPassService.findById(pd);
				main.put("state", "6");
				FastPassService.edit(main);
			}else {
				errorInfo = "不在您的审批节点！";
			}
		}else {
			errorInfo = "参数不正确！";
		}
		map.put("errorInfo", errorInfo);
		map.put("result", "success");
		return map;
	}
	
	
	/**
	 * 驳回
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/goReturn")
	@ResponseBody
	public Object goReturn() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errorInfo = "驳回完成";
		// 验证权限
		String userName = Jurisdiction.getUsername();
		PageData mission = FastPassApprovalService.findById(pd);
		if(mission!=null) {
			if(mission.getString("approval_user").equals(userName)) {
				// 主表状态更新为2
				PageData main = FastPassService.findById(pd);
				main.put("state", "2");
				FastPassService.edit(main);
				// 变更任务表信息
				mission.put("approval_suggest",pd.getString("suggest"));
				mission.put("approval_time",DateUtil.getTime());
				mission.put("status","3");
				FastPassApprovalService.edit(mission);
			}else {
				errorInfo = "不在您的审批节点！";
			}
		}else {
			errorInfo = "参数不正确！";
		}
		map.put("errorInfo", errorInfo);
		map.put("result", "success");
		return map;
	}
	
	/**
	 * 门卫驳回
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/guardGoReturn")
	@ResponseBody
	public Object guardGoReturn() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errorInfo = "驳回完成";
		String userName = Jurisdiction.getUsername();
		PageData mission = FastPassService.findById(pd);
		if(mission!=null) {
				// 主表状态更新为5：门卫拦截
				PageData main = FastPassService.findById(pd);
				main.put("state", "5");
				main.put("door", pd.getString("door"));
				FastPassService.edit(main);
				// 变更任务表信息
				// 构造一个放行数据，写入流程表
				PageData mission0 = new PageData();
				mission0.put("node", "门卫放行");
				mission0.put("process_id", pd.getString("process_id"));
				mission0.put("create_user", userName);
				mission0.put("create_suggest", "门卫处理");
				mission0.put("create_time", DateUtil.getTime());
				mission0.put("approval_user", userName);
				mission0.put("approval_suggest", pd.getString("suggest"));
				mission0.put("approval_time", DateUtil.getTime());
				mission0.put("status", "2");
				FastPassApprovalService.save(mission0);
		}else {
			errorInfo = "参数不正确！";
		}
		map.put("errorInfo", errorInfo);
		map.put("result", "success");
		return map;
	}
	
	/**
	 * 门卫批准
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/guardGoPass")
	@ResponseBody
	public Object guardGoPass() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errorInfo = "已放行";
		String userName = Jurisdiction.getUsername();
		PageData main = FastPassService.findById(pd);
		// 第一步：主表状态修改为
		main.put("state","4");
		main.put("door", pd.getString("door"));
		FastPassService.edit(main);
		// 构造一个放行数据，写入流程表
		PageData mission0 = new PageData();
		mission0.put("node", "门卫放行");
		mission0.put("process_id", pd.getString("process_id"));
		mission0.put("create_user", userName);
		mission0.put("create_suggest", "门卫处理");
		mission0.put("create_time", DateUtil.getTime());
		mission0.put("approval_user", userName);
		mission0.put("approval_suggest", "门卫放行");
		mission0.put("approval_time", DateUtil.getTime());
		mission0.put("status", "2");
		FastPassApprovalService.save(mission0);
		map.put("errorInfo", errorInfo);
		map.put("result", "success");
		return map;
	}
	
	/**
	 * 批准
	 *
	 * @throws Exception
	 */
	@RequestMapping(value = "/goPass")
	@ResponseBody
	public Object goPass() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errorInfo = "审批完成";
		String nextNode = pd.getString("nextNode");
		String userName = Jurisdiction.getUsername();
		PageData mission = FastPassApprovalService.findById(pd);
		// 查询保卫部审批节点的所有用户
		List<PageData> group = usersService.getAllMember(pd);
		if(group == null) {
			errorInfo = "保卫部审批节点未配置相关人员!";
		}else {
			List<String> approvalGroup = new ArrayList<String>();
			for (PageData temp : group) {
				approvalGroup.add(temp.getString("USERNAME"));
			}
			// 判定下一个节点是什么
			if(nextNode.equals("保卫部审批")) {
				if(mission.getString("approval_user").equals(userName)) {
					// 第一步：结束掉当前的任务
					mission.put("approval_suggest",pd.getString("approval_suggest"));
					mission.put("approval_time",DateUtil.getTime());
					mission.put("status","2");
					FastPassApprovalService.edit(mission);
					String missionID = "";
					missionID = mission.get("id").toString();
					// 第二步：给下一个节点创建新任务
					PageData mission1 = new PageData();
					mission1.put("node", nextNode);
					mission1.put("process_id", pd.getString("process_id"));
					mission1.put("create_user", mission.getString("create_user"));
					mission1.put("create_suggest", "");
					mission1.put("approval_user", listToString(approvalGroup,','));
					mission1.put("create_time", DateUtil.getTime());
					mission1.put("approval_suggest","");
					mission1.put("status", "1");
					mission1.put("parent_node", missionID);
					FastPassApprovalService.save(mission1);
				}else {
					errorInfo = "不在您的审批节点！";
				}		
			}
			else if(nextNode.equals("门卫放行")) {
				if(approvalGroup.indexOf(userName) != -1) {// 处理批准逻辑
					// 步骤1：结束当前任务
					mission.put("approval_suggest","[" + userName + "]" + pd.getString("approval_suggest"));
					mission.put("approval_time",DateUtil.getTime());
					mission.put("status","2");
					FastPassApprovalService.edit(mission);
					// 步骤2：主表状态更新为3
					PageData main = FastPassService.findById(pd);
					main.put("state", "3");
					FastPassService.edit(main);
				}else {
					errorInfo = "不在您的审批节点！";
				}
			}
		}
		map.put("errorInfo", errorInfo);
		map.put("result", "success");
		return map;
	}
	
	/**
	 * List转String
	 * 
	 * @param list      列表
	 * @param separator 分隔符
	 * @return String 字符串
	 * @throws UnsupportedEncodingException
	 */
	public String listToString(List<String> list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}
}
