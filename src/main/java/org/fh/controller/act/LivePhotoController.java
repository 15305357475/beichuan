package org.fh.controller.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.fh.controller.base.BaseController;
import org.fh.util.ImageAnd64Binary;
import org.fh.util.PathUtil;
import org.fh.entity.PageData;
import org.fh.service.act.LivePhotoService;

/**
 * 说明：作业审批流程现场图 作者：fsci 时间：2021-02-03 授权：bsic
 */
@Controller
@RequestMapping("/livephoto")
public class LivePhotoController extends BaseController {

	@Autowired
	private LivePhotoService livephotoService;

	/**
	 * 列表
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varList = livephotoService.listAll(pd); // 列出LivePhoto列表;
		if(varList.size() > 0) {
			// 将每一条记录的图片转码成base64
			List<String> base64List = new ArrayList<String>();
			for (PageData pageData : varList) {
				String onePath = PathUtil.getProjectpath() + pageData.getString("PTOTO_PATH");
				String Base64String = ImageAnd64Binary.getImageStr(onePath);
				base64List.add(Base64String);
			}
			map.put("base64List", base64List);
		}
		map.put("varList", varList);
		map.put("result", errInfo);
		return map;
	}
}
