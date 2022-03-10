package org.fh.controller.fhoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fh.controller.base.BaseController;
import org.fh.entity.PageData;
import org.fh.entity.fhoa.Registered;
import org.fh.entity.fhoa.StaffInfo;
import org.fh.service.fhoa.StaffInfoCheckService;
import org.fh.util.Const;
import org.fh.util.HttpRequest;
import org.fh.util.ImageAnd64Binary;
import org.fh.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 说明：人员信息核验处理类 作者：f-sci 授权：bsic
 */
@Controller
@RequestMapping("/staffInfoCheck")
public class StaffInfoCheckController extends BaseController {

	@Autowired
	private StaffInfoCheckService staffInfoCheckService;

	/**
	 * 根据身份证获取信息
	 */
	@RequestMapping(value = "/getInfo")
	@ResponseBody
	public Object getInfo() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String result = "success";
		String card = pd.getString("CARD");
		pd = staffInfoCheckService.findByCard(card);
		if (pd != null) {
			map.put("data", pd);
		}
		map.put("data", pd);
		map.put("result", result); // 返回结果
		return map;
	}
	
	/**
	 * 全量接口更新人员信息
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getStaffInfoByFull")
	@ResponseBody
	public Object updateListFull() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		JSONArray employee = new JSONArray();
		List<StaffInfo> FullStaffList = new ArrayList<StaffInfo>();
		// 获取一卡通token
		String token = GetOneCardToken();
		// 获取一卡通内的总在职人数
		if (!token.equals("error")) {
			// 循环获取全部员工数据，每次1000条，总共约20000人
			for (int i = 1; i < 25; i++) {
				JSONArray data = GetEmployeeByFull(token, i);
				if (data != null && data.size() > 0) {
					employee.add(data);// 数据格式JSONArray[[JSONArray],[JSONArray],[JSONArray],[JSONArray],...]
				} else {
					break;
				}
			}
			// 解包，构造对象list
			for (int i = 0; i < employee.size(); i++) {// 外层循环
				JSONArray now_array = employee.getJSONArray(i);
				for (int j = 0; j < now_array.size(); j++) {// 内层循环
					String oneEmployee = now_array.getString(j);
					JSONObject oneJSON = JSONObject.parseObject(oneEmployee);
					StaffInfo si = new StaffInfo();
					si.setID(this.get32UUID());
					si.setCARD(oneJSON.getString("IDCard"));
					si.setNAME(oneJSON.getString("UserName"));
					si.setPHONE(oneJSON.getString("PhoneNumber"));
					si.setUSERNO(oneJSON.getString("UserNo"));
					si.setLABORRELATION(oneJSON.getString("LaborRelation"));
					si.setSERVICESDEPARTMENT(oneJSON.getString("ServicesDepartment"));
					si.setEMPLOYEETYPE(oneJSON.getString("EmployeeType"));
					si.setTYPEOFWORK(oneJSON.getString("TypeOfWork"));
					si.setTEAM(oneJSON.getString("Team"));
					si.setSTATION(oneJSON.getString("Station"));
					si.setCERTIFICATIONS(oneJSON.getString("Photo"));
					si.setPHOTO(oneJSON.getString("Certifications"));
					si.setCERTIFICATIONS_IMGS(oneJSON.getString("CertificationsImg"));
					si.setSTATUS("1");
					FullStaffList.add(si);
				}
			}
		} else {
			errInfo = "error:一卡通接口数据异常，无法获取令牌";
		}
		// 遍历结果集更新数据
		updateListInfoByCard(FullStaffList);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 增量接口更新人员信息
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getStaffInfoByIncrement")
	@ResponseBody
	public Object updateListIncrement() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String errInfo = "success";
		JSONArray employee = new JSONArray();
		List<StaffInfo> FullStaffList = new ArrayList<StaffInfo>();
		// 获取一卡通token
		String token = GetOneCardToken();
		// 获取一卡通内的总在职人数
		if (!token.equals("error")) {
			JSONArray data = GetEmployeeByIncrement(token);
			if (data != null && data.size() > 0) {
				employee.add(data);// 数据格式JSONArray[[JSONArray],[JSONArray],[JSONArray],[JSONArray],...]
			}
			// 解包，构造对象list
			for (int i = 0; i < employee.size(); i++) {// 外层循环
				JSONArray now_array = employee.getJSONArray(i);
				for (int j = 0; j < now_array.size(); j++) {// 内层循环
					String oneEmployee = now_array.getString(j);
					JSONObject oneJSON = JSONObject.parseObject(oneEmployee);
					StaffInfo si = new StaffInfo();
					si.setID(this.get32UUID());
					si.setCARD(oneJSON.getString("IDCard"));
					si.setNAME(oneJSON.getString("UserName"));
					si.setPHONE(oneJSON.getString("PhoneNumber"));
					si.setUSERNO(oneJSON.getString("UserNo"));
					si.setLABORRELATION(oneJSON.getString("LaborRelation"));
					si.setSERVICESDEPARTMENT(oneJSON.getString("ServicesDepartment"));
					si.setEMPLOYEETYPE(oneJSON.getString("EmployeeType"));
					si.setTYPEOFWORK(oneJSON.getString("TypeOfWork"));
					si.setTEAM(oneJSON.getString("Team"));
					si.setSTATION(oneJSON.getString("Station"));
					si.setCERTIFICATIONS(oneJSON.getString("Photo"));
					si.setPHOTO(oneJSON.getString("Certifications"));
					si.setCERTIFICATIONS_IMGS(oneJSON.getString("CertificationsImg"));
					si.setSTATUS("1");
					FullStaffList.add(si);
				}
			}
		} else {
			errInfo = "error:一卡通接口数据异常，无法获取令牌";
		}
		// 遍历结果集更新数据
		updateListInfoByCard(FullStaffList);
		map.put("result", errInfo); // 返回结果
		return map;
	}

	/**
	 * 获取一卡通Token
	 * 
	 * @param
	 * @throws Exception
	 */
	public String GetOneCardToken() {
		String OneCardURL = "http://192.168.200.123/api/getUserToken/11002800";
		String Response = HttpRequest.get(OneCardURL);
		JSONObject json = JSONObject.parseObject(Response);
		if (json.getString("code").equals("1")) {
			return json.getString("data");
		}
		return "error";
	}

	/**
	 * 获取一卡通在职人员数据，每次1000条 请求全量接口
	 * 
	 * @param
	 * @throws Exception
	 */
	public JSONArray GetEmployeeByFull(String token, int startPage) {
		String OneCardURL = "http://192.168.200.123:8000/api/GetStaffListFull/" + token;
		// 构造请求体,一次1000人
		JSONObject requestBody = new JSONObject();
		requestBody.put("pageSize", 1000);
		requestBody.put("currentPage", startPage);
		// POST请求
		try {
			String Response = HttpRequest.post(requestBody, OneCardURL);
			JSONObject json = JSONObject.parseObject(Response);
			if (json.getString("code").equals("1")) {
				String jsonDataArray = json.getString("data");
				JSONArray jsonArray = new JSONArray();
				jsonArray = JSONArray.parseArray(jsonDataArray);
				return jsonArray;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取一卡通在职人员数据 请求增量接口
	 * 
	 * @param
	 * @throws Exception
	 */
	public JSONArray GetEmployeeByIncrement(String token) {
		String OneCardURL = "http://192.168.200.123:8000/api/GetStaffListIncrement/" + token;
		// 构造请求体
		JSONObject requestBody = new JSONObject();
		requestBody.put("tm", System.currentTimeMillis());
		// POST请求
		try {
			String Response = HttpRequest.post(requestBody, OneCardURL);
			JSONObject json = JSONObject.parseObject(Response);
			if (json.getString("code").equals("1")) {
				String jsonDataArray = json.getString("data");
				JSONArray jsonArray = new JSONArray();
				jsonArray = JSONArray.parseArray(jsonDataArray);
				return jsonArray;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据身份证号更新数据库数据
	 */
	public void updateListInfoByCard(List<StaffInfo> list1) {
		for (StaffInfo temp : list1) {
			try {
				// 保存图片
				String base64 = temp.getPHOTO();
				String cardNo = temp.getCARD();
				String shortFilePath = Const.INFOCHECKPHOTO + cardNo + ".jpg";
				String fullFilePath = PathUtil.getProjectpath() + Const.INFOCHECKPHOTO + cardNo + ".jpg";
				if (base64 != null && !base64.equals("")) {
					boolean flag = ImageAnd64Binary.generateImage(base64, fullFilePath);
					// 图片路径记录到temp
					if (flag) {
						temp.setPHOTO(shortFilePath);
					} else {
						temp.setPHOTO("N/A");
					}
				}
				// 更新或插入数据
				staffInfoCheckService.InsertOrUpdate(temp);
			} catch (Exception e) {
				// TODO: handle exception
				continue;
			}
		}
	}

	/**
	 * 差集(基于java8新特性)优化解法2 适用于大数据量 求List1中有的但是List2中没有的元素 身份证号一致即认为元素一致
	 */
	public static List<Registered> DifferenceSetByID(List<Registered> list1, List<Registered> list2) {
		Map<String, String> tempMap = list2.parallelStream()
				.collect(Collectors.toMap(Registered::getCARD, Registered::getCARD));
		return list1.parallelStream().filter(Registered -> {
			return !tempMap.containsKey(Registered.getCARD());
		}).collect(Collectors.toList());
	}

}
