package org.fh.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.fh.controller.base.BaseController;
import org.fh.entity.system.User;
import org.fh.service.fhoa.DepartmentService;
import org.fh.service.fhoa.RegisteredService;
import org.fh.service.fhoa.StaffService;
import org.fh.service.system.FHlogService;
import org.fh.service.system.UsersService;
import org.fh.util.Const;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Registered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 说明：登录处理类 作者：f-sci 授权：bsic
 */
@Api("用户注册登录找回密码接口")
@Controller
@RequestMapping("/admin")
public class LoginController extends BaseController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private FHlogService FHLOG;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private RegisteredService registeredService;
    @Autowired
    private StaffService staffService;

    /**
     * 请求登录验证用户接口
     *
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "登录", notes = "请求登录验证用户接口，校验登录是否成功")
    @ApiImplicitParam(name = "KEYDATA", value = "用户名密码混淆码组合", paramType = "query", required = true, dataType = "String")
    @RequestMapping(value = "/check", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object check() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String errInfo = "success";
        String KEYDATA[] = pd.getString("KEYDATA").replaceAll("BSIC", "").split(",FSCI,");
        String VERSION = pd.getString("VERSION");// 当前版本号
        String CID = pd.getString("CID");// 当前客户端CID
        if (null != KEYDATA && KEYDATA.length == 2) {
            String USERNAME = KEYDATA[0]; // 登录过来的用户名
            String PASSWORD = KEYDATA[1]; // 登录过来的密码
            // 如果登录名为纯数字，且长度为8位，则登录名为工号，需要转换为真实姓名+身份证后四位的正常登录名
            if (USERNAME.length() == 8 && USERNAME.matches("[0-9]+")) {
                // 根据工号从白名单中查出基本信息
                PageData temp = registeredService.findById(USERNAME);
                if (temp != null) {
                    String NAME = temp.getString("NAME");
                    String CARD = temp.getString("CARD");
                    USERNAME = NAME + CARD.substring(14, CARD.length());
                }
            }
            String tokenstr = "";
            if(pd.get("OPEN_ID")==null || pd.get("OPEN_ID").equals("")){
                tokenstr = new SimpleHash("SHA-1", USERNAME, PASSWORD).toString();
            }else{
                tokenstr = PASSWORD;
            }
            UsernamePasswordToken token = new UsernamePasswordToken(USERNAME,tokenstr);
            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token); // 这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中
                map.put("token", (String) subject.getSession().getId());
            } catch (UnknownAccountException uae) {
                errInfo = "usererror";
            } catch (IncorrectCredentialsException ice) {
                errInfo = "usererror";
            } catch (LockedAccountException lae) {
                errInfo = "usererror";
            } catch (ExcessiveAttemptsException eae) {
                errInfo = "usererror";
            } catch (DisabledAccountException sae) {
                errInfo = "usererror";
            } catch (AuthenticationException ae) {
                errInfo = "usererror";
            }
            if (subject.isAuthenticated()) { // 验证是否登录成功
                removeSession(USERNAME);
                Session session = Jurisdiction.getSession();
                // 设置session有效期
                session.setTimeout(129600000L);// 单位ms,36个小时
                pd.put("USERNAME", USERNAME);
                pd = usersService.findByUsername(pd);
                User user = new User();
                user.setPHONE(pd.getString("PHONE"));
                user.setUSER_ID(Integer.parseInt(pd.get("USER_ID").toString()));
                user.setUSERNAME(pd.getString("USERNAME"));
                user.setPASSWORD(pd.getString("PASSWORD"));
                user.setNAME(pd.getString("NAME"));
                user.setROLE_ID(pd.getString("ROLE_ID"));
                user.setLAST_LOGIN(pd.getString("LAST_LOGIN"));
                user.setIP(pd.getString("IP"));
                user.setSTATUS(pd.getString("STATUS"));
                String defaultVersion = pd.getString("VERSION");
                String defaultCid = pd.getString("CID");
                // 验证员工部门信息并写入session
                PageData staff_pd = new PageData();
                staff_pd.put("USER_ID", USERNAME);
                PageData staff_pd2 = new PageData();
                staff_pd2 = staffService.findByUserId(staff_pd);
                if (null != staff_pd2) {
                    // 递归查询所在部门
                    String department_id = staff_pd2.getString("DEPARTMENT_ID");
                    PageData dept_pd = new PageData();
                    dept_pd = departmentService.RecursionUpwardById(department_id, 2);
                    if (null != dept_pd) {
                        session.setAttribute(Const.SESSION_USER_DEPT, dept_pd.get("DEPARTMENT_ID")); // 把当前用户的部门放入session
                    } else {
                        session.setAttribute(Const.SESSION_USER_DEPT, ""); // 查不到的写入""
                    }
                } else {
                    session.setAttribute(Const.SESSION_USER_DEPT, ""); // 查不到的写入""
                }
                // 记录版本号
                if (!VERSION.equals("null")) {
                    session.setAttribute("VERSION", VERSION);
                } else {
                    session.setAttribute("VERSION", defaultVersion);
                }
                // 记录客户端CID
                if (!CID.equals("null")) {
                    session.setAttribute("CID", CID);
                } else {
                    session.setAttribute("CID", defaultCid);
                }
                session.setAttribute(Const.SESSION_USER, user); // 把当前用户放入session
                FHLOG.save(USERNAME, "成功登录系统,版本：" + VERSION); // 记录日志
            } else {
                token.clear();
                errInfo = "usererror";
            }
            if (!"success".equals(errInfo))
                FHLOG.save(USERNAME, "尝试登录系统失败,用户名密码错误,无权限。版本：" + VERSION);
        } else {
            errInfo = "error"; // 缺少参数
        }
        map.put("result", errInfo);
        return map;
    }

    /**
     * 系统用户注册接口
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/register", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object register(@RequestParam("callback") String callback) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String result = "00";
        if (Tools.checkKey("USERNAME", pd.getString("FKEY"))) { // 检验请求key值是否合法，防止恶意注册
            // 检查身份证信息是否在白名单中
            PageData pd2 = new PageData();
            pd2 = registeredService.findById(pd.getString("ID"));
            if (null == pd2) {
                result = "02";// 身份信息未授权
            } else {
                Registered reg = new Registered();
                reg.setID(Integer.parseInt(pd2.get("ID").toString()));// 主键
                reg.setCARD(pd.getString("ID"));// 身份证
                reg.setNAME(pd2.getString("NAME"));
                reg.setPHONE(pd.getString("PHONE"));// 根据用户填写的最新的手机号更新白名单手机号
                reg.setREGISTERED(pd2.getString("REGISTERED"));
                if (reg.getREGISTERED().equals("0") || reg.getREGISTERED().equals("3")) {
                    if (reg.getNAME().equals(pd.getString("NAME"))) {
                        // 允许注册
                        // pd.put("USER_ID", this.get32UUID()); //ID 主键自增
                        pd.put("ROLE_ID", "BASE_ROLE"); // 角色ID BASE_ROLE 为普通注册用户
                        pd.put("NUMBER", pd.getString("ID")); // 身份证号
                        pd.put("PHONE", pd.getString("PHONE")); // 手机号
                        pd.put("BZ", pd.getString("TYPE")); // 注册类型
                        pd.put("LAST_LOGIN", ""); // 最后登录时间
                        pd.put("IP", ""); // IP
                        pd.put("STATUS", "2"); // 状态2:等待审核
                        pd.put("SKIN",
                                "pcoded-navbar navbar-image-3,navbar pcoded-header navbar-expand-lg navbar-light header-dark,"); // 用户默认皮肤
                        pd.put("EMAIL", "");
                        pd.put("ROLE_IDS", "");
                        pd.put("REGISTERED_ID", reg.getID()); // 白名单主键
                        pd.put("PASSWORD",
                                new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString()); // 密码加密
                        if (null == usersService.findByUsername(pd)) { // 判断用户名是否存在
                            usersService.saveUser(pd); // 执行保存
                            // 更新白名单表为已注册等待审核状态
                            reg.setREGISTERED("2");
                            registeredService.updateStatus(reg);
                            // 将数据绑定到staff表
                            PageData pd3 = new PageData();
                            pd3.put("STAFF_ID", this.get32UUID());// 主键ID
                            pd3.put("NAME", reg.getNAME());
                            pd3.put("NAME_EN", "");
                            pd3.put("BIANMA", "");
                            pd3.put("DEPARTMENT_ID", pd.getString("ST2"));// 服务部门
                            pd3.put("FUNCTIONS", "");
                            pd3.put("TEL", pd.getString("PHONE"));
                            pd3.put("EMAIL", "");
                            pd3.put("SEX", pd.getString("SEX"));
                            pd3.put("BIRTHDAY", "");
                            pd3.put("NATION", "");
                            pd3.put("JOBTYPE", pd.getString("ST"));
                            pd3.put("JOBJIONTIME", "");
                            pd3.put("FADDESS", "");
                            pd3.put("POLITICAL", "");
                            pd3.put("SFID", reg.getCARD());
                            pd3.put("MARITAL", "");
                            pd3.put("DJIONTIME", "");
                            pd3.put("POST", "");
                            pd3.put("POJIONTIME", "");
                            pd3.put("EDUCATION", "");
                            pd3.put("SCHOOL", "");
                            pd3.put("FTITLE", "");
                            pd3.put("CERTIFICATE", "");
                            pd3.put("CONTRACTLENGTH", 0);
                            pd3.put("CSTARTTIME", "");
                            pd3.put("CENDTIME", "");
                            pd3.put("ADDRESS", "");
                            pd3.put("USER_ID", pd.getString("USERNAME"));// USER表的username字段
                            pd3.put("BZ", pd.getString("TYPE"));// 员工类型：本工、派遣工……
                            pd3.put("BASESALARY", 0);
                            staffService.save(pd3);
                            // 记录日志
                            FHLOG.save(pd.getString("USERNAME"), "新注册");
                        } else {
                            result = "01"; // 用户名已存在
                        }
                    } else {
                        result = "03";// 真实姓名和授权注册的姓名不符
                    }
                } else if (reg.getREGISTERED().equals("1")) {
                    result = "06";// 已注册已审核，可以直接登录
                } else if (reg.getREGISTERED().equals("2")) {
                    result = "07";// 等待审核中
                } else {
                    result = "08";// 未知错误
                }
            }
        } else {
            result = "05"; // 不合法的注册
        }
        map.put("result", result);
        JSONObject sresult = JSONObject.fromObject(map);
        return callback + "(" + sresult.toString() + ")";
    }

    /**
     * 判断是否登录状态
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/islogin")
    @ResponseBody
    public Object islogin() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String errInfo = "success";
        Session session = Jurisdiction.getSession();
        if (null == session.getAttribute(Const.SESSION_USERNAME)) {
            errInfo = "errer";
        }
        map.put("result", errInfo);
        return map;
    }

    /**
     * 清理session
     */
    public void removeSession(String USERNAME) {
        Session session = Jurisdiction.getSession(); // 以下清除session缓存
        session.removeAttribute(Const.SESSION_USER);
        session.removeAttribute(USERNAME + Const.SESSION_ROLE_RIGHTS);
        session.removeAttribute(USERNAME + Const.SESSION_ALLMENU);
        session.removeAttribute(USERNAME + Const.SHIROSET);
        session.removeAttribute(Const.SESSION_USERNAME);
        session.removeAttribute(Const.SESSION_U_NAME);
        session.removeAttribute(Const.SESSION_USERROL);
        session.removeAttribute(Const.SESSION_RNUMBERS);
        session.removeAttribute(Const.SKIN);
        session.removeAttribute(Const.SESSION_USER_DEPT);
    }

    /**
     * 获取系统内的全部公司
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/getAllCompamy")
    @ResponseBody
    public Object getAllCompamy() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String COM_TYPE = pd.getString("COM_TYPE");
        String errInfo = "success";
        List<PageData> zdepartmentPdList = new ArrayList<PageData>();
        JSONArray arr = JSONArray.fromObject(departmentService.getAllCompamyToSelect(COM_TYPE, zdepartmentPdList));
        map.put("zTreeNodes", (null == arr ? "" : "{\"treeNodes\":" + arr.toString() + "}"));
        map.put("result", errInfo); // 返回结果
        return map;
    }

    /**
     * 获取本工组织架构下的所有部门
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/getAllDept")
    @ResponseBody
    public Object getDepartmenttree() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String COM_TYPE = pd.getString("COM_TYPE");
        String errInfo = "success";
        List<PageData> zdepartmentPdList = new ArrayList<PageData>();
        JSONArray arr = JSONArray.fromObject(departmentService.getAllCompamyToSelect(COM_TYPE, zdepartmentPdList));
        map.put("zTreeNodes", (null == arr ? "" : "{\"treeNodes\":" + arr.toString() + "}"));
        map.put("result", errInfo); // 返回结果
        return map;
    }

    /**
     * 找回密码身份验证接口
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/checkinfo", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object checkinfo(@RequestParam("callback2") String callback) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String result = "00";
        Object userID = -1;
        long timestamp = 1L;
        if (Tools.checkKey("USERNAME", pd.getString("FKEY"))) { // 检验请求key值是否合法，防止恶意注册
            // 检查身份证信息是否在白名单中
            PageData pd2 = new PageData();
            pd2 = registeredService.findById(pd.getString("ID"));
            if (null == pd2) {
                result = "02";// 身份信息未授权
            } else {
                Registered reg = new Registered();
                reg.setID(Integer.parseInt(pd2.get("ID").toString()));// 主键
                reg.setCARD(pd.getString("ID"));// 身份证
                reg.setNAME(pd2.getString("NAME"));
                reg.setPHONE(pd2.getString("PHONE"));
                reg.setREGISTERED(pd2.getString("REGISTERED"));
                // if (reg.getREGISTERED().equals("1")) {
                if (reg.getNAME().equals(pd.getString("NAME"))) {
                    // 根据身份证号ID从sys_user表中找到当前用户主键
                    PageData pd3 = new PageData();
                    pd3.put("NUMBER", pd.getString("ID"));
                    PageData pd4 = new PageData();
                    pd4 = usersService.findByNumber(pd3);
                    if (null == pd4) {
                        result = "07";// 账号已被注销或未曾注册账户
                    } else {
                        // 判断预留手机号
                        if (pd4.getString("PHONE").equals(pd.getString("PHONE"))) {
                            // 信息验证通过，允许找回
                            // 生成时间戳为本次找回密码身份授权信息并写入session
                            timestamp = System.currentTimeMillis();
                            Session session = Jurisdiction.getSession();
                            session.setAttribute(Const.authorization, timestamp);
                            userID = pd4.get("USER_ID");
                        } else {
                            result = "04";// 手机号码和预留授权手机号不符
                        }
                    }
                } else {
                    result = "03";// 真实姓名和授权注册的姓名不符
                }
                    /*
                } else {
                    result = "05"; // 尚未注册
                }*/
            }
        } else {
            result = "06"; // 违法的接口请求
        }
        map.put("result", result);
        map.put("authorization", timestamp);
        map.put("userID", userID);
        JSONObject result2 = JSONObject.fromObject(map);
        return callback + "(" + result2.toString() + ")";
    }

    /**
     * 重置密码接口
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/retrieve")
    @ResponseBody
    public Object retrieve() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String NEWPASSWORD = pd.getString("PASSWORD");
        String AUTHORIZATION = pd.getString("AUTHORIZATION");
        String result = "success";
        // 反查用户信息
        PageData pd2 = new PageData();
        pd2 = usersService.findById(pd);
        // 如果传入的验证信息是MD5加密后的值，则是从PC端重置密码进入的此接口，则将密码重置为：1
        if (Tools.checkKey("USERNAME", AUTHORIZATION)) {
            String USERNAME = pd2.getString("USERNAME");
            String new_pwd = new SimpleHash("SHA-1", USERNAME, "1").toString();
            pd2.put("PASSWORD", new_pwd);
            usersService.editUser(pd2);
        } else { // 如果是其它值，则验证该值是否在session中
            Session session = Jurisdiction.getSession();
            if (null != session) {
                String token = session.getAttribute(Const.authorization).toString();
                // 移除session中的授权信息
                session.removeAttribute(Const.authorization);
                if (token.equals(AUTHORIZATION)) {
                    String USERNAME = pd2.getString("USERNAME");
                    String new_pwd = new SimpleHash("SHA-1", USERNAME, NEWPASSWORD).toString();
                    String old_pwd = pd2.getString("PASSWORD");
                    if (new_pwd.equals(old_pwd)) {
                        result = "notsame";
                    } else {
                        pd2.put("PASSWORD", new_pwd);
                        usersService.editUser(pd2);
                    }
                } else {
                    result = "invalid";
                }
            } else {
                result = "invalid";
            }
        }
        map.put("result", result); // 返回结果
        return map;
    }

    /**
     * 根据身份证获取基本信息
     */
    @RequestMapping(value = "/getuserinfo")
    @ResponseBody
    public Object getUserInfo() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        PageData pd = new PageData();
        pd = this.getPageData();
        String result = "success";
        String card = pd.getString("CARD");
        pd = registeredService.findById(card);
        if (pd != null) {
            map.put("data", pd);
        }
        map.put("data", pd);
        map.put("result", result); // 返回结果
        return map;
    }
}
