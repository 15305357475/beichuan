package org.fh.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.fh.controller.base.BaseController;
import org.fh.entity.system.Role;
import org.fh.service.system.FHlogService;
import org.fh.service.system.RoleService;
import org.fh.service.system.UsersService;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：审批角色配置处理类 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/ApprovalRole")
public class ApprovalRoleController extends BaseController {

	@Autowired
	private UsersService usersService;
	@Autowired
	private RoleService roleService;
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
	@RequiresPermissions("ApprovalRole:list")
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
	 * 去修改用户页面(从系统用户页面修改)
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEditUser")
	@RequiresPermissions("ApprovalRole:edit")
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
		pd = usersService.findById(pd); // 根据ID读取
		String ROLE_IDS = pd.getString("ROLE_IDS"); // 副职角色ID
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
	 * 修改用户(系统用户列表修改)
	 */
	@RequestMapping(value = "/editUser")
	@RequiresPermissions("ApprovalRole:edit")
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
}
