package org.flowable.ui.modeler.service;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.cmmn.converter.CmmnXmlConverter;
import org.flowable.cmmn.editor.json.converter.CmmnJsonConverter;
import org.flowable.cmmn.model.Case;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.AppDefinition;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.AppDefinitionListModelRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.repository.ModelSort;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 说明：重构流程编辑器编辑 Service
 * 作者：f-sci
 * 授权：bsic
 */
@Service
@Transactional
public class FFlowableModelQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FFlowableModelQueryService.class);

    protected static final String FILTER_SHARED_WITH_ME = "sharedWithMe";
    protected static final String FILTER_SHARED_WITH_OTHERS = "sharedWithOthers";
    protected static final String FILTER_FAVORITE = "favorite";

    protected static final int MIN_FILTER_LENGTH = 1;

    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected ModelService modelService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
    
    protected CmmnXmlConverter cmmnXmlConverter = new CmmnXmlConverter();
    protected CmmnJsonConverter cmmnJsonConverter = new CmmnJsonConverter();

    public ResultListDataRepresentation getModels(String filter, String sort, Integer modelType, HttpServletRequest request) {
        // need to parse the filterText parameter ourselves, due to encoding issues with the default parsing.
        String filterText = null;
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
        if (params != null) {
            for (NameValuePair nameValuePair : params) {
                if ("filterText".equalsIgnoreCase(nameValuePair.getName())) {
                    filterText = nameValuePair.getValue();
                }
            }
        }
        List<ModelRepresentation> resultList = new ArrayList<>();
        List<Model> models = null;
        String validFilter = makeValidFilterText(filterText);
        if (validFilter != null) {
            models = modelRepository.findByModelTypeAndFilter(modelType, validFilter, sort);
        } else {
            models = modelRepository.findByModelType(modelType, sort);
        }
        if (CollectionUtils.isNotEmpty(models)) {
            List<String> addedModelIds = new ArrayList<>();
            for (Model model : models) {
                if (!addedModelIds.contains(model.getId())) {
                    addedModelIds.add(model.getId());
                    ModelRepresentation representation = createModelRepresentation(model);
                    resultList.add(representation);
                }
            }
        }
        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        return result;
    }

    public ResultListDataRepresentation getModelsToIncludeInAppDefinition() {
        List<ModelRepresentation> resultList = new ArrayList<>();
        List<String> addedModelIds = new ArrayList<>();
        List<Model> models = modelRepository.findByModelType(AbstractModel.MODEL_TYPE_BPMN, ModelSort.MODIFIED_DESC);
        if (CollectionUtils.isNotEmpty(models)) {
            for (Model model : models) {
                if (!addedModelIds.contains(model.getId())) {
                    addedModelIds.add(model.getId());
                    ModelRepresentation representation = createModelRepresentation(model);
                    resultList.add(representation);
                }
            }
        }
        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        return result;
    }
    
    public ResultListDataRepresentation getCmmnModelsToIncludeInAppDefinition() {
        List<ModelRepresentation> resultList = new ArrayList<>();
        List<String> addedModelIds = new ArrayList<>();
        List<Model> models = modelRepository.findByModelType(AbstractModel.MODEL_TYPE_CMMN, ModelSort.MODIFIED_DESC);
        if (CollectionUtils.isNotEmpty(models)) {
            for (Model model : models) {
                if (!addedModelIds.contains(model.getId())) {
                    addedModelIds.add(model.getId());
                    ModelRepresentation representation = createModelRepresentation(model);
                    resultList.add(representation);
                }
            }
        }
        ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
        return result;
    }

    /**执行导入流程xml文件
     * @param request
     * @param file
     * @return
     */
    public ModelRepresentation importProcessModel(HttpServletRequest request, MultipartFile file) {

        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"))) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), "UTF-8");
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    throw new BadRequestException("No process found in definition " + fileName);
                }

                if (bpmnModel.getLocationMap().size() == 0) {
                    BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                    bpmnLayout.execute();
                }

                ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);

                org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
                String name = process.getId();
                if (StringUtils.isNotEmpty(process.getName())) {
                    name = process.getName();
                }
                String description = process.getDocumentation();
                int version = 1;
            	List<Model> mlist = modelRepository.findByKeyAndType(process.getId(), 0);	//获取相同KEY的模型list
            	for(Model m : mlist) {
            		if(m.getVersion() >= version) {
            			version = m.getVersion() + 1;	//最大的版本号+1
            		}
            	}
                ModelRepresentation model = new ModelRepresentation();
                model.setKey(process.getId());
                model.setName(name);
                model.setDescription(description);
                model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
                Model newModel = modelService.createModel(model, modelNode.toString(), SecurityUtils.getCurrentUserObject());
            	newModel.setVersion(version);			//更新版本号
            	modelService.saveModel(newModel);
                return new ModelRepresentation(newModel);

            } catch (BadRequestException e) {
                throw e;

            } catch (Exception e) {
                LOGGER.error("Import failed for {}", fileName, e);
                throw new BadRequestException("Import failed for " + fileName + ", error message " + e.getMessage());
            }
        } else {
            //throw new BadRequestException("文件无效，仅支持.bpmn和.bpmn20.xml, 不支持：" + fileName);
        	return null;
        }
    }
    
    /**执行导入模型
     * @param request
     * @param file
     * @return
     */
    public ModelRepresentation importCaseModel(HttpServletRequest request, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".cmmn") || fileName.endsWith(".cmmn.xml"))) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), "UTF-8");
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                CmmnModel cmmnModel = cmmnXmlConverter.convertToCmmnModel(xtr);
                if (CollectionUtils.isEmpty(cmmnModel.getCases())) {
                    throw new BadRequestException("No cases found in definition " + fileName);
                }
                if (cmmnModel.getLocationMap().size() == 0) {
                    throw new BadRequestException("No CMMN DI found in definition " + fileName);
                }
                ObjectNode modelNode = cmmnJsonConverter.convertToJson(cmmnModel);
                Case caseModel = cmmnModel.getPrimaryCase();
                String name = caseModel.getId();
                if (StringUtils.isNotEmpty(caseModel.getName())) {
                    name = caseModel.getName();
                }
                String description = caseModel.getDocumentation();
                ModelRepresentation model = new ModelRepresentation();
                model.setKey(caseModel.getId());
                model.setName(name);
                model.setDescription(description);
                model.setModelType(AbstractModel.MODEL_TYPE_CMMN);
                Model newModel = modelService.createModel(model, modelNode.toString(), SecurityUtils.getCurrentUserObject());
                int version = 1;
            	List<Model> mlist = modelRepository.findByKeyAndType(caseModel.getId(), 0);
            	for(Model m : mlist) {
            		if(m.getVersion() >= version) {
            			version = m.getVersion() + 1;
            		}
            	}
            	newModel.setVersion(version);
            	modelService.saveModel(newModel);
                return new ModelRepresentation(newModel);

            } catch (BadRequestException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.error("Import failed for {}", fileName, e);
                throw new BadRequestException("Import failed for " + fileName + ", error message " + e.getMessage());
            }
        } else {
            //throw new BadRequestException("文件无效，仅支持.bpmn和.bpmn20.xml, 不支持：" + fileName);
        	return null;
        }
    }

    protected ModelRepresentation createModelRepresentation(AbstractModel model) {
        ModelRepresentation representation = null;
        if (model.getModelType() != null && model.getModelType() == 3) {
            representation = new AppDefinitionListModelRepresentation(model);
            AppDefinition appDefinition = null;
            try {
                appDefinition = objectMapper.readValue(model.getModelEditorJson(), AppDefinition.class);
            } catch (Exception e) {
                LOGGER.error("Error deserializing app {}", model.getId(), e);
                throw new InternalServerErrorException("Could not deserialize app definition");
            }
            ((AppDefinitionListModelRepresentation) representation).setAppDefinition(appDefinition);

        } else {
            representation = new ModelRepresentation(model);
        }
        return representation;
    }

    protected String makeValidFilterText(String filterText) {
        String validFilter = null;

        if (filterText != null) {
            String trimmed = StringUtils.trim(filterText);
            if (trimmed.length() >= MIN_FILTER_LENGTH) {
                validFilter = "%" + trimmed.toLowerCase() + "%";
            }
        }
        return validFilter;
    }

}
