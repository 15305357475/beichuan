package org.fh.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

/**
 * 说明：权限工具类
 * 作者：f-sci
 * 授权：bsic
 */
public class Jurisdiction {
	
	/**shiro管理的session
	 * @return
	 */
	public static Session getSession(){
		return SecurityUtils.getSubject().getSession();
	}
	
	/**获取当前登录的用户名
	 * @return
	 */
	public static String getUsername(){
		return getSession().getAttribute(Const.SESSION_USERNAME).toString();
	}
	
	/**获取当前登录的姓名
	 * @return
	 */
	public static String getName(){
		return getSession().getAttribute(Const.SESSION_U_NAME).toString();
	}
	
	/**获取当前登录用户的角色编码
	 * @return
	 */
	public static String getRnumbers(){
		return getSession().getAttribute(Const.SESSION_RNUMBERS).toString();
	}
	
	/**获取用户的最高组织机构权限集合
	 * @return
	 */
	public static String getDEPARTMENT_IDS(){
		return getSession().getAttribute(Const.DEPARTMENT_IDS).toString();
	}
	
	/**获取用户的最高组织机构权限
	 * @return
	 */
	public static String getDEPARTMENT_ID(){
		return getSession().getAttribute(Const.DEPARTMENT_ID).toString();
	}

	/**获取当前用户部门
	 * @return
	 */
	public static String getUSER_DEPT(){
		return getSession().getAttribute(Const.SESSION_USER_DEPT).toString();
	}
}
