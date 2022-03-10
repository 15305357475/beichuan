package org.fh.controller.act;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.fh.controller.base.BaseController;
import org.fh.util.Const;
import org.fh.util.DelFileUtil;
import org.fh.util.FileUpload;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
/*import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;*/
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;


/**
 * 说明：流程管家总类
 * 作者：fsci
 * 授权：bsic
 */
public class AcBaseController extends BaseController{
	
	
	/*
	 * @Autowired private ProcessEngine processEngine; //流程引擎对象
	 */	
	@Autowired
	private RepositoryService repositoryService; //管理流程定义  与流程定义和部署对象相关的Service
	
	/*
	 * @Autowired private RuntimeService runtimeService;
	 * //与正在执行的流程实例和执行对象相关的Service(执行管理，包括启动、推进、删除流程实例等操作)
	 * 
	 * @Autowired private HistoryService historyService; //历史管理(执行完的数据的管理)
	 */	
	@Autowired
	private ModelService modelService;			//模型服务
	
	@Autowired
	protected ModelRepository modelRepository;	//模型资源服务
	
	/**从流程定义映射模型
	 * @param processDefinitionId 流程定义ID
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XMLStreamException
	 */
	protected void saveModelFromPro(String processDefinitionId) throws Exception {
		
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
		modelData.setName(processDefinition.getName()+"(反射)");			//名称
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
		modelRepository.save(modelData);				//保存模型
	
	}
	
	/**通过模型ID获取流程基本信息
	 * @param modelId 流程ID
	 * @throws JsonProcessingException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @return
	 */
	protected Map<String,String> getProcessProperties(String modelId) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode editorJsonNode = (ObjectNode)objectMapper.readTree(new String(repositoryService.getModelEditorSource(modelId), "utf-8")).get("properties");
		Map<String,String> map = new HashMap<String,String>();
		map.put("process_id",editorJsonNode.get("process_id").toString());					//流程唯一标识(KEY)
		map.put("process_author",editorJsonNode.get("process_author").toString());			//流程作者
		map.put("name",editorJsonNode.get("name").toString());								//流程名称
		return map;
	}
	
	/**部署流程定义(根据ui.modeler的 modelId部署)
	 * @param modelId 模型ID
	 * @return 部署ID
	 */
	protected String deploymentProcessDefinitionFromUIModelId(String modelId) throws Exception{
		Model model = modelService.getModel(modelId);
		BpmnModel bpmnModel = modelService.getBpmnModel(model);
		Deployment deployment = repositoryService.createDeployment()
		.name(model.getName())
		.addBpmnModel(model.getKey() + ".bpmn", bpmnModel).deploy();
        return deployment.getId();	//部署ID
	}
	
	/**部署流程定义(从Classpath)
	 * @param name		//部署名称
	 * @param xmlpath	//xml文件路径
	 * @param pngpath	//png文件路径
	 * @return 部署ID
	 */
	protected String deploymentProcessDefinitionFromClasspath(String name, String xmlpath, String pngpath){
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();		//创建部署对象
		deploymentBuilder.name(name);						//部署名称
		deploymentBuilder.addClasspathResource(xmlpath);	//从文件中读取xml资源
		deploymentBuilder.addClasspathResource(pngpath);	//从文件中读取png资源
		Deployment deployment = deploymentBuilder.deploy();	//完成部署
		return deployment.getId();							//部署ID
	}
	
	/**部署流程定义(从zip压缩包)
	 * @param name		//部署名称
	 * @param zippath	//zip文件路径
	 * @return 部署ID
	 * @throws FileNotFoundException 
	 */
	protected String deploymentProcessDefinitionFromZip(String name, String zippath) throws Exception{
		File outfile = new File(zippath);
		FileInputStream inputStream = new FileInputStream(outfile);
		ZipInputStream ipInputStream = new ZipInputStream(inputStream);
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();		//创建部署对象
		deploymentBuilder.name(name);						//部署名称
		deploymentBuilder.addZipInputStream(ipInputStream);
		Deployment deployment = deploymentBuilder.deploy();	//完成部署
		ipInputStream.close();
		inputStream.close();
		return deployment.getId();							//部署ID
	}
	
	/**根据流程定义的部署ID生成XML和PNG
	 * @param DEPLOYMENT_ID_ 部署ID
	 * @throws IOException 
	 */
	protected void createXmlAndPng(String DEPLOYMENT_ID_) throws IOException{
		DelFileUtil.delFolder(PathUtil.getProjectpath()+"uploadFiles/activitiFile"); 			//生成先清空之前生成的文件
		List<String> names = repositoryService.getDeploymentResourceNames(DEPLOYMENT_ID_);
        for (String name : names) {
        	if(name.indexOf("zip")!=-1)continue;
            InputStream in = repositoryService.getResourceAsStream(DEPLOYMENT_ID_, name);  
            FileUpload.copyFile(in,PathUtil.getProjectpath()+Const.FILEACTIVITI,name); 			//把文件上传到文件目录里面
            in.close();  
        }  
	}
	
	/**删除部署的流程
	 * @param DEPLOYMENT_ID_ 部署ID
	 * @throws IOException 
	 */
	protected void deleteDeployment(String DEPLOYMENT_ID_) throws Exception{
		//repositoryService.deleteDeployment(DEPLOYMENT_ID_); 		//不带级联的删除，此删除只能删除没有启动的流程，否则抛出异常 .act_re_deployment，act_re_procdef 和  act_ge_bytearray 三张表中相关数据都删除
		repositoryService.deleteDeployment(DEPLOYMENT_ID_, true);	//级联删除，不管流程是否启动，都可以删除
	}
	
	/**激活流程定义
	 * @param DEPLOYMENT_ID_ 流程定义ID
	 * @throws IOException
	 */
	protected void activateProcessDefinitionById(String DEPLOYMENT_ID_) throws IOException{
		repositoryService.activateProcessDefinitionById(DEPLOYMENT_ID_, true, null);
	}
	
	/**挂起流程定义
	 * @param DEPLOYMENT_ID_ 流程定义ID
	 * @throws IOException
	 */
	protected void suspendProcessDefinitionById(String DEPLOYMENT_ID_) throws IOException{
		repositoryService.suspendProcessDefinitionById(DEPLOYMENT_ID_, true, null);
	}

}