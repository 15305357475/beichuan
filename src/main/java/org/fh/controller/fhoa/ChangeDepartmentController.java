package org.fh.controller.fhoa;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcStartController;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.fhoa.ChangeDepartmentService;
import org.fh.service.fhoa.DatajurService;
import org.fh.service.fhoa.DepartmentService;
import org.fh.service.fhoa.StaffService;
import org.fh.service.system.FHlogService;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：变更部门 
 * 作者：f-sci 
 * 授权：bsic
 */
@Controller
@RequestMapping("/changeDepartment")
public class ChangeDepartmentController extends AcStartController {

	@Autowired
	private ChangeDepartmentService changeDepartmentService;
	@Autowired
	private StaffService staffService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private DatajurService datajurService;
	@Autowired
	private FHlogService FHLOG;

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	//@RequiresPermissions("changedept:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		PageData staff_pd = new PageData();
		staff_pd = staffService.findByUserId(pd);
		// 构造变更字典
		PageData change_pd = new PageData();
		change_pd.put("OLD_DEPARTMENT", staff_pd.getString("DEPARTMENT_ID"));
		change_pd.put("OLD_JOBTYPE", staff_pd.getString("JOBTYPE"));
		change_pd.put("NEW_DEPARTMENT", pd.getString("ST2"));
		change_pd.put("NEW_JOBTYPE", pd.getString("ST"));
		change_pd.put("STAFF_ID", pd.getString("STAFF_ID"));
		change_pd.put("USERID", pd.getString("USER_ID"));
		change_pd.put("STATUS", "1");
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		change_pd.put("COMMIT_TIME", formatter.format(date));// 发起时间
		// 记录到数据库
		changeDepartmentService.save(change_pd);
		map.put("result", errInfo);
		return map; // 返回结果
	}

	/**
	 * 未审批变更部门列表
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/notApprovedChangeList")
	@RequiresPermissions("changedept:list")
	@ResponseBody
	public Object notApprovedChangeList(Page page) throws Exception {
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
		List<PageData> userList = changeDepartmentService.notApprovedChangelistPage(page); // 列出未审核列表
		map.put("userList", userList);
		map.put("page", page);
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}


	/**
	 * 根据staffid查询部门变更历史
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/findChangeHistory")
	@ResponseBody
	public Object findChangeHistoryByStaffId() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		System.out.println("id是：" + pd.getString("STAFF_ID"));
		pd = changeDepartmentService.findChangeHistoryByStaffId(pd.getString("STAFF_ID")); // 根据STAFFID读取
		map.put("pd", pd);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 驳回变更请求
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/disallow")
	@ResponseBody
	public Object disallow() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		String IDS = pd.getString("IDS");
		String errInfo = "success";
		if (Tools.notEmpty(IDS)) {
			String ArrayIDS[] = IDS.split(",");
			FHLOG.save(Jurisdiction.getUsername(), "驳回部门变更申请");// 记录日志
			for(int i= 0;i< ArrayIDS.length;i++) {
				// 根据主键查出当前记录
				PageData change_pd = new PageData();
				change_pd = changeDepartmentService.findById(ArrayIDS[i]);
				change_pd.put("STATUS","3");
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				change_pd.put("CHECK_TIME", formatter.format(date)); // 驳回时间
				change_pd.put("CHECK_USERID",Jurisdiction.getUsername());
				// 记录变更
				changeDepartmentService.edit(change_pd);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 批准变更请求
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/allow")
	@ResponseBody
	public Object allow() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		String IDS = pd.getString("IDS");
		String errInfo = "success";
		if (Tools.notEmpty(IDS)) {
			String Array_IDS[] = IDS.split(",");
			FHLOG.save(Jurisdiction.getUsername(), "批准部门变更申请");// 记录日志
			for (int i = 0; i < Array_IDS.length; i++) {
				// 根据主键查出当前记录
				PageData change_pd = new PageData();
				change_pd = changeDepartmentService.findById(Array_IDS[i]);
				change_pd.put("STATUS","2");
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				change_pd.put("CHECK_TIME", formatter.format(date)); // 批准时间
				change_pd.put("CHECK_USERID",Jurisdiction.getUsername());
				// 记录变更
				changeDepartmentService.edit(change_pd);
				// 将新的部门信息更新到STAFF表
				PageData staff_pd = new PageData();
				staff_pd.put("USER_ID",change_pd.getString("USERID"));
				staff_pd = staffService.findByUserId(staff_pd);
				staff_pd.put("JOBTYPE",change_pd.getString("NEW_JOBTYPE"));
				staff_pd.put("DEPARTMENT_ID",change_pd.getString("NEW_DEPARTMENT"));
				staffService.edit(staff_pd);
				// 调整部门权限
				PageData datajur_pd = new PageData();
				datajur_pd.put("DATAJUR_ID",staff_pd.getString("STAFF_ID"));
				datajur_pd.put("DEPARTMENT_IDS", departmentService.getDEPARTMENT_IDS(change_pd.getString("NEW_DEPARTMENT")));		//部门ID集
				datajur_pd.put("STAFF_ID",staff_pd.getString("STAFF_ID"));
				datajur_pd.put("DEPARTMENT_ID",change_pd.getString("NEW_DEPARTMENT"));
				datajurService.edit(datajur_pd);
			}
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}
}
