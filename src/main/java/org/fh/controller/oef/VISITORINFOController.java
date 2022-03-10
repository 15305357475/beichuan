package org.fh.controller.oef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.oef.VISITORINFOService;

/** 
 * 说明：外来人员入厂人员信息表
 * 作者：fsci
 * 时间：2021-08-16
 * 授权：bsic
 */
@Controller
@RequestMapping("/visitorinfo")
public class VISITORINFOController extends BaseController {
	
	@Autowired
	private VISITORINFOService visitorinfoService;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("visitorinfo:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData>	varList = visitorinfoService.list(page);	//列出VISITORINFO列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
}
