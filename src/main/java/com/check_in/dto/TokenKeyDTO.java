package com.check_in.dto;

import java.security.Key;

public class TokenKeyDTO {
    private String agentID;
    private String token;

    public String getAgentID() {
        return agentID;
    }

    public String getToken() {
        return token;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
