package proj.checkIN.clientDTO;

public class LoginNumberJSONData {
    private String LoginNumber;

    public LoginNumberJSONData(String loginNumber) {
        LoginNumber = loginNumber;
    }

    public String getLoginNumber() {
        return LoginNumber;
    }

    public void setLoginNumber(String loginNumber) {
        LoginNumber = loginNumber;
    }
}
