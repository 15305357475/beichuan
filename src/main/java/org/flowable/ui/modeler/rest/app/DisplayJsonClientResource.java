package org.flowable.ui.modeler.rest.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.fh.util.Jurisdiction;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.service.BpmnDisplayJsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 说明：重构查看流程图Display
 * 作者：f-sci
 * 授权：bsic
 */
@RestController
@RequestMapping("/app")
public class DisplayJsonClientResource {
	
	@Autowired
	protected BpmnDisplayJsonConverter bpmnDisplayJsonConverter;
	@Autowired
	private ProcessEngine processEngine; 			// 流程引擎对象
	@Autowired
	private HistoryService historyService; 			// 历史管理(执行完的数据的管理)
	@Autowired
	private RuntimeService runtimeService;			// 与正在执行的流程实例和执行对象相关的Service(执行管理，包括启动、推进、删除流程实例等操作)
	@Autowired
	private RepositoryService repositoryService; 	// 管理流程定义 与流程定义和部署对象相关的Service
	@Autowired
	protected ModelRepository modelRepository;		//模型资源服务

	/**根据部署ID获取流程图资源json数据
	 * @param processDefinitionId
	 * @return
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	@GetMapping(value = "/rest/fhadmin-deployment/{processDefinitionId}/model-json", produces = "application/json")
	public JsonNode getModelJSON1(@PathVariable String processDefinitionId) throws XMLStreamException, IOException {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
		InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
		processDefinition.getResourceName());
		XMLInputFactory xif = XMLInputFactory.newInstance();
		InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
		XMLStreamReader xtr = xif.createXMLStreamReader(in);
		BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
		BpmnJsonConverter converter = new BpmnJsonConverter();
		ObjectNode modelNode = converter.convertToJson(bpmnModel);
		Model modelData = new Model();
		modelData.setKey(processDefinition.getKey());					//唯一标识
		modelData.setName(processDefinition.getName());					//名称
		int version = 1;
    	List<Model> mlist = modelRepository.findByKeyAndType(modelData.getKey(), 0);
    	for(Model m : mlist) {
    		if(m.getVersion() >= version) {
    			version = m.getVersion() + 1;
    		}
    	}
    	modelData.setVersion(version);									//版本
		modelData.setModelType(0);
		modelData.setCreatedBy(Jurisdiction.getUsername());				//创建人，当前用户名
		modelData.setLastUpdatedBy(Jurisdiction.getUsername());			//最后更新人，当前用户名
		modelData.setLastUpdated(new Date());
		List<String> names = repositoryService.getDeploymentResourceNames(processDefinition.getDeploymentId());
        for (String name : names) {
        	if(name.indexOf("png")!=-1) {								//读取流程资源中的图片资源
        		InputStream inr = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), name);  
        		ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
                byte[] buff = new byte[100]; 
                int rc = 0; 
                while ((rc = inr.read(buff, 0, 100)) > 0) { 
                    swapStream.write(buff, 0, rc); 		//InputStream 转 byte
                } 
                byte[] inb = swapStream.toByteArray();
        		modelData.setThumbnail(inb);			//把图资源存到 model中
        		inr.close(); 
        		continue;
        	}
        }
		modelData.setModelEditorJson(modelNode.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode displayNode = objectMapper.createObjectNode();
		bpmnDisplayJsonConverter.processProcessElements(modelData, displayNode, new GraphicInfo());
		return displayNode;
	}
	
	/**根据流程实例ID获取流程图资源json数据
	 * @param processId
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/rest/fhadmin-definitions/{processId}/model-json", produces = "application/json")
	public JsonNode getModelJSON2(@PathVariable String processId) throws IOException {
		String processDefinitionId = "";
		if (this.isFinished(processId)) { 			// 如果流程已经结束，则得到结束节点
			HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
			processDefinitionId = pi.getProcessDefinitionId();
		} else { 									// 如果流程没有结束，则取当前活动节点
			/* 根据流程实例ID获得当前处于活动状态的ActivityId合集 */
			ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
			processDefinitionId = pi.getProcessDefinitionId();
		}
		StringBuffer highLightedFlow = new StringBuffer();
		/* 获得活动的节点对象 */
		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();
		List<String> highLightedActivitis = new ArrayList<String>(); 				// 节点对象ID
		for (HistoricActivityInstance tempActivity : highLightedActivitList) {
			String activityId = tempActivity.getActivityId();
			highLightedActivitis.add(activityId);
			highLightedFlow.append(activityId).append(",");
		}
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId); // 获取流程图
		BpmnJsonConverter converter = new BpmnJsonConverter();
		ObjectNode modelNode = converter.convertToJson(bpmnModel);
		Model modelData = new Model();
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
		modelData.setKey(processDefinition.getKey()); 			// 唯一标识
		modelData.setName(processDefinition.getName()); 		// 名称
		modelData.setVersion(processDefinition.getVersion()); 	// 版本
		modelData.setModelType(0);
		modelData.setCreatedBy(Jurisdiction.getUsername()); 	// 创建人，当前用户名
		modelData.setLastUpdatedBy(Jurisdiction.getUsername()); // 最后更新人，当前用户名
		modelData.setLastUpdated(new Date());
		ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
		/* 获得活动的连线对象 */
		List<ActivityInstance> highLightedFlowInstances = runtimeService.createActivityInstanceQuery().activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).processInstanceId(processId).list();
		List<String> flows = new ArrayList<>(); 				// 连线ID
		for (ActivityInstance ai : highLightedFlowInstances) {
			flows.add(ai.getActivityId());
		}
		ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
		InputStream inr = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, flows,
				engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(),
				engconf.getClassLoader(), 1.0, true);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inr.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc); 						// InputStream 转 byte
		}
		byte[] inb = swapStream.toByteArray();
		modelData.setThumbnail(inb); 							// 把图资源存到 model中
		inr.close();
		modelData.setModelEditorJson(modelNode.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode displayNode = objectMapper.createObjectNode();
		displayNode.put("highLightedFlow", highLightedFlow.toString());
		bpmnDisplayJsonConverter.processProcessElements(modelData, displayNode, new GraphicInfo());
		return displayNode;
	}

	/**
	 * 流程是否完成功能
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public boolean isFinished(String processInstanceId) {
		return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId)
				.count() > 0;
	}
	
	/**仅做一个捂手，不进行业务处理
	 * @return
	 */
	@GetMapping(value = "/rest/fhadmin-callback", produces = "application/json")
	public JsonNode callback() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode displayNode = objectMapper.createObjectNode();
		return displayNode;
	}

}
