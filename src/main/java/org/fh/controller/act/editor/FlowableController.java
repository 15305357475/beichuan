package org.fh.controller.act.editor;

import org.fh.util.Jurisdiction;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.DefaultPrivileges;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

/**
 * 说明：模拟流程编辑器内置登录
 * 作者：fsci
 * 授权：bsic
 */
@RestController
@RequestMapping("/login")
public class FlowableController {

    @RequestMapping(value = "/rest/account", method = RequestMethod.GET, produces = "application/json")
    public UserRepresentation getAccount() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(Jurisdiction.getUsername());
        userRepresentation.setEmail("admin@flowable.org");
        userRepresentation.setFullName(Jurisdiction.getName());
        // userRepresentation.setLastName("Administrator");
        userRepresentation.setFirstName(Jurisdiction.getName());
        List<String> privileges = new ArrayList<>();
        privileges.add(DefaultPrivileges.ACCESS_MODELER);
        privileges.add(DefaultPrivileges.ACCESS_IDM);
        privileges.add(DefaultPrivileges.ACCESS_ADMIN);
        privileges.add(DefaultPrivileges.ACCESS_TASK);
        privileges.add(DefaultPrivileges.ACCESS_REST_API);
        userRepresentation.setPrivileges(privileges);
        return userRepresentation;
    }

}