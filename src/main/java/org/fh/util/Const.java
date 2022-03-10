package org.fh.util;

/**
 * 说明：常量 作者：f-sci 授权：bsic
 */
public class Const {

	public static final String SESSION_USER = "SESSION_USER"; // session用的用户
	public static final String SESSION_USERROL = "SESSION_USERROL"; // 用户对象(包含角色信息)
	public static final String SESSION_ROLE_RIGHTS = "SESSION_ROLE_RIGHTS"; // 角色菜单权限
	public static final String SHIROSET = "SHIROSET"; // 菜单权限标识
	public static final String SESSION_USERNAME = "USERNAME"; // 用户名
	public static final String SESSION_U_NAME = "SESSION_U_NAME"; // 用户姓名
	public static final String SESSION_ROLE = "SESSION_ROLE"; // 主职角色信息
	public static final String SESSION_RNUMBERS = "RNUMBERS"; // 角色编码数组
	public static final String SESSION_ALLMENU = "SESSION_ALLMENU"; // 全部菜单
	public static final String SKIN = "SKIN"; // 用户皮肤

	public static final String ONECARD_URL = "http://192.168.200.123:80";// 一卡通服务器地址

	public static final String SYSSET = "config/sysSet.ini"; // 系统设置配置文件路径
	public static final String SYSNAME = "sysName"; // 系统名称
	public static final String SHOWCOUNT = "showCount"; // 每页条数

	public static final String FILEPATHFILE = "uploadFiles/file/"; // 文件上传路径
	
	public static final String TSBG_FILEPATHFILE = "uploadFiles/file/TSBG/"; // 探伤报告附件上传路径
	
	public static final String LIVEPHOTOS = "uploadFiles/file/LIVEPHOTOS/"; // 作业审批流程现场施工照片
	
	public static final String MAIL_FILES = "uploadFiles/file/mailfiles/rece/"; // 保修项目下载的邮件附件路径
	public static final String MAIL_FILES_SENT = "uploadFiles/file/mailfiles/sent/";// 保修项目发出的邮件附件路径
	public static final String WARRANTY_FILES = "uploadFiles/file/warrantyfiles/"; // 保修项目保修单附件路径
	
	public static final String FILEPATHIMG = "uploadFiles/imgs/"; // 图片上传路径
	public static final String FACEBOOK = "uploadFiles/facebook/";// 一卡通中的人脸数据
	public static final String FASTPASS_IMGS = "uploadFiles/file/fastpassIMG/"; // 出门证图片存储路径
	public static final String INS_IMGS = "uploadFiles/file/insimgs/"; // 隐患排查图片存储路径
	public static final String INS_AUDIOS = "uploadFiles/file/insaudios/"; // 隐患排查音频存储路径
	
	public static final String SBXJ_IMGS = "uploadFiles/file/sbxjimgs/"; // 设备巡检图片存储路径
	public static final String SBXJ_AUDIOS = "uploadFiles/file/sbxjaudios/"; // 设备巡检音频存储路径
	public static final String SBXJ_VIDEOS = "uploadFiles/file/sbxjvideos/"; // 设备巡检视频存储路径
	
	public static final String INS_Export_Temp = "uploadFiles/file/InsExportTemplate/InsExportTemplate.xls"; // 隐患排查导出表模板

	public static final String FILEACTIVITI = "uploadFiles/activitiFile/"; // 工作流生成XML和PNG目录

	public static final String DEPARTMENT_IDS = "DEPARTMENT_IDS"; // 当前用户拥有的最高部门权限集合
	public static final String DEPARTMENT_ID = "DEPARTMENT_ID"; // 当前用户拥有的最高部门权限

	public static final String SESSION_USER_DEPT = "SESSION_USER_DEPT"; // 当前用户的隶属部门/服务部门

	public static final String authorization = "authorization"; // 找回密码当次操作的授权信息
	
	public static final String INFOCHECKPHOTO = "uploadFiles/photo/infocheck/"; // 人员信息核验人员照片存放路径
}
