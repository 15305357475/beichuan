package org.fh.controller.fhoa;

import net.sf.json.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.DelFileUtil;
import org.fh.util.FileDownload;
import org.fh.util.FileUpload;
import org.fh.util.FileUtil;
import org.fh.util.Jurisdiction;
import org.fh.entity.PageData;
import org.fh.service.fhoa.MfolderService;

/**
 * 说明：文件管理 作者：fsci 授权：bsic
 */
@Controller
@RequestMapping("/mfolder")
public class MfolderController extends BaseController {

	@Autowired
	private MfolderService mfolderService;

	/**
	 * 创建目录
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@RequiresPermissions("mfolder:add")
	@ResponseBody
	public Object add() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("MFOLDER_ID", this.get32UUID()); // 主键
		pd.put("FILEPATH", ""); // 路径
		pd.put("CTIME", DateUtil.date2Str(new Date())); // 创建时间
		pd.put("UNAME", Jurisdiction.getName()); // 上传者
		pd.put("MASTER", Jurisdiction.getUsername()); // 所属人
		pd.put("FILESIZE", "");
		pd.put("SHARE", "no");
		mfolderService.save(pd);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 上传文件
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload")
	@RequiresPermissions("mfolder:add")
	@ResponseBody
	public Object add(@RequestParam(value = "FFILE", required = false) MultipartFile file,
			@RequestParam(value = "NAME", required = false) String NAME,
			@RequestParam(value = "PARENT_ID", required = false) String PARENT_ID,
			@RequestParam(value = "REMARKS", required = false) String REMARKS,
			@RequestParam(value = "SHARE", required = false) String SHARE) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String ffile = DateUtil.getDays(), fileName = "";
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getProjectpath() + Const.FILEPATHFILE + ffile; // 文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
			pd.put("FILEPATH", Const.FILEPATHFILE + ffile + "/" + fileName); // 文件路径
			pd.put("NAME", NAME); // 文件名
			pd.put("PARENT_ID", PARENT_ID); // 目录ID
			pd.put("CTIME", DateUtil.date2Str(new Date())); // 创建时间
			pd.put("UNAME", Jurisdiction.getName()); // 上传者,当前用户的姓名
			pd.put("MASTER", Jurisdiction.getUsername()); // 用户名
			pd.put("FILESIZE", FileUtil.getFilesize(filePath + "/" + fileName)); // 文件大小
			pd.put("REMARKS", REMARKS); // 备注
			pd.put("SHARE", SHARE); // 是否共享
			pd.put("MFOLDER_ID", this.get32UUID()); // 主键
			mfolderService.save(pd); // 存入数据库表
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 上传文件
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadAll")
	@ResponseBody
	public Object uploadAll(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "FH_ID", required = false) String MFOLDER_ID) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String ffile = DateUtil.getDays(), fileName = "";
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getProjectpath() + Const.FILEPATHFILE + ffile; // 文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID()); // 执行上传
			pd.put("FILEPATH", Const.FILEPATHFILE + ffile + "/" + fileName); // 文件路径
			pd.put("NAME", (file.getOriginalFilename()).split("\\.")[0]); // 文件名
			pd.put("PARENT_ID", MFOLDER_ID); // 目录ID
			pd.put("CTIME", DateUtil.date2Str(new Date())); // 创建时间
			pd.put("UNAME", Jurisdiction.getName()); // 上传者,当前用户的姓名
			pd.put("MASTER", Jurisdiction.getUsername()); // 用户名
			pd.put("FILESIZE", FileUtil.getFilesize(filePath + "/" + fileName)); // 文件大小
			pd.put("REMARKS", "无"); // 备注
			pd.put("SHARE", "no"); // 是否共享
			pd.put("MFOLDER_ID", this.get32UUID()); // 主键
			mfolderService.save(pd); // 存入数据库表
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	@RequiresPermissions("mfolder:del")
	@ResponseBody
	public Object delete(@RequestParam String MFOLDER_ID, @RequestParam String FILEPATH) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("parentId", MFOLDER_ID);
		if (mfolderService.listByParentId(pd).size() > 0) { // 判断是否有子级，是：不允许删除
			errInfo = "error";
		} else {
			if (Tools.notEmpty(FILEPATH)) {
				DelFileUtil.delFolder(PathUtil.getProjectpath() + FILEPATH.trim()); // 删除文件
			}
			pd.put("MFOLDER_ID", MFOLDER_ID);
			mfolderService.delete(pd);
		}
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
	@RequiresPermissions("mfolder:edit")
	@ResponseBody
	public Object edit() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		mfolderService.edit(pd);
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
	@ResponseBody
	public Object list(Page page) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String SHARE = pd.getString("SHARE");
		String KEYWORDS = pd.getString("KEYWORDS"); // 关键词检索条件
		if (Tools.notEmpty(KEYWORDS))
			pd.put("KEYWORDS", KEYWORDS.trim());
		String MFOLDER_ID = null == pd.get("MFOLDER_ID") ? "" : pd.get("MFOLDER_ID").toString();
		pd.put("MFOLDER_ID", MFOLDER_ID); // 当作上级ID
		if (Tools.notEmpty(SHARE) && "yes".equals(SHARE)) {
			pd.put("SHARE", "yes");
		} else {
			pd.put("USERNAME", "admin".equals(Jurisdiction.getUsername()) ? "" : Jurisdiction.getUsername()); // 除admin用户外，只能查看自己的数据
		}
		page.setPd(pd);
		List<PageData> varList = mfolderService.list(page); // 列出Mfolder列表
		if ("".equals(MFOLDER_ID) || "0".equals(MFOLDER_ID)) {
			map.put("PARENT_ID", "0"); // 上级ID
		} else {
			map.put("PARENT_ID", mfolderService.findById(pd).getString("PARENT_ID")); // 上级ID
		}
		for (int i = 0; i < varList.size(); i++) {
			String FILEPATH = varList.get(i).getString("FILEPATH");
			if (Tools.isEmpty(FILEPATH))
				break;
			String extension_name = FILEPATH.substring(59, FILEPATH.length());// 文件拓展名
			String fileType = "file";
			int zindex1 = "java,php,jsp,html,css,txt,asp".indexOf(extension_name);
			if (zindex1 != -1) {
				fileType = "wenben"; // 文本类型
			}
			int zindex2 = "jpg,gif,bmp,png".indexOf(extension_name);
			if (zindex2 != -1) {
				fileType = "tupian"; // 图片文件类型
			}
			int zindex3 = "rar,zip,rar5".indexOf(extension_name);
			if (zindex3 != -1) {
				fileType = "yasuo"; // 压缩文件类型
			}
			int zindex4 = "doc,docx".indexOf(extension_name);
			if (zindex4 != -1) {
				fileType = "doc"; // doc文件类型
			}
			int zindex5 = "xls,xlsx".indexOf(extension_name);
			if (zindex5 != -1) {
				fileType = "xls"; // xls文件类型
			}
			int zindex6 = "ppt,pptx".indexOf(extension_name);
			if (zindex6 != -1) {
				fileType = "ppt"; // ppt文件类型
			}
			int zindex7 = "pdf".indexOf(extension_name);
			if (zindex7 != -1) {
				fileType = "pdf"; // ppt文件类型
			}
			int zindex8 = "fly,f4v,mp4,m3u8,webm,ogg,avi".indexOf(extension_name);
			if (zindex8 != -1) {
				fileType = "video"; // 视频文件类型
			}
			varList.get(i).put("extension_name", extension_name); // 文件拓展名
			varList.get(i).put("fileType", fileType); // 用于文件图标
		}
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 显示列表ztree
	 * 
	 * @return
	 */
	@RequestMapping(value = "/listTree")
	@ResponseBody
	public Object listTree() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String SHARE = pd.getString("SHARE");
		if (!(Tools.notEmpty(SHARE) && "yes".equals(SHARE))) {
			pd.put("USERNAME", "admin".equals(Jurisdiction.getUsername()) ? "" : Jurisdiction.getUsername()); // 除admin用户外，只能查看自己的数据
		}
		pd.put("parentId", "0");
		pd.put("SHARE", SHARE);
		JSONArray arr = JSONArray.fromObject(mfolderService.listTree(pd, SHARE));
		String json = arr.toString();
		json = json.replaceAll("MFOLDER_ID", "id").replaceAll("PARENT_ID", "pId").replaceAll("NAME", "name")
				.replaceAll("subMfolder", "nodes").replaceAll("hasMfolder", "checked").replaceAll("treeurl", "url");
		map.put("zTreeNodes", json);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 预览txt,java,php,等文本文件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/viewTxt")
	@ResponseBody
	public Object viewTxt() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String encoding = pd.getString("encoding");
		pd = mfolderService.findById(pd); // 根据ID读取
		String code = FileUtil.readTxtFileAll(PathUtil.getProjectpath() + pd.getString("FILEPATH"), encoding);
		map.put("code", code);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 批量操作
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/makeAll")
	@RequiresPermissions("mfolder:edit")
	@ResponseBody
	public Object deleteAll() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (Tools.notEmpty(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			pd.put("IDS", ArrayDATA_IDS);
			mfolderService.makeAll(pd);
			errInfo = "success";
		} else {
			errInfo = "error";
		}
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 下载
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/download")
	public void download(HttpServletResponse response) throws NofileException {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = mfolderService.findById(pd);
			String FILEPATH = pd.getString("FILEPATH");
			String fileName = pd.getString("NAME");
			FileDownload.fileDownload(response, PathUtil.getProjectpath() + FILEPATH,
					fileName + FILEPATH.substring(58, FILEPATH.length()));
		} catch (Exception e) {
			throw new NofileException("=========要下载的文件已经没有了=========");
		}
	}

