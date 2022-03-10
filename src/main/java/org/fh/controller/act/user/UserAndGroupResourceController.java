package org.fh.controller.act.user;

import org.apache.commons.lang3.StringUtils;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.service.system.RoleService;
import org.fh.service.system.UsersService;
import org.fh.util.Tools;
import org.flowable.engine.ManagementService;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.model.GroupRepresentation;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

/**
 * 说明：流程编辑器分配用户
 * 作者：x-sci
 * 授权：bsic
 */
@RestController
@RequestMapping("app")
public class UserAndGroupResourceController {

	@Autowired
	protected ManagementService managementService;
	@Autowired
	protected IdmIdentityService idmIdentityService;
	@Autowired
    private RoleService roleService;
	@Autowired
    private UsersService usersService;

	@RequestMapping(value = "/rest/editor-groups", method = RequestMethod.GET)
	public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter, Page page) {
		if (StringUtils.isNotBlank(filter)) {
			filter = filter.trim();
			List<GroupRepresentation> result = new ArrayList<>();
			PageData pd = new PageData();
			String KEYWORDS = filter;						//关键词检索条件
			if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
			page.setShowCount(1000);
			page.setPd(pd);
			try {
				List<PageData> roleList = roleService.roleListWindow(page);//列出所有角色
				for(PageData rpd : roleList) {
					GroupRepresentation rg = new GroupRepresentation();
					rg.setId(rpd.getString("RNUMBER"));
					rg.setName(rpd.getString("ROLE_NAME"));
					result.add(rg);
				}
			} catch (Exception e) {}		
			return new ResultListDataRepresentation(result);
		}
		return null;
	}

	@RequestMapping(value = "/rest/editor-users", method = RequestMethod.GET)
	public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter, Page page) {
		if (StringUtils.isNotBlank(filter)) {
			filter = filter.trim();
			List<UserRepresentation> userRepresentations = new ArrayList<>();
			PageData pd = new PageData();
			String KEYWORDS = filter;						//关键词检索条件
			if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
			page.setShowCount(1000);
			page.setPd(pd);
			try {
				List<PageData>	userList = usersService.listUsersBystaff(page);//列出用户列表
				for(PageData upd : userList) {
					UserRepresentation ur = new UserRepresentation();
					ur.setId(upd.getString("USERNAME"));
					ur.setFirstName(upd.getString("USERNAME"));
					ur.setFullName("");
					ur.setLastName("");
					userRepresentations.add(ur);
				}
			} catch (Exception e) {}	
			return new ResultListDataRepresentation(userRepresentations);
		}
		return null;
	}

}