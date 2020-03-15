package proj.checkIN.androidDTO;

import java.util.ArrayList;

public class AccessLogJSONData {
    private String id;
    private ArrayList<AccessLogItem> AccessLogList;

    public AccessLogJSONData(String id, ArrayList<AccessLogItem> accessLogList) {
        this.id = id;
        AccessLogList = accessLogList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<AccessLogItem> getAccessLogList() {
        return AccessLogList;
    }

    public void setAccessLogList(ArrayList<AccessLogItem> accessLogList) {
        AccessLogList = accessLogList;
    }
}