	/**
	 * 根据文件相对路径下载文件--大型物件吊装探伤报告
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadByPath")
	public void downloadByPath(HttpServletResponse response) throws NofileException {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			String FILEPATH = pd.getString("FILEPATH");
			FileDownload.fileDownload(response, PathUtil.getProjectpath() + FILEPATH,
					FILEPATH.substring(58, FILEPATH.length()));
		} catch (Exception e) {
			throw new NofileException("=========要下载的文件已经没有了=========");
		}
	}

	/**
	 * 根据文件相对路径下载文件：保修管理平台收件箱附件
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadByPath2")
	public void downloadByPath2(HttpServletResponse response) throws NofileException {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			String FILEPATH = pd.getString("FILEPATH");
			FileDownload.fileDownload(response, PathUtil.getProjectpath() + FILEPATH,
					FILEPATH.substring(76, FILEPATH.length()));
		} catch (Exception e) {
			throw new NofileException("=========要下载的文件已经没有了=========");
		}
	}

	/**
	 * 根据文件相对路径下载文件：保修管理平台发件箱附件
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadByPath3")
	public void downloadByPath3(HttpServletResponse response) throws NofileException {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			String FILEPATH = pd.getString("FILEPATH");
			FileDownload.fileDownload(response, PathUtil.getProjectpath() + FILEPATH,
					FILEPATH.substring(42, FILEPATH.length()));
		} catch (Exception e) {
			throw new NofileException("=========要下载的文件已经没有了=========");
		}
	}

	/**
	 * 根据文件相对路径下载文件：设备巡检视频下载
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadByPath4")
	public void downloadByPath4(HttpServletResponse response) throws NofileException {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			String FILEPATH = pd.getString("FILEPATH");
			System.out.println(FILEPATH);
			System.out.println(FILEPATH.substring(38, FILEPATH.length()));
			FileDownload.fileDownload(response, PathUtil.getProjectpath() + FILEPATH,
					FILEPATH.substring(38, FILEPATH.length()));
		} catch (Exception e) {
			throw new NofileException(e);
		}
	}
}

/*
 * 自定义异常类
 */
class NofileException extends Exception {

	private static final long serialVersionUID = 1L;

	public NofileException() {
		super();
	}

	public NofileException(String message) {
		super(message);
	}

	public NofileException(String message, Throwable cause) {
		super(message, cause);
	}

	public NofileException(Throwable cause) {
		super(cause);
	}

}
