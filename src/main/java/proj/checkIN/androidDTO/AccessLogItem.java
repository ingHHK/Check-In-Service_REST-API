package proj.checkIN.androidDTO;

class AccessLogItem {
    private String ip;
    private String time;
    private String status;

    public AccessLogItem(String ip, String time, String status) {
        this.ip = ip;
        this.time = time;
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
