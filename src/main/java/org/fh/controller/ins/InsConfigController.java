package org.fh.controller.ins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.system.Dictionaries;
import org.fh.entity.system.Role;
import org.fh.service.system.DictionariesService;
import org.fh.service.system.FHlogService;
import org.fh.service.system.RoleService;
import org.fh.service.system.UsersService;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONArray;

/**
 * 说明：隐患排查基础参数配置处理类 、 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/insConfig")
public class InsConfigController extends BaseController {

	@Autowired
	private DictionariesService dictionariesService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private FHlogService FHLOG;

	/**
	 * 显示列表ztree
	 * 
	 * @return
	 */
	@RequestMapping(value = "/listAllDict")
	@RequiresPermissions("insConfig:list")
	@ResponseBody
	public Object listAllDict() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		JSONArray arr = JSONArray.fromObject(dictionariesService.listAllDictForIns("INS_CONFIG_00"));
		String json = arr.toString();
		json = json.replaceAll("DICTIONARIES_ID", "id").replaceAll("PARENT_ID", "pId").replaceAll("NAME", "name")
				.replaceAll("subDict", "nodes").replaceAll("hasDict", "checked").replaceAll("treeurl", "url");
		map.put("zTreeNodes", json);
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
	@RequiresPermissions("insConfig:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());
		String DICTIONARIES_ID = null == pd.get("DICTIONARIES_ID") ? "" : pd.get("DICTIONARIES_ID").toString();
		pd.put("DICTIONARIES_ID", DICTIONARIES_ID); // 上级ID
		page.setPd(pd);
		List<PageData> varList = dictionariesService.list(page); // 列出Dictionaries列表
		if ("".equals(DICTIONARIES_ID) || "0".equals(DICTIONARIES_ID)) {
			map.put("PARENT_ID", "0"); // 上级ID
		} else {
			map.put("PARENT_ID", dictionariesService.findById(pd).getString("PARENT_ID")); // 上级ID
		}
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 去新增页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAdd")
	@ResponseBody
	public Object goAdd() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DICTIONARIES_ID = null == pd.get("DICTIONARIES_ID") ? "" : pd.get("DICTIONARIES_ID").toString();
		pd.put("DICTIONARIES_ID", DICTIONARIES_ID); // 上级ID
		map.put("pds", dictionariesService.findById(pd)); // 传入上级所有信息
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("insConfig:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("DICTIONARIES_ID", this.get32UUID()); // 主键
		dictionariesService.save(pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 判断编码是否存在
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hasBianma")
	@ResponseBody
	public Object hasBianma() {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		try {
			pd = this.getPageData();
			if (dictionariesService.findByBianma(pd) != null) {
				errInfo = "error";
			}
		} catch (Exception e) {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 去修改页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = dictionariesService.findById(pd); // 根据ID读取
		map.put("pd", pd); // 放入视图容器
		pd.put("DICTIONARIES_ID", pd.get("PARENT_ID").toString()); // 用作上级信息
		map.put("pds", dictionariesService.findById(pd)); // 传入上级所有信息
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
	@RequiresPermissions("insConfig:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		dictionariesService.edit(pd);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param DICTIONARIES_ID
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("insConfig:del")
	@ResponseBody
	public Object delete(@RequestParam String DICTIONARIES_ID) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd.put("DICTIONARIES_ID", DICTIONARIES_ID);
		String errInfo = "success";
		if (dictionariesService.listSubDictByParentId(DICTIONARIES_ID).size() > 0) {// 判断是否有子级，是：不允许删除
			errInfo = "error";
		} else {
			pd = dictionariesService.findById(pd); // 根据ID读取
			if ("yes".equals(pd.getString("YNDEL")))
				return null; // 当禁止删除字段值为yes, 则禁止删除，只能从手动从数据库删除
			if (null != pd.get("TBSNAME") && !"".equals(pd.getString("TBSNAME"))) {
				String TBFIELD = pd.getString("TBFIELD");
				if (Tools.isEmpty(TBFIELD))
					TBFIELD = "BIANMA"; // 如果关联字段没有设置，就默认字段为 BIANMA
				pd.put("TBFIELD", TBFIELD);
				String[] table = pd.getString("TBSNAME").split(",");
				for (int i = 0; i < table.length; i++) {
					pd.put("thisTable", table[i]);
					try {
						if (Integer.parseInt(dictionariesService.findFromTbs(pd).get("zs").toString()) > 0) {// 判断是否被占用，是：不允许删除(去排查表检查字典表中的编码字段)
							errInfo = "error";
							break;
						}
					} catch (Exception e) {
						errInfo = "error2";
						break;
					}
				}
			}
		}
		if ("success".equals(errInfo)) {
			dictionariesService.delete(pd); // 执行删除
		}
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 获取连级数据
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getLevels")
	@ResponseBody
	public Object getLevels() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DICTIONARIES_ID = pd.getString("DICTIONARIES_ID");
		DICTIONARIES_ID = Tools.isEmpty(DICTIONARIES_ID) ? "0" : DICTIONARIES_ID;
		List<Dictionaries> varList = dictionariesService.listSubDictByParentId(DICTIONARIES_ID); // 用传过来的ID获取此ID下的子列表数据
		List<PageData> pdList = new ArrayList<PageData>();
		for (Dictionaries d : varList) {
			PageData pdf = new PageData();
			pdf.put("DICTIONARIES_ID", d.getDICTIONARIES_ID());
			pdf.put("BIANMA", d.getBIANMA());
			pdf.put("NAME", d.getNAME());
			pdf.put("BZ", d.getBZ());
			pdList.add(pdf);
		}
		map.put("list", pdList);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 根据编码模糊查询检查点
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getPointByBIANMA")
	@ResponseBody
	public Object getPointByBIANMA() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DICTIONARIES_ID = pd.getString("BIANMA");
		DICTIONARIES_ID = Tools.isEmpty(DICTIONARIES_ID) ? "0" : DICTIONARIES_ID;
		List<Dictionaries> varList = dictionariesService.getPointByBIANMA(DICTIONARIES_ID); // 用传过来的ID获取此ID下的子列表数据
		List<PageData> pdList = new ArrayList<PageData>();
		for (Dictionaries d : varList) {
			PageData pdf = new PageData();
			pdf.put("DICTIONARIES_ID", d.getDICTIONARIES_ID());
			pdf.put("BIANMA", d.getBIANMA());
			pdf.put("NAME", d.getNAME());
			pdf.put("BZ", d.getBZ());
			pdList.add(pdf);
		}
		map.put("list", pdList);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 查询隐患排查系统下的所有安全组
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/AllRolelist")
	@ResponseBody
	public Object AllRolelist() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ROLE_ID", "4");// 列出此组下架角色
		List<Role> AllRolelist = roleService.listAllRolesByPId(pd);
		pd = roleService.findById(pd);
		map.put("pd", pd);
		map.put("AllRolelist", AllRolelist);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 查询当前用户的主职和副职角色
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getNowUserRoles")
	@ResponseBody
	public Object getNowUserRoles() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("USERNAME", Jurisdiction.getUsername());
		PageData res = usersService.getUserRoles(pd);
		map.put("res", res);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 查询指定角色组下所有成员
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getAllMember")
	@ResponseBody
	public Object getAllMember() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> res = usersService.getAllMember(pd);
		map.put("res", res);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 修改用户(系统用户列表修改)
	 */
	@RequestMapping(value = "/changeRoleGroup")
	@ResponseBody
	public Object changeRoleGroup() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String ROLE_IDS = pd.getString("ROLE_IDS");
		PageData upd = new PageData();
		upd.put("USERNAME", Jurisdiction.getUsername());
		upd = usersService.findByUsername(upd);
		upd.put("ROLE_IDS", ROLE_IDS);
		usersService.editUser(upd); // 执行修改
		FHLOG.save(Jurisdiction.getUsername(), "变更安全组为:" + ROLE_IDS); // 记录日志
		map.put("result", errInfo);
		return map;
	}
}
