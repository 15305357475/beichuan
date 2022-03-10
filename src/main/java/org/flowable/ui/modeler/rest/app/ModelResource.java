package org.flowable.ui.modeler.rest.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.idm.api.User;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.service.exception.ConflictingRequestException;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 说明：重构流程编辑器编辑
 * 作者：f-sci
 * 授权：bsic
 */
@RestController
@RequestMapping("/app")
public class ModelResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelResource.class);

    private static final String RESOLVE_ACTION_OVERWRITE = "overwrite";
    private static final String RESOLVE_ACTION_SAVE_AS = "saveAs";
    private static final String RESOLVE_ACTION_NEW_VERSION = "newVersion";

    @Autowired
    protected ModelService modelService;

    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

    protected BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();

    /**获取流程信息  GET /rest/models/{modelId} -> Get process model
     * @param modelId
     * @return
     */
    @GetMapping(value = "/rest/models/{modelId}", produces = "application/json")
    public ModelRepresentation getModel(@PathVariable String modelId) {
        return modelService.getModelRepresentation(modelId);
    }

    /**获取模型的流程图片byte[]数据 GET /rest/models/{modelId}/thumbnail -> Get process model thumbnail
     * @param modelId
     * @return
     */
    @GetMapping(value = "/rest/models/{modelId}/thumbnail", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getModelThumbnail(@PathVariable String modelId) {
        Model model = modelService.getModel(modelId);
        return model.getThumbnail();
    }

    /**更新模型基本信息 PUT方式进入，在显示详细信息页面中的修改进入 PUT /rest/models/{modelId} -> update process model properties
     * @param modelId
     * @param updatedModel
     * @return
     */
    @PutMapping(value = "/rest/models/{modelId}")
    public ModelRepresentation updateModel(@PathVariable String modelId, @RequestBody ModelRepresentation updatedModel) {
        // Get model, write-permission required if not a favorite-update
        Model model = modelService.getModel(modelId);
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(model, model.getModelType(), updatedModel.getKey());
        int version = 1;
        if (modelKeyInfo.isKeyAlreadyExists()) {
            //throw new BadRequestException("Model with provided key already exists " + updatedModel.getKey());
        	List<Model> mlist = modelRepository.findByKeyAndType(updatedModel.getKey(), 0);
        	for(Model m : mlist) {
        		if(m.getVersion() >= version) {
        			version = m.getVersion() + 1;
        		}
        	}
        }
        try {
        	model.setVersion(version);
            updatedModel.updateModel(model);
            if (model.getModelType() != null) {
                ObjectNode modelNode = (ObjectNode) objectMapper.readTree(model.getModelEditorJson());
                modelNode.put("name", model.getName());
                modelNode.put("key", model.getKey());

                if (Model.MODEL_TYPE_BPMN == model.getModelType()) {
                    ObjectNode propertiesNode = (ObjectNode) modelNode.get("properties");
                    propertiesNode.put("process_id", model.getKey());
                    propertiesNode.put("name", model.getName());
                    if (StringUtils.isNotEmpty(model.getDescription())) {
                        propertiesNode.put("documentation", model.getDescription());
                    }
                    modelNode.set("properties", propertiesNode);
                }
                model.setModelEditorJson(modelNode.toString());
            }
            modelRepository.save(model);
            ModelRepresentation result = new ModelRepresentation(model);
            return result;
        } catch (Exception e) {
            throw new BadRequestException("Model cannot be updated: " + modelId);
        }
    }

    /**删除模型 DELETE /rest/models/{modelId} -> delete process model or, as a non-owner, remove the share info link for that user specifically
     * @param modelId
     */
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "/rest/models/{modelId}")
    public void deleteModel(@PathVariable String modelId) {
        // Get model to check if it exists, read-permission required for delete
        Model model = modelService.getModel(modelId);
        try {
            modelService.deleteModel(model.getId());
        } catch (Exception e) {
            LOGGER.error("Error while deleting: ", e);
            throw new BadRequestException("Model cannot be deleted: " + modelId);
        }
    }

    /**根据模型ID获取json格式数据 GET /rest/models/{modelId}/editor/json -> get the JSON model
     * @param modelId
     * @return
     */
    @GetMapping(value = "/rest/models/{modelId}/editor/json", produces = "application/json")
    public ObjectNode getModelJSON(@PathVariable String modelId) {
        Model model = modelService.getModel(modelId);
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put("modelId", model.getId());
        modelNode.put("name", model.getName());
        modelNode.put("key", model.getKey());
        modelNode.put("description", model.getDescription());
        modelNode.putPOJO("lastUpdated", model.getLastUpdated());
        modelNode.put("lastUpdatedBy", model.getLastUpdatedBy());
        if (StringUtils.isNotEmpty(model.getModelEditorJson())) {
            try {
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(model.getModelEditorJson());
                editorJsonNode.put("modelType", "model");
                modelNode.set("model", editorJsonNode);
            } catch (Exception e) {
                LOGGER.error("Error reading editor json {}", modelId, e);
                throw new InternalServerErrorException("Error reading editor json " + modelId);
            }
        } else {
            ObjectNode editorJsonNode = objectMapper.createObjectNode();
            editorJsonNode.put("id", "canvas");
            editorJsonNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorJsonNode.put("modelType", "model");
            modelNode.set("model", editorJsonNode);
        }
        return modelNode;
    }

    /**保存模型 POST /rest/models/{modelId}/editor/json -> save the JSON model
     * @param modelId
     * @param values
     * @return
     */
    @PostMapping(value = "/rest/models/{modelId}/editor/json")
    public ModelRepresentation saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
        // Validation: see if there was another update in the meantime
        long lastUpdated = -1L;
        String lastUpdatedString = values.getFirst("lastUpdated");
        if (lastUpdatedString == null) {
            throw new BadRequestException("Missing lastUpdated date");
        }
        try {
            Date readValue = objectMapper.getDeserializationConfig().getDateFormat().parse(lastUpdatedString);
            lastUpdated = readValue.getTime();
        } catch (ParseException e) {
            throw new BadRequestException("Invalid lastUpdated date: '" + lastUpdatedString + "'");
        }
        Model model = modelService.getModel(modelId);
        User currentUser = SecurityUtils.getCurrentUserObject();
        boolean currentUserIsOwner = model.getLastUpdatedBy().equals(currentUser.getId());
        String resolveAction = values.getFirst("conflictResolveAction");
        // If timestamps differ, there is a conflict or a conflict has been resolved by the user
        if (model.getLastUpdated().getTime() != lastUpdated) {
            if (RESOLVE_ACTION_SAVE_AS.equals(resolveAction)) {
                String saveAs = values.getFirst("saveAs");
                String json = values.getFirst("json_xml");
                return createNewModel(saveAs, model.getDescription(), model.getModelType(), json);

            } else if (RESOLVE_ACTION_OVERWRITE.equals(resolveAction)) {
                return updateModel(model, values, false);
            } else if (RESOLVE_ACTION_NEW_VERSION.equals(resolveAction)) {
                return updateModel(model, values, true);
            } else {
                // Exception case: the user is the owner and selected to create a new version
                String isNewVersionString = values.getFirst("newversion");
                if (currentUserIsOwner && "true".equals(isNewVersionString)) {
                    return updateModel(model, values, true);
                } else {
                    // Tried everything, this is really a conflict, return 409
                    ConflictingRequestException exception = new ConflictingRequestException("Process model was updated in the meantime");
                    exception.addCustomData("userFullName", model.getLastUpdatedBy());
                    exception.addCustomData("newVersionAllowed", currentUserIsOwner);
                    throw exception;
                }
            }
        } else {
            return updateModel(model, values, false); // Actual, regular, update

        }
    }

    /**导入新的版本 POST /rest/models/{modelId}/newversion -> create a new model version
     * @param modelId
     * @param file
     * @return
     */
    @PostMapping(value = "/rest/models/{modelId}/newversion")
    public ModelRepresentation importNewVersion(@PathVariable String modelId, @RequestParam("file") MultipartFile file) {
        InputStream modelStream = null;
        try {
            modelStream = file.getInputStream();
        } catch (Exception e) {
            throw new BadRequestException("Error reading file inputstream", e);
        }

        return modelService.importNewVersion(modelId, file.getOriginalFilename(), modelStream);
    }

    /**更新模型
     * @param model
     * @param values
     * @param forceNewVersion
     * @return
     */
    protected ModelRepresentation updateModel(Model model, MultiValueMap<String, String> values, boolean forceNewVersion) {
        String name = values.getFirst("name");
        String key = values.getFirst("key").replaceAll(" ", "");
        String description = values.getFirst("description");
        String isNewVersionString = values.getFirst("newversion");
        String newVersionComment = null;
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(model, model.getModelType(), key);
        int version = 1;
        if (modelKeyInfo.isKeyAlreadyExists()) {
        	List<Model> mlist = modelRepository.findByKeyAndType(key, 0);
        	for(Model m : mlist) {
        		if(m.getVersion() >= version) {
        			version = m.getVersion() + 1;
        		}
        	}
        }
        boolean newVersion = false;
        if (forceNewVersion) {
            newVersion = true;
            newVersionComment = values.getFirst("comment");
        } else {
            if (isNewVersionString != null) {
                newVersion = "true".equals(isNewVersionString);
                newVersionComment = values.getFirst("comment");
            }
        }
        String json = values.getFirst("json_xml");
        try {
			ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(json);

			ObjectNode propertiesNode = (ObjectNode) editorJsonNode.get("properties");
			String processId = key;
			propertiesNode.put("process_id", processId);
			propertiesNode.put("name", name);
			if (StringUtils.isNotEmpty(description)) {
				propertiesNode.put("documentation", description);
			}
			editorJsonNode.set("properties", propertiesNode);
            model = modelService.saveModel(model.getId(), name, key, description, editorJsonNode.toString(), newVersion, newVersionComment, SecurityUtils.getCurrentUserObject());
            if(version != 1) {
            	model.setVersion(version);	//更新版本号
            	model = modelService.saveModel(model);
            }
            return new ModelRepresentation(model);
        } catch (Exception e) {
            LOGGER.error("Error saving model {}", model.getId(), e);
            throw new BadRequestException("Process model could not be saved " + model.getId());
        }
    }

    /**创建模型
     * @param name
     * @param description
     * @param modelType
     * @param editorJson
     * @return
     */
    protected ModelRepresentation createNewModel(String name, String description, Integer modelType, String editorJson) {
        ModelRepresentation model = new ModelRepresentation();
        model.setName(name);
        model.setDescription(description);
        model.setModelType(modelType);
        Model newModel = modelService.createModel(model, editorJson, SecurityUtils.getCurrentUserObject());
        return new ModelRepresentation(newModel);
    }
    
}
