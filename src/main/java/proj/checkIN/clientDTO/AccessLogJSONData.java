package proj.checkIN.clientDTO;

import java.util.ArrayList;

public class AccessLogJSONData {
    private String agentID;
    private ArrayList<AccessLogItem> AccessLogList;

    public AccessLogJSONData(String id, ArrayList<AccessLogItem> accessLogList) {
        this.agentID = id;
        AccessLogList = accessLogList;
    }

    public String getId() {
        return agentID;
    }

    public void setId(String id) {
        this.agentID = id;
    }

    public ArrayList<AccessLogItem> getAccessLogList() {
        return AccessLogList;
    }

    public void setAccessLogList(ArrayList<AccessLogItem> accessLogList) {
        AccessLogList = accessLogList;
    }
}
