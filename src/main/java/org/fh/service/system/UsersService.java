package org.fh.service.system;

import java.util.List;

import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.system.User;

/**
 * 说明：用户服务接口
 * 作者：f-sci
 * 授权：bsic
 */
public interface UsersService {
	
	/**通过用户名获取用户信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData findByUsername(PageData pd)throws Exception;

    /**通过openid获取用户信息
     * @param pd
     * @return
     * @throws Exception
     */
    public PageData findByOpenId(PageData pd)throws Exception;
	
	/**通过身份证获取用户信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData findByNumber(PageData pd)throws Exception;
	
	/**通过用户ID获取用户信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**用户列表
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> userlistPage(Page page)throws Exception;
	
	/**通过用户ID获取用户信息和角色信息
	 * @param USER_ID
	 * @return
	 * @throws Exception
	 */
	public User getUserAndRoleById(String USER_ID) throws Exception;
	
	/**保存用户IP
	 * @param pd
	 * @throws Exception
	 */
	public void saveIP(PageData pd)throws Exception;
	
	/**通过邮箱或者用户姓名获取数据
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> findByEmail(PageData pd)throws Exception;
	
	/**通过编码获取数据
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData findByNumbe(PageData pd) throws Exception;
	
	/**列出某角色下的所有用户
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> listAllUserByRoldId(PageData pd) throws Exception;
	
	/**用户列表(全部)
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> listAllUser(PageData pd)throws Exception;
	
	/**根据角色编码查询拥有该角色的用户列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> listAllUserByRNUMBER(PageData pd)throws Exception;
	
	/**用户列表(弹窗选择用)
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> listUsersBystaff(Page page)throws Exception;
	
	/**保存用户
	 * @param pd
	 * @throws Exception
	 */
	public void saveUser(PageData pd)throws Exception;
	
	/**保存用户系统皮肤
	 * @param pd
	 * @throws Exception
	 */
	public void saveSkin(PageData pd)throws Exception;
	
	/**修改用户
	 * @param pd
	 * @throws Exception
	 */
	public void editUser(PageData pd)throws Exception;
	
	/**删除用户
	 * @param pd
	 * @throws Exception
	 */
	public void deleteUser(PageData pd)throws Exception;
	
	/**批量删除用户
	 * @param pd
	 * @throws Exception
	 */
	public void deleteAllUser(String[] USER_IDS)throws Exception;
	
	/**查询指定部门下未审批的申请注册用户
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> notApprovedUserlistPage(Page page)throws Exception;
	
	/**查询指定用户主职和副职角色
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public PageData getUserRoles(PageData pd)throws Exception;
	
	/**查询指定用户主职和副职角色
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getAllMember(PageData pd)throws Exception;
}
