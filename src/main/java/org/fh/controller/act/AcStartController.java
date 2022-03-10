package org.fh.controller.act;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.fh.controller.base.BaseController;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;

/**
 * 说明：启动流程用
 * 作者：fsci
 * 授权：bsic
 */
public class AcStartController extends BaseController {
	
	@Autowired
	private RuntimeService runtimeService; 		//与正在执行的流程实例和执行对象相关的Service(执行管理，包括启动、推进、删除流程实例等操作)
	
	/**通过KEY启动流程实例(不带变量)
	 * @param processInstanceKey //流程定义的KEY
	 * @return 返回流程实例ID
	 */
	protected String startProcessInstanceByKey(String processInstanceKey){
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processInstanceKey);			//用流程定义的KEY启动，会自动选择KEY相同的流程定义中最新版本的那个(KEY为模型中的流程唯一标识)
		return processInstance.getId();	//返回流程实例ID
	}
	
	/**通过KEY启动流程实例(带变量)
	 * @param processInstanceKey 流程定义的KEY
	 * @param map 存流程变量
	 * @param USERNAME 流程发起人
	 * @return 返回流程实例ID
	 */
	protected String startProcessInstanceByKeyHasVariables(String processInstanceKey,Map<String,Object> map, String USERNAME){
		Authentication.setAuthenticatedUserId(USERNAME);//设置流程发起人
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processInstanceKey, map);	//map存储变量 用流程定义的KEY启动，会自动选择KEY相同的流程定义中最新版本的那个(KEY为模型中的流程唯一标识)
		Authentication.setAuthenticatedUserId(null);//这个方法最终使用一个ThreadLocal类型的变量进行存储，也就是与当前的线程绑定，所以流程实例启动完毕之后，需要设置为null，防止多线程的时候出问题。
		return processInstance.getId();	//返回流程实例ID
	}
	
	/**通过ID启动流程实例
	 * @param processInstanceId //流程定义的ID
	 * @return 返回流程实例ID
	 */
	protected String startProcessInstanceById(String processInstanceId){
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processInstanceId);			//用流程定义的ID启动
		return processInstance.getId();	//返回流程实例ID
	}
   
}
