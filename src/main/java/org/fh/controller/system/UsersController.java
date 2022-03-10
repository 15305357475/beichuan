package org.fh.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.fh.controller.base.BaseController;
import org.fh.entity.system.Role;
import org.fh.service.fhoa.DepartmentService;
import org.fh.service.fhoa.RegisteredService;
import org.fh.service.fhoa.StaffService;
import org.fh.service.fhoa.DatajurService;
import org.fh.service.system.FHlogService;
import org.fh.service.system.RoleService;
import org.fh.service.system.UeditorService;
import org.fh.service.system.UsersService;
import org.fh.util.*;
/*import org.fh.util.GetPinyin;*/
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Registered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 说明：系统用户处理类 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/user")
public class UsersController extends BaseController {

	@Autowired
	private UsersService usersService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UeditorService ueditorService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RegisteredService registeredService;
	@Autowired
	private StaffService staffService;
	@Autowired
	private DatajurService datajurService;
	@Autowired
	private FHlogService FHLOG;

	/**
	 * 用户列表
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/list")
	@RequiresPermissions("user:list")
	@ResponseBody
	public Object listUsers(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();

		/* 检索条件 */
		String ROLE_ID = pd.getString("ROLE_ID"); // 角色ID
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());
		String STRARTTIME = pd.getString("STRARTTIME"); // 开始时间
		String ENDTIME = pd.getString("ENDTIME"); // 结束时间
		if (Tools.notEmpty(STRARTTIME))
			pd.put("STRARTTIME", STRARTTIME + " 00:00:00");
		if (Tools.notEmpty(ENDTIME))
			pd.put("ENDTIME", ENDTIME + " 00:00:00");

		page.setPd(pd);
		List<PageData> userList = usersService.userlistPage(page); // 列出用户列表
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有系统用户角色

		map.put("userList", userList);
		map.put("roleList", roleList);
		map.put("ROLE_ID", ROLE_ID);
		map.put("page", page);
		map.put("pd", pd);

		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去新增用户页面
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAddUser")
	@RequiresPermissions("user:add")
	@ResponseBody
	public Object goAddUser() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有功能角色
		PageData pd2 = new PageData();
		pd2.put("ROLE_ID", "2");
		List<Role> roleList_sp_2 = roleService.listAllRolesByPId(pd2); // 列出所有审批角色--作业审批
		PageData pd3 = new PageData();
		pd3.put("ROLE_ID", "3");
		List<Role> roleList_sp_3 = roleService.listAllRolesByPId(pd3); // 列出所有审批角色--保修流程
		PageData pd4 = new PageData();
		pd4.put("ROLE_ID", "4");
		List<Role> roleList_sp_4 = roleService.listAllRolesByPId(pd4); // 列出所有审批角色--隐患排查
		PageData pd5 = new PageData();
		pd5.put("ROLE_ID", "5");
		List<Role> roleList_sp_5 = roleService.listAllRolesByPId(pd5); // 列出所有审批角色--预留1
		PageData pd6 = new PageData();
		pd6.put("ROLE_ID", "6");
		List<Role> roleList_sp_6 = roleService.listAllRolesByPId(pd6); // 列出所有审批角色--预留2
		PageData pd7 = new PageData();
		pd7.put("ROLE_ID", "7");
		List<Role> roleList_sp_7 = roleService.listAllRolesByPId(pd7); // 列出所有审批角色--预留3
		List<Role> roleList_sp = new ArrayList<Role>();
		roleList_sp.addAll(roleList_sp_2);
		roleList_sp.addAll(roleList_sp_3);
		roleList_sp.addAll(roleList_sp_4);
		roleList_sp.addAll(roleList_sp_5);
		roleList_sp.addAll(roleList_sp_6);
		roleList_sp.addAll(roleList_sp_7);
		map.put("roleList", roleList);
		map.put("roleList_sp", roleList_sp);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去修改用户页面(从系统用户页面修改)
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEditUser")
	@RequiresPermissions("user:edit")
	@ResponseBody
	public Object goEditUser() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if ("1".equals(pd.getString("USER_ID"))) {
			return null;
		} // 不能修改admin用户
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有功能角色
		PageData pd2 = new PageData();
		pd2.put("ROLE_ID", "2");
		pd2.put("USER_ID", pd.getString("USER_ID"));
		List<Role> roleList_sp_2 = roleService.listAllRolesByPId(pd2); // 列出所有审批角色--作业审批
		PageData pd3 = new PageData();
		pd3.put("ROLE_ID", "3");
		List<Role> roleList_sp_3 = roleService.listAllRolesByPId(pd3); // 列出所有审批角色--保修流程
		PageData pd4 = new PageData();
		pd4.put("ROLE_ID", "4");
		List<Role> roleList_sp_4 = roleService.listAllRolesByPId(pd4); // 列出所有审批角色--隐患排查
		PageData pd5 = new PageData();
		pd5.put("ROLE_ID", "5");
		List<Role> roleList_sp_5 = roleService.listAllRolesByPId(pd5); // 列出所有审批角色--预留1
		PageData pd6 = new PageData();
		pd6.put("ROLE_ID", "6");
		List<Role> roleList_sp_6 = roleService.listAllRolesByPId(pd6); // 列出所有审批角色--预留2
		PageData pd7 = new PageData();
		pd7.put("ROLE_ID", "7");
		List<Role> roleList_sp_7 = roleService.listAllRolesByPId(pd7); // 列出所有审批角色--预留3
		pd = usersService.findById(pd); // 根据ID读取
		String ROLE_IDS = pd.getString("ROLE_IDS"); // 副职角色ID
		List<Role> roleList_sp = new ArrayList<Role>();
		roleList_sp.addAll(roleList_sp_2);
		roleList_sp.addAll(roleList_sp_3);
		roleList_sp.addAll(roleList_sp_4);
		roleList_sp.addAll(roleList_sp_5);
		roleList_sp.addAll(roleList_sp_6);
		roleList_sp.addAll(roleList_sp_7);
		if (Tools.notEmpty(ROLE_IDS)) {
			String arryROLE_ID[] = ROLE_IDS.split(",");
			for (int i = 0; i < roleList_sp.size(); i++) {
				Role role = roleList_sp.get(i);
				String roleId = role.getROLE_ID();
				for (int n = 0; n < arryROLE_ID.length; n++) {
					if (arryROLE_ID[n].equals(roleId)) {
						role.setRIGHTS("1"); // 此时的目的是为了修改用户信息上，能看到审批角色都有哪些
						break;
					}
				}
			}
		}
		map.put("pd", pd);
		map.put("roleList", roleList);
		map.put("roleList_sp", roleList_sp);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去修改用户页面(个人资料修改)
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEditMyInfo")
	@ResponseBody
	public Object goEditMyInfo() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有系统用户角色
		pd.put("USERNAME", Jurisdiction.getUsername());
		pd = usersService.findByUsername(pd); // 根据用户名读取
		map.put("pd", pd);
		map.put("roleList", roleList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 修改用户(系统用户列表修改)
	 */
	@RequestMapping(value = "/editUser")
	@RequiresPermissions("user:edit")
	@ResponseBody
	public Object editUser() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		FHLOG.save(Jurisdiction.getUsername(), "从系统用户中修改" + pd.getString("USERNAME") + "的资料"); // 记录日志
		if (!Jurisdiction.getUsername().equals(pd.getString("USERNAME"))) { // 如果当前登录用户修改用户资料提交的用户名非本人
			if ("admin".equals(pd.getString("USERNAME")) && !"admin".equals(Jurisdiction.getUsername())) {
				return null;
			} // 非admin用户不能修改admin
		} else { // 如果当前登录用户修改用户资料提交的用户名是本人，则不能修改本人的角色ID
			PageData upd = new PageData();
			upd = usersService.findByUsername(pd);
			pd.put("ROLE_ID", upd.getString("ROLE_ID")); // 对角色ID还原本人角色ID
			pd.put("ROLE_IDS", Tools.notEmpty(upd.getString("ROLE_IDS")) ? upd.get("ROLE_IDS") : ""); // 对角色ID还原本人副职角色ID
		}
		if (pd.getString("PASSWORD") != null && !"".equals(pd.getString("PASSWORD"))) {
			pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());
		}
		usersService.editUser(pd); // 执行修改
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 修改用户(个人资料修改)
	 */
	@RequestMapping(value = "/editUserOwn")
	@ResponseBody
	public Object editUserOwn() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if (!Jurisdiction.getUsername().equals(pd.getString("USERNAME"))) { // 如果当前登录用户修改用户资料提交的用户名非本人
			FHLOG.save(Jurisdiction.getUsername(), "恶意修改用户资料：" + pd.getString("USERNAME"));
			return null;// 不能修改非本人的资料
		} else { // 如果当前登录用户修改用户资料提交的用户名是本人，则不能修改本人的角色ID
			PageData upd = new PageData();
			upd = usersService.findByUsername(pd);
			pd.put("USER_ID", Integer.parseInt(upd.get("USER_ID").toString())); // 对ID还原本人ID，防止串改
			pd.put("ROLE_ID", upd.getString("ROLE_ID")); // 对角色ID还原本人角色ID
			pd.put("ROLE_IDS", Tools.notEmpty(upd.getString("ROLE_IDS")) ? upd.get("ROLE_IDS") : ""); // 对角色ID还原本人副职角色ID
		}
		if (pd.getString("PASSWORD") != null && !"".equals(pd.getString("PASSWORD"))) {
			pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());
		}
		usersService.editUser(pd); // 执行修改
		FHLOG.save(Jurisdiction.getUsername(), "从个人资料中修改" + pd.getString("USERNAME") + "的资料"); // 记录日志
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 判断用户名是否存在
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hasUser")
	@ResponseBody
	public Object hasUser() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if (usersService.findByUsername(pd) != null) {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 判断邮箱是否存在
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hasEmail")
	@ResponseBody
	public Object hasEmail() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if (usersService.findByEmail(pd) != null) {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 判断编码是否存在
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hasNumber")
	@ResponseBody
	public Object hasNumber() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if (usersService.findByNumbe(pd) != null) {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 保存用户
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveUser")
	@RequiresPermissions("user:add")
	@ResponseBody
	public Object saveUser() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// pd.put("USER_ID", this.get32UUID()); //ID 主键
		if (null == pd.getString("USER_ID") || pd.getString("USER_ID").equals("")) {
			pd.remove("USER_ID");
			// pd.put("USER_ID", "");
		}
		pd.put("LAST_LOGIN", ""); // 最后登录时间
		pd.put("IP", ""); // IP
		pd.put("STATUS", "0"); // 状态
		pd.put("SKIN", "pcoded-navbar navbar-image-3,navbar pcoded-header navbar-expand-lg navbar-light header-dark,"); // 用户默认皮肤
		pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString()); // 密码加密
		if (null == usersService.findByUsername(pd)) { // 判断用户名是否存在
			usersService.saveUser(pd); // 执行保存
		} else {
			map.put("result", "failed");
		}
		FHLOG.save(Jurisdiction.getUsername(), "新增用户：" + pd.getString("USERNAME")); // 记录日志
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 删除用户
	 * 
	 * @return
	 */
	@RequestMapping(value = "/deleteUser")
	@RequiresPermissions("user:del")
	@ResponseBody
	public Object deleteUser() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		String errInfo = "success";
		pd = this.getPageData();
		FHLOG.save(Jurisdiction.getUsername(), "删除用户ID：" + pd.getString("USER_ID")); // 记录日志
		usersService.deleteUser(pd); // 删除用户
		ueditorService.delete(pd); // 删除副文本关联数据
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 批量删除
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAllUser")
	@RequiresPermissions("user:del")
	@ResponseBody
	public Object deleteAllUser() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		String USER_IDS = pd.getString("USER_IDS");
		String errInfo = "success";
		if (Tools.notEmpty(USER_IDS)) {
			String ArrayUSER_IDS[] = USER_IDS.split(",");
			FHLOG.save(Jurisdiction.getUsername(), "批量删除用户"); // 记录日志
			usersService.deleteAllUser(ArrayUSER_IDS); // 删除用户
			ueditorService.deleteAll(ArrayUSER_IDS); // 删除副文本关联数据
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 导出用户信息到EXCEL
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	@RequiresPermissions("toExcel")
	public ModelAndView exportExcel() throws Exception {
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {

			/* 检索条件 */
			String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
			if (Tools.notEmpty(KEYWORDS))
				pd.put("KEYWORDS", KEYWORDS.trim());
			String STRARTTIME = pd.getString("STRARTTIME"); // 开始时间
			String ENDTIME = pd.getString("ENDTIME"); // 结束时间
			if (Tools.notEmpty(STRARTTIME))
				pd.put("STRARTTIME", STRARTTIME + " 00:00:00");
			if (Tools.notEmpty(ENDTIME))
				pd.put("ENDTIME", ENDTIME + " 00:00:00");

			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();
			titles.add("登录名"); // 1
			titles.add("身份证号"); // 2
			titles.add("姓名"); // 3
			titles.add("角色"); // 4
			titles.add("手机"); // 5
			titles.add("邮箱"); // 6
			titles.add("最近登录"); // 7
			titles.add("上次登录IP"); // 8
			dataMap.put("titles", titles);
			List<PageData> userList = usersService.listAllUser(pd);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < userList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", userList.get(i).getString("USERNAME")); // 1
				vpd.put("var2", userList.get(i).getString("NUMBER")); // 2
				vpd.put("var3", userList.get(i).getString("NAME")); // 3
				vpd.put("var4", userList.get(i).getString("ROLE_NAME")); // 4
				vpd.put("var5", userList.get(i).getString("PHONE")); // 5
				vpd.put("var6", userList.get(i).getString("EMAIL")); // 6
				vpd.put("var7", userList.get(i).getString("LAST_LOGIN")); // 7
				vpd.put("var8", userList.get(i).getString("IP")); // 8
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView(); // 执行excel操作
			mv = new ModelAndView(erv, dataMap);
		} catch (Exception e) {
		}
		return mv;
	}

	/**
	 * 导出一卡通白名单到excel
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel2")
	@RequiresPermissions("toExcel")
	public ModelAndView exportExcel2() throws Exception {
		ModelAndView mv = new ModelAndView();
		try {
			// 获取一卡通在职人员数据
			List<Registered> OnJobList = GetOneCardOnJobEmployee();
			// 构造excel
			Map<String, Object> dataMap = new HashMap<String, Object>();
			List<String> titles = new ArrayList<String>();
			titles.add("ID"); // 1
			titles.add("CARD"); // 2
			titles.add("NAME"); // 3
			titles.add("PHONE"); // 4
			titles.add("REGISTERED"); // 5
			titles.add("LABORRELATION"); // 6
			titles.add("SERVICESDEPARTMENT"); // 7
			titles.add("EMPLOYEETYPE"); // 8
			titles.add("USERNO"); // 9
			titles.add("EMPLOYER");//10
			titles.add("WAYS");//11
			titles.add("MAJOR");//12
			titles.add("STATION");//13
			titles.add("GENDER");//14
			titles.add("JOB");//15
			titles.add("PHOTO");//16
			titles.add("TEAM");//17
			titles.add("ONJOB");//18
			titles.add("PHOTO_URL");//19
			dataMap.put("titles", titles);
			List<PageData> varList = new ArrayList<PageData>();
			for (int i = 0; i < OnJobList.size(); i++) {
				PageData vpd = new PageData();
				vpd.put("var1", OnJobList.get(i).getID()); // 1
				vpd.put("var2", OnJobList.get(i).getCARD()); // 2
				vpd.put("var3", OnJobList.get(i).getNAME()); // 3
				vpd.put("var4", OnJobList.get(i).getPHONE()); // 4
				vpd.put("var5", OnJobList.get(i).getREGISTERED()); // 5
				vpd.put("var6", OnJobList.get(i).getLaborRelation()); // 6
				vpd.put("var7", OnJobList.get(i).getServicesDepartment()); // 7
				vpd.put("var8", OnJobList.get(i).getEmployeeType()); // 8
				vpd.put("var9", OnJobList.get(i).getUserNo()); // 9
				vpd.put("var10", OnJobList.get(i).getEMPLOYER()); //10
				vpd.put("var11", OnJobList.get(i).getWAYS());//11
				vpd.put("var12", OnJobList.get(i).getMAJOR());//12
				vpd.put("var13", OnJobList.get(i).getSTATION());//13
				vpd.put("var14", OnJobList.get(i).getGENDER());//14
				vpd.put("var15", OnJobList.get(i).getJOB());//15
				vpd.put("var16", Const.FACEBOOK + OnJobList.get(i).getCARD() + ".png");//16
				vpd.put("var17", OnJobList.get(i).getTEAM());//17
				vpd.put("var18", OnJobList.get(i).getONJOB());//18
				vpd.put("var19", OnJobList.get(i).getPHOTO_URL());//19
				varList.add(vpd);
			}
			dataMap.put("varList", varList);
			ObjectExcelView erv = new ObjectExcelView(); // 执行excel操作
			mv = new ModelAndView(erv, dataMap);
		} catch (Exception e) {
		}
		return mv;
	}

	/**
	 * 下载模版
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downExcel")
	public void downExcel(HttpServletResponse response) throws Exception {
		FileDownload.fileDownload(response, PathUtil.getProjectpath() + Const.FILEPATHFILE + "Users.xls", "Users.xls");
	}

	/**
	 * 从EXCEL导入到数据库
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/readExcel")
	@RequiresPermissions("fromExcel")
	@ResponseBody
	public Object readExcel(@RequestParam(value = "excel", required = false) MultipartFile file,@RequestParam(value = "Confirmation", required = false) String ConfirmationText) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		String ConfirmationTips = "";
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getProjectpath() + Const.FILEPATHFILE; // 文件上传路径
			String fileName = FileUpload.fileUp(file, filePath, "OneCardWriteList"); // 执行上传
			// 执行读EXCEL操作,读出的数据导入List.// 2:从第2行开始；0:从第A列开始；0:第0个sheet
			List<Registered> OnJobList = ObjectExcelRead.readExcelFromOneCardExcel(filePath, fileName, 1, 0, 0);
			// 获取在库白名单数据
			List<Registered> whriteList = new ArrayList<Registered>();
			whriteList = registeredService.getAll();
			// 集合做差集
			List<Registered> need_delete = new ArrayList<Registered>();
			need_delete = DifferenceSetByID(whriteList, OnJobList); // 需要在白名单删除的
			List<Registered> need_add = new ArrayList<Registered>();
			need_add = DifferenceSetByID(OnJobList, whriteList);// 需要新增到白名单
			if((OnJobList.size() < 8000 || need_delete.size() > 1000) && !ConfirmationText.equals("--no-preserve-root")){
				errInfo = "ConfirmationTips";
				ConfirmationTips = "本次操作全量数据包长度" + OnJobList.size() + ",需注销用户" + need_delete.size() + "人,需添加白名单" + need_add.size() + "人。本次操作是十分危险的，请使用'--no-preserve-root'参数再次确认本次操作的安全性！";
				map.put("ConfirmationTips", ConfirmationTips); // 返回结果
			}else{
				// 把新入职的加入到白名单
				int newCount = 0;
				for (int i = 0; i < need_add.size(); i++) {
					Registered temp = need_add.get(i);
					registeredService.save(temp);
					newCount++;
				}
				// 把已经离职的员工在系统中标记为离职
				int outdateCount = 0;
				for (int i = 0; i < need_delete.size(); i++) {
					Registered temp = need_delete.get(i);
					if (temp.getREGISTERED().equals("1") || temp.getREGISTERED().equals("2")) {// 已注册的和等待审核的
						// 删除白名单
						registeredService.delete(temp.getCARD());
						// 删除user表
						PageData pd = new PageData();
						pd.put("NUMBER", temp.getCARD());
						pd = usersService.findByNumber(pd);
						if (pd.size() == 1) {// 没找到该用户，证明白名单数据和user表数据产生了不一致
							// nothing to do
						} else {
							pd.put("STATUS", "9");// 9状态是自动离职销户
							usersService.editUser(pd);
							// 删除富文本编辑器
							PageData pd3 = new PageData();
							pd3.put("USER_ID", pd.get("USER_ID").toString());
							ueditorService.delete(pd3); // 删除副文本关联数据
							// 获取员工数据
							PageData pd2 = new PageData();
							pd2.put("USER_ID", pd.getString("USERNAME"));
							pd2 = staffService.findByUserId(pd2);
							if (pd2.size() == 1) {// 数据不一致
								// nothing to do
							} else {
								// 删除datajur表,收回权限
								System.out.println("即将删除DJ：" + pd2.getString("STAFF_ID"));
								datajurService.delete(pd2.getString("STAFF_ID"));
								// 删除staff表
								PageData pd4 = new PageData();
								pd4.put("STAFF_ID", pd2.getString("STAFF_ID"));
								staffService.delete(pd4);
							}
							outdateCount++;
						}
					} else {
						// 删除白名单
						registeredService.delete(temp.getCARD());
						outdateCount++;
					}
				}
				// 更新白名单基础数据
				updateListInfoByCard(OnJobList);
				map.put("result", errInfo); // 返回结果
				map.put("newCount", newCount); // 新增计数
				map.put("outdateCount", outdateCount); // 离职计数
				return map;
			}
		}else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 去修改用户页面(在线管理页面打开)
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEditUfromOnline")
	@ResponseBody
	public Object goEditUfromOnline() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		if ("admin".equals(pd.getString("USERNAME"))) {
			return null;
		} // 不能查看admin用户
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有系统用户角色
		map.put("fx", "user");
		pd = usersService.findByUsername(pd); // 根据ID读取
		String ROLE_IDS = pd.getString("ROLE_IDS"); // 副职角色ID
		if (Tools.notEmpty(ROLE_IDS)) {
			String arryROLE_ID[] = ROLE_IDS.split(",");
			for (int i = 0; i < roleList.size(); i++) {
				Role role = roleList.get(i);
				String roleId = role.getROLE_ID();
				for (int n = 0; n < arryROLE_ID.length; n++) {
					if (arryROLE_ID[n].equals(roleId)) {
						role.setRIGHTS("1"); // 此时的目的是为了修改用户信息上，能看到副职角色都有哪些
						break;
					}
				}
			}
		}
		map.put("pd", pd);
		map.put("roleList", roleList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 查看用户
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/view")
	@ResponseBody
	public Object view() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String USERNAME = pd.getString("USERNAME");
		if ("admin".equals(USERNAME)) {
			return null;
		} // 不能查看admin用户
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有系统用户角色
		pd = usersService.findByUsername(pd); // 根据ID读取
		map.put("msg", "1");
		if (null == pd) {
			PageData rpd = new PageData();
			rpd.put("RNUMBER", USERNAME); // 用户名查不到数据时就把数据当作角色的编码去查询角色表
			rpd = roleService.getRoleByRnumber(rpd);
			map.put("rpd", rpd);
			map.put("msg", "2");
		}
		map.put("pd", pd);
		map.put("roleList", roleList);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 显示用户列表(弹窗选择用)
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listUsersForWindow")
	@ResponseBody
	public Object listUsersForWindow(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		String DEPT = pd.getString("DEPT");// 部门检索
		if (!DEPT.equals("") && !DEPT.equals("null")) {
			// 获取该部门下的所有科室
			String childrens = departmentService.getDEPARTMENT_IDS(DEPT);
			childrens = childrens.replace("fh", DEPT);// 加上父亲自己
			pd.put("DEPT", childrens);
		}
		if (Tools.notEmpty(KEYWORDS)) {
			pd.put("KEYWORDS", KEYWORDS.trim());
		}
		String STRARTTIME = pd.getString("STRARTTIME"); // 开始时间
		String ENDTIME = pd.getString("ENDTIME"); // 结束时间
		if (Tools.notEmpty(STRARTTIME)) {
			pd.put("STRARTTIME", STRARTTIME + " 00:00:00");
		}
		if (Tools.notEmpty(ENDTIME)) {
			pd.put("ENDTIME", ENDTIME + " 00:00:00");
		}
		page.setPd(pd);
		List<PageData> userList = usersService.listUsersBystaff(page); // 列出用户列表(弹窗选择用)
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd); // 列出所有系统用户角色
		map.put("userList", userList);
		map.put("roleList", roleList);
		map.put("page", page);
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 未审批注册用户列表
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/notApprovedUserList")
	@RequiresPermissions("registcheck:list")
	@ResponseBody
	public Object notApprovedUserList(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		/* 检索条件 */
		// 部门信息搜索条件
		String USERNAME = Jurisdiction.getUsername();
		// 超级管理员可看到全部部门未审批的注册员工
		if (USERNAME.equals("admin")) {
			// nothing to do
		} else {
			String USERDEPT = Jurisdiction.getUSER_DEPT();
			if (Tools.notEmpty(USERDEPT))
				pd.put("DEPARTMENT_ID", USERDEPT.trim());// 部门号
			if (Tools.notEmpty(USERDEPT)) {
				// 获取该部门下的所有孩子节点ID
				String ids = departmentService.getDEPARTMENT_IDS(USERDEPT);
				pd.put("DEPARTMENT_IDS", ids.trim());// 孩子节点
			}
		}
		// 关键字搜索条件
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());

		page.setPd(pd);
		List<PageData> userList = usersService.notApprovedUserlistPage(page); // 列出未审核用户列表
		map.put("userList", userList);
		map.put("page", page);
		map.put("pd", pd);

		map.put("result", errInfo);
		return map;
	}

	/**
	 * 驳回注册
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/disallow")
	@ResponseBody
	public Object disallow() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		String USER_IDS = pd.getString("USER_IDS");
		String ID_CARDS = pd.getString("ID_CARDS");
		String errInfo = "success";
		if (Tools.notEmpty(USER_IDS) && Tools.notEmpty(ID_CARDS)) {
			String ArrayUSER_IDS[] = USER_IDS.split(",");
			String ArrayID_CARDS[] = ID_CARDS.split(",");
			FHLOG.save(Jurisdiction.getUsername(), "驳回注册申请");// 记录日志
			// 根据身份证号修改registered_list表registered字段为：0(未注册)
			registeredService.updateStatusByCardIdTo0(ArrayID_CARDS);
			// 根据ID从sys_user中删除该条记录
			usersService.deleteAllUser(ArrayUSER_IDS); // 删除用户
			ueditorService.deleteAll(ArrayUSER_IDS); // 删除副文本关联数据
			// 根据身份证号从oa_staff中删除该条记录
			staffService.deleteAllBySFID(ArrayID_CARDS);
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 批准注册
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/allow")
	@ResponseBody
	public Object allow() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		String USER_IDS = pd.getString("USER_IDS");
		String ID_CARDS = pd.getString("ID_CARDS");
		String STAFF_IDS = pd.getString("STAFF_IDS");
		String ROLE_ID = pd.getString("ROLE_ID");
		String errInfo = "success";
		if (Tools.notEmpty(USER_IDS) && Tools.notEmpty(ID_CARDS) && Tools.notEmpty(STAFF_IDS)) {
			String ArrayUSER_IDS[] = USER_IDS.split(",");
			String ArrayID_CARDS[] = ID_CARDS.split(",");
			String ArraySTAFF_IDS[] = STAFF_IDS.split(",");
			FHLOG.save(Jurisdiction.getUsername(), "批准注册申请");// 记录日志
			// 根据身份证号修改registered_list表registered字段为：1(已注册，已审核)
			registeredService.updateStatusByCardIdTo1(ArrayID_CARDS);
			// 根据ID更新sys_user表status字段为0
			// 授予部门组织架构权限datajur表
			for (int i = 0; i < ArrayUSER_IDS.length; i++) {
				// 处理用户信息
				PageData user_pd = new PageData();
				user_pd.put("USER_ID", ArrayUSER_IDS[i]);
				user_pd = usersService.findById(user_pd);
				user_pd.put("STATUS", "0");
				// 设置角色
				if (!ROLE_ID.equals("null")) {
					user_pd.put("ROLE_ID", ROLE_ID);
				}
				usersService.editUser(user_pd);
				// 处理部门和组织架构权限信息
				PageData staff_pd = new PageData();
				staff_pd.put("STAFF_ID", ArraySTAFF_IDS[i]);
				staff_pd = staffService.findById(staff_pd);
				String staff_type = staff_pd.getString("BZ");
				if (staff_type.equals("本工")) {
					String DEPARTMENT_IDS = departmentService.getDEPARTMENT_IDS(staff_pd.getString("DEPARTMENT_ID"));// 获取某个部门所有下级部门ID
					PageData datajur_pd = new PageData();
					datajur_pd.put("DATAJUR_ID", staff_pd.getString("STAFF_ID"));
					datajur_pd.put("STAFF_ID", staff_pd.getString("STAFF_ID")); // 主键
					datajur_pd.put("DEPARTMENT_IDS", DEPARTMENT_IDS); // 部门ID集
					datajur_pd.put("DEPARTMENT_ID", staff_pd.getString("DEPARTMENT_ID")); // 部门ID集
					datajurService.save(datajur_pd); // 把此员工默认部门及以下部门ID保存到组织数据权限表
					map.put("result", errInfo);
				} else {
					String DEPARTMENT_IDS = departmentService.getDEPARTMENT_IDS(staff_pd.getString("JOBTYPE"));// 获取某个部门所有下级部门ID
					PageData datajur_pd = new PageData();
					datajur_pd.put("DATAJUR_ID", staff_pd.getString("STAFF_ID"));
					datajur_pd.put("STAFF_ID", staff_pd.getString("STAFF_ID")); // 主键
					datajur_pd.put("DEPARTMENT_IDS", DEPARTMENT_IDS); // 部门ID集
					datajur_pd.put("DEPARTMENT_ID", staff_pd.getString("DEPARTMENT_ID")); // 部门ID集
					datajurService.save(datajur_pd); // 把此员工默认部门及以下部门ID保存到组织数据权限表
					map.put("result", errInfo);
				}
			}
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 获取当前用户信息 包括但不限于登录名、部门、角色、身份证等各种细节信息
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getUserInfo")
	@ResponseBody
	public Object getUserInfo() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String USER_ID = Jurisdiction.getUsername();
		System.out.println("当前用户ID：" + USER_ID);
		pd.put("USER_ID", USER_ID);
		pd = staffService.findByUserId(pd); // 根据USERID读取
		map.put("pd", pd);
		if (!USER_ID.equals("admin")) {
			map.put("rdept", Jurisdiction.getUSER_DEPT());// 递归出来的部门级粒度部门号
		}
		map.put("Rnumbers", Jurisdiction.getRnumbers());
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 移动端人员信息核验接口
	 *
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/infoCheck")
	@ResponseBody
	public Object infoCheck() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 鉴定权限
		String infFilePath = PathUtil.getClasspath() + Const.SYSSET; // 配置文件路径
		String authorize = IniFileUtil.readCfgValue(infFilePath, "AuthorizeSet", "ScanCodeAuthorize", "admin"); // 授权人员列表
		String[] authorizeArray = authorize.split(",");
		String userName = Jurisdiction.getUsername();
		boolean flag = false;
		for(int i = 0;i<authorizeArray.length;i++){
			if(authorizeArray[i].equals(userName)){
				flag = true;
				break;
			}
		}
		if(flag){
			List<PageData> staffInfoList = registeredService.findByCodeOrName(pd);
			map.put("staffInfoList", staffInfoList);
			map.put("msg", "查询成功");
			map.put("result", errInfo); // 返回结果
			return map;
		}
		map.put("msg", "权限不足，请联系管理员授权！");
		map.put("staffInfoList", new ArrayList<PageData>());
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 随机取一个人
	 *
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/shake")
	@ResponseBody
	public Object shake() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		// 鉴定权限
		String infFilePath = PathUtil.getClasspath() + Const.SYSSET; // 配置文件路径
		String authorize = IniFileUtil.readCfgValue(infFilePath, "AuthorizeSet", "ScanCodeAuthorize", "admin"); // 授权人员列表
		String[] authorizeArray = authorize.split(",");
		String userName = Jurisdiction.getUsername();
		boolean flag = false;
		for(int i = 0;i<authorizeArray.length;i++){
			if(authorizeArray[i].equals(userName)){
				flag = true;
				break;
			}
		}
		if(flag){
			PageData staffInfoList = registeredService.shake(pd);
			map.put("staffInfoList", staffInfoList);
			map.put("msg", "查询成功");
			map.put("result", errInfo); // 返回结果
			return map;
		}
		map.put("msg", "权限不足，请联系管理员授权！");
		map.put("staffInfoList", new ArrayList<PageData>());
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 获取人脸照片数据
	 *
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getStaffPhoto")
	@ResponseBody
	public Object getStaffPhoto() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String[] photoPathArray = pd.getString("photoPathArray").split(",");
		List<String> base64List = new ArrayList<String>();//这个list将用来放一个一个的base64编码字符串
		// base64编码图片
		for (String onePath : photoPathArray) {
			// 编码成base64
			String Base64String = ImageAnd64Binary.getImageStr(PathUtil.getProjectpath() + onePath);
			if(Base64String == null){
				// 暂无人像数据，则加载缺省的占位图片
				Base64String = ImageAnd64Binary.getImageStr(PathUtil.getProjectpath() + "uploadFiles/facebook/qzx.jpg");
			}
			// 记录下这个路径
			base64List.add(Base64String);//加到list里
		}
		map.put("varListI", base64List);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 更新白名单数据 通过一卡通的数据接口更新白名单数据
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateList")
	@ResponseBody
	public Object updateList() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		List<Registered> whriteList = new ArrayList<Registered>();
		// 获取一卡通在职人员数据
		List<Registered> OnJobList = GetOneCardOnJobEmployee();
		// 获取在库白名单数据
		whriteList = registeredService.getAll();
		// 集合做差集
		List<Registered> need_delete = new ArrayList<Registered>();
		need_delete = DifferenceSetByID(whriteList, OnJobList); // 需要在白名单删除的
		List<Registered> need_add = new ArrayList<Registered>();
		need_add = DifferenceSetByID(OnJobList, whriteList);// 需要新增到白名单
		// 把新入职的加入到白名单
		int newCount = 0;
		for (int i = 0; i < need_add.size(); i++) {
			Registered temp = need_add.get(i);
			registeredService.save(temp);
			newCount++;
		}
		// 把已经离职的员工在系统中标记为离职
		int outdateCount = 0;
		for (int i = 0; i < need_delete.size(); i++) {
			Registered temp = need_delete.get(i);
			if (temp.getREGISTERED().equals("1") || temp.getREGISTERED().equals("2")) {// 已注册的和等待审核的
				// 删除白名单
				registeredService.delete(temp.getCARD());
				// 删除user表
				PageData pd = new PageData();
				pd.put("NUMBER", temp.getCARD());
				pd = usersService.findByNumber(pd);
				if (pd.size() == 1) {// 没找到该用户，证明白名单数据和user表数据产生了不一致
					// nothing to do
				} else {
					pd.put("STATUS", "9");// 9状态是自动离职销户
					usersService.editUser(pd);
					// 删除富文本编辑器
					PageData pd3 = new PageData();
					pd3.put("USER_ID", pd.get("USER_ID").toString());
					ueditorService.delete(pd3); // 删除副文本关联数据
					// 获取员工数据
					PageData pd2 = new PageData();
					pd2.put("USER_ID", pd.getString("USERNAME"));
					pd2 = staffService.findByUserId(pd2);
					if (pd2.size() == 1) {// 数据不一致
						// nothing to do
					} else {
						// 删除datajur表,收回权限
						datajurService.delete(pd2.getString("STAFF_ID"));
						// 删除staff表
						PageData pd4 = new PageData();
						pd4.put("STAFF_ID", pd2.getString("STAFF_ID"));
						staffService.delete(pd4);
					}
					outdateCount++;
				}
			} else {
				// 删除白名单
				registeredService.delete(temp.getCARD());
				outdateCount++;
			}
		}
		// 更新白名单基础数据
		updateListInfoByCard(OnJobList);
		map.put("result", errInfo); // 返回结果
		map.put("newCount", newCount); // 新增计数
		map.put("outdateCount", outdateCount); // 离职计数
		return map;
	}

	/**
	 * 通过接口获取一卡通全部在职人数
	 * 
	 * @param
	 * @throws Exception
	 */
	public List<Registered> GetOneCardOnJobEmployee() {
		JSONArray employee = new JSONArray();
		List<Registered> OnJobList = new ArrayList<Registered>();
		// 获取一卡通token
		String token = GetOneCardToken();
		// 获取一卡通内的总在职人数
		if (!token.equals("error")) {
			// 循环获取全部员工数据，每次1000条，总共约20000人
			for (int i = 1; i < 25; i++) {
				JSONArray data = GetEmployee(token, i);
				if (data != null && data.size() > 0) {
					employee.add(data);// 数据格式JSONArray[[JSONArray],[JSONArray],[JSONArray],[JSONArray],...]
				} else {
					break;
				}
			}
			// 解包，构造对象list
			for (int i = 0; i < employee.size(); i++) {// 外层循环
				JSONArray now_array = employee.getJSONArray(i);
				for (int j = 0; j < now_array.size(); j++) {// 内层循环
					String oneEmployee = now_array.getString(j);
					JSONObject oneJSON = JSONObject.parseObject(oneEmployee);
					Registered reg = new Registered();
					// if(!oneJSON.getString("IDCard").equals("211223198110190000")) {
					reg.setCARD(oneJSON.getString("IDCard"));
					reg.setNAME(oneJSON.getString("UserName"));
					reg.setPHONE(oneJSON.getString("PhoneNumber"));
					reg.setUserNo(oneJSON.getString("UserNo"));
					reg.setLaborRelation(oneJSON.getString("LaborRelation"));
					reg.setServicesDepartment(oneJSON.getString("ServicesDepartment"));
					reg.setEmployeeType(oneJSON.getString("EmployeeType"));
					reg.setEMPLOYER(oneJSON.getString("Employer"));
					reg.setWAYS(oneJSON.getString("Ways"));
					reg.setMAJOR(oneJSON.getString("Major"));
					reg.setSTATION(oneJSON.getString("Station"));
					reg.setGENDER(oneJSON.getString("Gender"));
					reg.setJOB(oneJSON.getString("Job"));
					reg.setPHOTO(Const.FACEBOOK + oneJSON.getString("IDCard") + ".png");
					reg.setTEAM(oneJSON.getString("Team"));
					reg.setONJOB(oneJSON.getString("OnJob"));
					reg.setPHOTO_URL(oneJSON.getString("Photo"));
					reg.setREGISTERED("0");
					OnJobList.add(reg);
					// }
				}
			}
			return OnJobList;
		} else {
			return null;
		}
	}

	/**
	 * 获取一卡通Token
	 * 
	 * @param
	 * @throws Exception
	 */
	public String GetOneCardToken() {
		String OneCardURL = Const.ONECARD_URL + "/api/getUserToken/11003219";
		String Response = HttpRequest.get(OneCardURL);
		JSONObject json = JSONObject.parseObject(Response);
		if (json.getString("code").equals("1")) {
			return json.getString("data");
		}
		return "error";
	}

	/**
	 * 获取一卡通在职人员数据，每次1000条
	 * 
	 * @param
	 * @throws Exception
	 */
	public JSONArray GetEmployee(String token, int startPage) {
		//String OneCardURL = "http://192.168.200.123:8000/api/GetEmployeeList/" + token;
		String OneCardURL = Const.ONECARD_URL + "/api/GetStaffListFull/" + token;// 新版全量接口
		// 构造请求体,一次1000人
		JSONObject requestBody = new JSONObject();
		requestBody.put("pageSize", 1000);
		requestBody.put("currentPage", startPage);
		// POST请求
		try {
			String Response = HttpRequest.post(requestBody, OneCardURL);
			JSONObject json = JSONObject.parseObject(Response);
			if (json.getString("code").equals("1")) {
				String jsonDataArray = json.getString("data");
				JSONArray jsonArray = new JSONArray();
				jsonArray = JSONArray.parseArray(jsonDataArray);
				return jsonArray;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根数身份证号更新白名单数据
	 */
	public void updateListInfoByCard(List<Registered> list1) {
		for (Registered registered : list1) {
			try {
				registeredService.updateListInfo(registered);
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}
		}
	}

	/**
	 * 构造白名单人像数据
	 *
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/createPhotoFull")
	@ResponseBody
	public Object createPhotoFull() throws Exception {
		/** 固定的线程池（当前线程池大小为5） */
		final ExecutorService executor = Executors.newFixedThreadPool(5);
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<Registered> whriteList = new ArrayList<Registered>();
		// 获取在库白名单数据
		whriteList = registeredService.getAll();
		if(whriteList.size() > 0){
			// 逐个下载照片文件
			String saveBasePath = PathUtil.getProjectpath() + Const.FACEBOOK;
			// 初始化计时器
			CountDownLatch cdl = new CountDownLatch(whriteList.size());
			System.out.println("====== 线程开始 =====");
			for (Registered reg:whriteList) {
				String now_card = reg.getCARD();
				String now_URL = reg.getPHOTO_URL();
				if(!now_URL.equals("") && reg.getALREADY_DOWN().equals("0")){
					// 开启线程
					executor.submit(new Runnable() {
						@Override
						public void run() {
							try {
								FileUpload.getHtmlPicture(now_URL,saveBasePath,now_card + ".png");
								reg.setALREADY_DOWN("1");
								registeredService.updateALREADY_DOWN(reg);
							} catch (Exception e) {
								System.out.println("照片下载失败：" + now_URL);
							}
							// 闭锁-1
							cdl.countDown();
						}
					});
				}
			}
		}
		map.put("tips", "执行完毕！");
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 差集(基于java8新特性)优化解法2 适用于大数据量 求List1中有的但是List2中没有的元素 身份证号一致即认为元素一致
	 */
	public static List<Registered> DifferenceSetByID(List<Registered> list1, List<Registered> list2) {
		Map<String, String> tempMap = list2.parallelStream()
				.collect(Collectors.toMap(Registered::getCARD, Registered::getCARD));
		return list1.parallelStream().filter(Registered -> {
			return !tempMap.containsKey(Registered.getCARD());
		}).collect(Collectors.toList());
	}

	/**
	 * 差集(基于java8新特性)优化解法2 适用于大数据量 求List1中有的但是List2中没有的元素
	 */
	public static List<Registered> DifferenceSet(List<Registered> list1, List<Registered> list2) {
		Map<Registered, Registered> tempMap = list2.parallelStream()
				.collect(Collectors.toMap(Function.identity(), Function.identity(), (oldData, newData) -> newData));
		return list1.parallelStream().filter(str -> {
			return !tempMap.containsKey(str);
		}).collect(Collectors.toList());
	}

}
