package org.fh.controller.act.editor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说明：指定stencilset_bpmn stencilset_cmmn json 文件，流程设计器默认指向的i18n为中文
 * 作者：fsci
 * 授权：bsic
 */
@RestController
@RequestMapping("/app")
public class FlowableStencilSetResource {

	@Autowired
	protected ObjectMapper objectMapper;

	@RequestMapping(value = "/rest/stencil-sets/editor", method = RequestMethod.GET, produces = "application/json")
	public JsonNode getStencilSetForEditor() {
		try {
			JsonNode stencilNode = objectMapper.readTree(this.getClass().getClassLoader().getResourceAsStream("stencilset/stencilset_bpmn.json"));
			return stencilNode;
		} catch (Exception e) {
			throw new InternalServerErrorException("Error reading bpmn stencil set json");
		}
	}

	@RequestMapping(value = "/rest/stencil-sets/cmmneditor", method = RequestMethod.GET, produces = "application/json")
	public JsonNode getCmmnStencilSetForEditor() {
		try {
			JsonNode stencilNode = objectMapper.readTree(this.getClass().getClassLoader().getResourceAsStream("stencilset/stencilset_cmmn.json"));
			return stencilNode;
		} catch (Exception e) {
			throw new InternalServerErrorException("Error reading bpmn stencil set json");
		}
	}
	
}
