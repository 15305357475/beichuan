package org.fh.controller.act.fhmodel;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.act.AcBaseController;
import org.fh.entity.PageData;
import org.fh.util.Jurisdiction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 说明：工作流模型管理
 * 作者：fsci
 * 授权：bsic
 */
@Controller
@RequestMapping("/fhmodel")
public class FHModelController extends AcBaseController{
	
	 /**获取当前用户
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getAuthor")
	@ResponseBody
	public Object getAuthor()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		map.put("process_author", Jurisdiction.getName());	//流程作者
		map.put("result", errInfo);							//返回结果
		return map;
	}
	
	/**部署流程定义
	 * @return 
	 */
	@RequestMapping(value="/deployment")
	@RequiresPermissions("fhmodel:edit")
	@ResponseBody
	public Object deployment(){
		Map<String,Object> map = new HashMap<String,Object>();
		String result = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			deploymentProcessDefinitionFromUIModelId(pd.getString("modelId"));//部署流程定义
		}catch (Exception e){
			result = "error";
		}finally{
			map.put("result", result);
		}
		return map;
	}

	 /**从流程定义映射模型
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/saveModelFromPro")
	@RequiresPermissions("fhmodel:add")
	@ResponseBody
	public Object saveModelFromPro(){
		Map<String,Object> map = new HashMap<String,Object>();
		String result = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String processDefinitionId = pd.getString("processDefinitionId"); 		//流程定义ID
		try {
			saveModelFromPro(processDefinitionId);
		} catch (Exception e) {
			result = "errer";
		}
		map.put("result", result);
		return map;
	}
	
}
