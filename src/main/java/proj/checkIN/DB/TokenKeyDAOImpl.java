package proj.checkIN.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.check_in.dto.TokenKeyDTO;

@Repository
public class TokenKeyDAOImpl implements TokenKeyDAO {
    private static TokenKeyDAOImpl tkDao;
    private MariaDBConnector mdbc = MariaDBConnector.getInstance();

    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    StringBuffer query;

    private TokenKeyDAOImpl() {}

    public static TokenKeyDAOImpl getInstance() {
        if(tkDao == null) {
            tkDao = new TokenKeyDAOImpl();
        }
        return tkDao;
    }

    public TokenKeyDTO read(TokenKeyDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existToken(dto);  // 가져올 데이터 유무 확인

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM TokenKey WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        rs = pstmt.executeQuery();
        TokenKeyDTO ret = new TokenKeyDTO();
        while(rs.next()) {
            ret.setAgentID(rs.getString("agentID"));
            ret.setToken(rs.getString("token"));
        }

        disconnect();
        return ret;
    }

    public synchronized int insert(TokenKeyDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existToken(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return 0;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("INSERT INTO TokenKey VALUES(?, ?)");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getToken());

        pstmt.executeUpdate();
        disconnect();

        return 1;
    }

    public void delete(TokenKeyDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("DELETE FROM TokenKey WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        pstmt.executeUpdate();
        disconnect();
    }

    public boolean isKey(TokenKeyDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existToken(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return true;
        else
            return false;
    }

    private int existToken (TokenKeyDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM TokenKey WHERE agentID = ?");
        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        rs = pstmt.executeQuery();

        rs.next();
        int ret = rs.getInt("cnt");

        disconnect();
        return ret;
    }

    private void disconnect() throws SQLException {
        if(rs != null) {
            rs.close();
        }
        pstmt.close();
        con.close();
    }
}
