package com.demo.netty.c9marshalling;

import java.io.Serializable;

public class SubscriptResp implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4923081103118853877L;
    private Integer subScriptID;
    private String respCode;
    private String desc;

    public Integer getSubScriptID() {
        return subScriptID;
    }

    public void setSubScriptID(Integer subScriptID) {
        this.subScriptID = subScriptID;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SubscriptResp [subScriptID=" + subScriptID + ", respCode="
                + respCode + ", desc=" + desc + "]";
    }

}