package com.fastbee.oauth.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * kerwincui
 */
@Controller
@SessionAttributes("authorizationRequest")
public class ConfirmAccessController {


    @RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(Map<String, Object> model, Principal principal ) {
//        AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
//        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
//
//        Map<String, String> scopes = new LinkedHashMap<String, String>();
//        for (String scope : clientAuth.getScope()) {
//            scopes.put(OAuth2Utils.SCOPE_PREFIX + scope, "false");
//        }
//        for (Approval approval : approvalStore.getApprovals(principal.getName(), client.getClientId())) {
//            if (clientAuth.getScope().contains(approval.getScope())) {
//                scopes.put(OAuth2Utils.SCOPE_PREFIX + approval.getScope(),
//                        approval.getStatus() == Approval.ApprovalStatus.APPROVED ? "true" : "false");
//            }
//        }
//        model.put("auth_request", clientAuth);
//        model.put("client", client);
//        model.put("scopes", scopes);
        return "confirm.html";
    }
}
