package proj.checkIN.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class AgentAccountLogDAOImpl implements AgentAccountLogDAO {
    private static AgentAccountLogDAOImpl aalDao;
    private MariaDBConnector mdbc = MariaDBConnector.getInstance();

    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    StringBuffer query;

    private AgentAccountLogDAOImpl() {}

    public static AgentAccountLogDAOImpl getInstance() {
        if (aalDao == null) {
            aalDao = new AgentAccountLogDAOImpl();
        }
        return aalDao;
    }

    public List<AgentAccountLogDTO> readAll(AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existLog(dto);

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM AgentAccountLog WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        rs = pstmt.executeQuery();
        List<AgentAccountLogDTO> dtos = new ArrayList<AgentAccountLogDTO>();
        while(rs.next()) {
            AgentAccountLogDTO lto = new AgentAccountLogDTO();
            lto.setAgentID(rs.getString("agentID"));
            lto.setLoginStatus(rs.getString("loginStatus"));
            lto.setAccessIP(rs.getString("accessIP"));
            lto.setAccessTime(rs.getString("accessTime"));
            dtos.add(lto);
        }

        disconnect();
        return dtos;
    }

    public synchronized int insert(AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("INSERT INTO AgentAccountLog VALUES(?, ?, ?, ?)");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getLoginStatus());
        pstmt.setString(3, dto.getAccessIP());
        pstmt.setString(4, dto.getAccessTime());

        pstmt.executeUpdate();
        disconnect();

        return 1;
    }

    public void delete(AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("DELETE FROM AgentAccountLog WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        pstmt.executeUpdate();
        disconnect();
    }

    private int existLog (AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM AgentAccountLog WHERE agentID = ?");
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
