package org.fh.controller.ins;

import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.FileUpload;
import org.fh.util.ObjectExcelView;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.ins.InsMediaService;

/**
 * 说明：隐患排查系统媒体表 作者：fsci 时间：2021-04-13 授权：bsic
 */
@Controller
@RequestMapping("/insmedia")
public class InsMediaController extends BaseController {

	@Autowired
	private InsMediaService insmediaService;

	/**
	 * 新增
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("insmedia:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("INSMEDIA_ID", this.get32UUID()); // 主键
		pd.put("INS_ID", ""); // 隐患主表，外键
		pd.put("PROC_INST_ID_", ""); // 主流程实例ID，外键
		pd.put("TASK_ID_", ""); // 流程引擎任务ID，外键
		pd.put("NODE", ""); // 当前媒体产生节点
		pd.put("CREATE_USER", ""); // 媒体创建人
		pd.put("CREATE_DATE", DateUtil.date2Str(new Date())); // 媒体创建日期
		pd.put("MEDIA_TYPE", ""); // 媒体类型：I：图；A：音
		pd.put("MEDIA_NAME", ""); // 媒体文件名
		pd.put("MEDIA_PATH", ""); // 媒体文件存储路径
		pd.put("STATUS", ""); // 媒体状态.0:删；1：正常
		insmediaService.save(pd);
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
	@RequiresPermissions("insmedia:del")
	@ResponseBody
	public Object delete() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		insmediaService.delete(pd);
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
	@RequiresPermissions("insmedia:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		insmediaService.edit(pd);
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
	@RequiresPermissions("insmedia:list")
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData> varList = insmediaService.list(page); // 列出InsMedia列表
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
	@RequiresPermissions("insmedia:edit")
	@ResponseBody
	public Object goEdit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = insmediaService.findById(pd); // 根据ID读取
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
	@RequiresPermissions("insmedia:del")
	@ResponseBody
	public Object deleteAll() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (Tools.notEmpty(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			insmediaService.deleteAll(ArrayDATA_IDS);
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
		titles.add("隐患主表，外键"); // 1
		titles.add("主流程实例ID，外键"); // 2
		titles.add("流程引擎任务ID，外键"); // 3
		titles.add("当前媒体产生节点"); // 4
		titles.add("媒体创建人"); // 5
		titles.add("媒体创建日期"); // 6
		titles.add("媒体类型：I：图；A：音"); // 7
		titles.add("媒体文件名"); // 8
		titles.add("媒体文件存储路径"); // 9
		titles.add("媒体状态.0:删；1：正常"); // 10
		dataMap.put("titles", titles);
		List<PageData> varOList = insmediaService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("INS_ID")); // 1
			vpd.put("var2", varOList.get(i).getString("PROC_INST_ID_")); // 2
			vpd.put("var3", varOList.get(i).getString("TASK_ID_")); // 3
			vpd.put("var4", varOList.get(i).getString("NODE")); // 4
			vpd.put("var5", varOList.get(i).getString("CREATE_USER")); // 5
			vpd.put("var6", varOList.get(i).getString("CREATE_DATE")); // 6
			vpd.put("var7", varOList.get(i).getString("MEDIA_TYPE")); // 7
			vpd.put("var8", varOList.get(i).getString("MEDIA_NAME")); // 8
			vpd.put("var9", varOList.get(i).getString("MEDIA_PATH")); // 9
			vpd.put("var10", varOList.get(i).getString("STATUS")); // 10
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 上传图片
	 */
	@RequestMapping(value = "/uploadImgs")
	@ResponseBody
	public Object uploadImgs(HttpServletResponse response, MultipartHttpServletRequest request,@RequestParam("file") MultipartFile[] files) throws Exception {

		int fileCount = Integer.parseInt(request.getParameter("fileCount"));
		List<MultipartFile> MultipartFiles = new ArrayList<MultipartFile>();
		if(files.length==0){
			for (int i = 0; i < fileCount; i++) {
				MultipartFiles.add(request.getFile("file[" + i + "]"));
			}
		}else {
			MultipartFiles = Arrays.asList(files);
		}

		List<String> INSMEDIA_NAMES = new ArrayList<String>(); // 附件文件名
		List<String> INSMEDIA_PATHS = new ArrayList<String>(); // 附件路径
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		// 获取当前年月
		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		// 构造存储路径
		String filePath = PathUtil.getProjectpath() + Const.INS_IMGS + year + "//" + month + "//"; // 文件上传绝对路径
		String shortFilePath = Const.INS_IMGS + year + "//" + month + "//"; // 文件上传相对路径
		// 初始化变量
		String fileName = "";
		String OriginalFilename = "";
		if (null != MultipartFiles && MultipartFiles.size() > 0) {
			for (MultipartFile file : MultipartFiles) {
				fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
				OriginalFilename = file.getOriginalFilename();
				INSMEDIA_NAMES.add(OriginalFilename);
				INSMEDIA_PATHS.add(shortFilePath + fileName);
			}
		} else {
			errInfo = "error";
		}
		// 返回结果
		map.put("result", errInfo);
		map.put("MEDIA_TYPE", "I");// 图片类型
		map.put("INSMEDIA_NAMES", INSMEDIA_NAMES);
		map.put("INSMEDIA_PATHS", INSMEDIA_PATHS);
		return map;
	}

	/**
	 * 上传录音
	 */
	@RequestMapping(value = "/uploadAudio")
	@ResponseBody
	public Object uploadAudio(@RequestParam(value = "audioFile", required = false) MultipartFile file)
			throws Exception {
		String INSMEDIA_NAME = ""; // 附件文件名
		String INSMEDIA_PATH = ""; // 附件路径
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		// 获取当前年月
		String year = DateUtil.getYear();
		String month = DateUtil.getDay().substring(5, 7);
		// 构造存储路径
		String filePath = PathUtil.getProjectpath() + Const.INS_AUDIOS + year + "//" + month + "//"; // 文件上传绝对路径
		String shortFilePath = Const.INS_AUDIOS + year + "//" + month + "//"; // 文件上传相对路径
		// 初始化变量
		String fileName = "";
		String OriginalFilename = "";
		if (null != file && !file.isEmpty()) {
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
			OriginalFilename = file.getOriginalFilename();
			INSMEDIA_NAME = OriginalFilename;
			INSMEDIA_PATH = shortFilePath + fileName;
		} else {
			errInfo = "error";
		}
		// 返回结果
		map.put("result", errInfo);
		map.put("MEDIA_TYPE", "A");// 音频类型
		map.put("INSMEDIA_NAME", INSMEDIA_NAME);
		map.put("INSMEDIA_PATH", INSMEDIA_PATH);
		return map;
	}
}
