package org.fh.util.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.fh.controller.base.BaseController;
import org.fh.controller.ins.HttpRequest;
import org.fh.entity.PageData;
import org.fh.service.system.FHlogService;
import org.fh.service.system.UsersService;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx")
public class WxController extends BaseController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private FHlogService FHLOG;

    //小程序唯一标识   (在微信小程序管理后台获取)
    private static String wxspAppid = "wx9daf503ceeb8a940";
    //小程序的 app secret (在微信小程序管理后台获取)
    private static String wxspSecret = "243e30cb51216ac3411fb4dd92b9833d";
    /**
     * 获取微信用户手机号码
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserPhone")
    public String decodeUserInfo(String code,String encryptedData,String iv) {
        System.out.println(code);
        Map map = new HashMap();
        //登录凭证不能为空
        if (code == null || code.length() == 0) {
            map.put("status", 0);
            map.put("msg", "code 不能为空");
            return "";
        }
        //微信端登录code值
        String wxCode = code;
        //请求参数 获取openid
        //发送请求
        Map<String,String> requestUrlParam = new HashMap<String,String>();
        requestUrlParam.put("appid", wxspAppid);	//开发者设置中的appId
        requestUrlParam.put("secret", wxspSecret);	//开发者设置中的appSecret
        requestUrlParam.put("js_code", wxCode);	//小程序调用wx.login返回的code
        requestUrlParam.put("grant_type", "authorization_code");	//默认参数
        String sr = HttpRequest.sendPost("https://api.weixin.qq.com/sns/jscode2session",requestUrlParam);
        System.out.println("sr========" + sr);
        //解析相应内容（转换成json对象）
        JSONObject json = JSON.parseObject(sr);
        String open_id = (String) json.get("open_id");
        String session_key = (String) json.get("session_key");
        String jsonObject = decryptWeChatData(encryptedData,iv,session_key);
        return jsonObject;
    }

    /**
     * 获取微信openid
     * @param code
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/getopenid")
    public Object getopenid(String code) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        //登录凭证不能为空
        if (code == null || code.length() == 0) {
            map.put("status", 0);
            map.put("msg", "code 不能为空");
            map.put("result", false);
            return map;
        }
        //微信端登录code值
        String wxCode = code;
        //请求参数 获取openid
        //发送请求
        Map<String,String> requestUrlParam = new HashMap<String,String>();
        requestUrlParam.put("appid", wxspAppid);	//开发者设置中的appId
        requestUrlParam.put("secret", wxspSecret);	//开发者设置中的appSecret
        requestUrlParam.put("js_code", wxCode);	//小程序调用wx.login返回的code
        requestUrlParam.put("grant_type", "authorization_code");	//默认参数
        String sr = HttpRequest.sendPost("https://api.weixin.qq.com/sns/jscode2session",requestUrlParam);
        JSONObject json = JSON.parseObject(sr);
        String openid = (String)json.get("openid");
        return openid;
    }

    /**
     * 微信绑定
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/nopasswordlogin")
    public Object nopasswordlogin() throws Exception {
        PageData pd = new PageData();
        pd = this.getPageData();
        PageData upd = usersService.findByOpenId(pd);
        Map<String, Object> map = new HashMap<String, Object>();
        if(upd != null){
            map.put("PASSWORD", upd.get("PASSWORD"));
            map.put("USERNAME", upd.get("USERNAME"));
            map.put("OPEN_ID",upd.get("OPEN_ID"));
            map.put("result","success");
        }else{
            map.put("result","fail");
        }
        return map;
    }

    /**
     * 微信绑定
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/userwchat")
    public Object userwchat() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        String errInfo = "success";
        PageData pd = new PageData();
        pd = this.getPageData();
        String USERNAME = pd.getString("USERNAME"); // 登录过来的用户名
        String PASSWORD = pd.getString("PASSWORD"); // 登录过来的密码
        UsernamePasswordToken token = new UsernamePasswordToken(USERNAME,
                new SimpleHash("SHA-1", USERNAME, PASSWORD).toString());
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
        if (subject.isAuthenticated()) {

            PageData upd = new PageData();
            upd = usersService.findByOpenId(pd);
            //判断用户是否已经绑定过微信
            if(upd != null){
                map.put("status", 0);
                map.put("msg", "该用户已绑定过微信");
                map.put("result","openiderror");
                return map;
            }
            upd = usersService.findByUsername(pd);
            if(upd.get("OPEN_ID") != null && !upd.get("OPEN_ID").equals("")){
                map.put("status", 0);
                map.put("msg", "该用户已绑定过微信");
                map.put("result","wchaterror");
                return map;
            }
            upd.put("OPEN_ID",pd.get("OPEN_ID"));
            usersService.editUser(upd); // 执行修改
            FHLOG.save((String) upd.get("USERNAME"), "进行微信绑定" + pd.getString("USERNAME"));
        }else {
            token.clear();
            errInfo = "usererror";
        }
        map.put("result", errInfo);
        // 记录日志
        return map;
    }


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 解密用户敏感数据获取用户信息
     *
     * @author zhy
     * @param sessionKey 数据进行加密签名的密钥
     * @param encryptedData 包括敏感数据在内的完整用户信息的加密数据
     * @param iv 加密算法的初始向量
     * @return
     */
    public static String decryptWeChatData(String encryptedData, String iv, String sessionKey) {
        final Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] sessionKeyByte = decoder.decode(sessionKey);
        byte[] encryptedDataByte = decoder.decode(encryptedData);
        byte[] ivByte = decoder.decode(iv);

        byte[] bytes;
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (sessionKeyByte.length % base != 0) {
                int groups = sessionKeyByte.length / base + (sessionKeyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(sessionKeyByte, 0, temp, 0, sessionKeyByte.length);
                sessionKeyByte = temp;
            }

            Key key = new SecretKeySpec(sessionKeyByte, "AES");
            AlgorithmParameters algorithmParameters = null;
            Cipher cipher = null;
            algorithmParameters = AlgorithmParameters.getInstance("AES");
            algorithmParameters.init(new IvParameterSpec(ivByte));
            cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, algorithmParameters);
            bytes = cipher.doFinal(encryptedDataByte);
        } catch (InvalidParameterSpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            bytes = new byte[0];
        }

        String decryptString = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("微信解密后数据为：{}"+ decryptString);
        return decryptString;
    }

}
