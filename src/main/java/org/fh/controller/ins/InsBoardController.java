package org.fh.controller.ins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.fh.controller.act.AcStartController;
import org.fh.entity.PageData;
import org.fh.service.ins.InsBoardService;

/**
 * 说明：隐患排查看板 作者：fsci 时间：2021-08-30 授权：bsic
 */
@Controller
@RequestMapping("/insboard")
public class InsBoardController extends AcStartController {

	@Autowired
	private InsBoardService insboardService;

	/**
	 * 隐患类型分布饼状图
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/TypeBoard")
	@ResponseBody
	public Object TypeBoard() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		List<PageData> varlist = insboardService.TypeBoard();
		map.put("varlist", varlist);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 隐患状态分布饼状图
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/StatusBoard")
	@ResponseBody
	public Object StatusBoard() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		List<PageData> varlist = insboardService.StatusBoard();
		map.put("varlist", varlist);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 隐患发起Top直方图
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/LaunchTop")
	@ResponseBody
	public Object LaunchTop() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varlist = insboardService.LaunchTop(pd);
		map.put("varlist", varlist);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 隐患整改Top直方图
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/AbarbeitungTop")
	@ResponseBody
	public Object AbarbeitungTop() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> varlist = insboardService.AbarbeitungTop(pd);
		map.put("varlist", varlist);
		map.put("result", errInfo); // 返回结果
		return map;
	}

}
