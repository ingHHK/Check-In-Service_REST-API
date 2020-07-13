package proj.checkIN.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

@Repository
public class AgentAccountDAOImpl implements AgentAccountDAO {
    private static AgentAccountDAOImpl aaDao;
    private MariaDBConnector mdbc = MariaDBConnector.getInstance();

    private Connection con;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private StringBuffer query;

    private AgentAccountDAOImpl() {}

    public static AgentAccountDAOImpl getInstance() {
        if(aaDao == null) {
            aaDao = new AgentAccountDAOImpl();
        }
        return aaDao;
    }

    public AgentAccountDTO read(AgentAccountDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existAccount(dto);  // 가져올 데이터 유무 확인

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM AgentAccount WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        rs = pstmt.executeQuery();
        AgentAccountDTO ret = new AgentAccountDTO();
        while(rs.next()) {
            ret.setAgentID(rs.getString("agentID"));
            ret.setAgentPW(rs.getString("agentPW"));
            ret.setName(rs.getString("name"));
            ret.setErrorCount(rs.getInt("errorCount"));
            ret.setNumberOfDevice(rs.getInt("numberOfDevice"));
            ret.setOtpEnable(rs.getInt("otpEnable"));
        }

        disconnect();
        return ret;
    }

    public synchronized int insert(AgentAccountDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existAccount(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return 0;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("INSERT INTO AgentAccount VALUES(?, ?, ?, ?, ?, ?)");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getAgentPW());
        pstmt.setString(3, dto.getName());
        pstmt.setInt(4, dto.getErrorCount());
        pstmt.setInt(5, dto.getNumberOfDevice());
        pstmt.setInt(6, dto.getOtpEnable());

        pstmt.executeUpdate();
        disconnect();

        return 1;
    }

    public synchronized int update(AgentAccountDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existAccount(dto);  // 변경할 데이터 존재 여부 확인

        if(cnt == 0)
            return 0;

        AgentAccountDTO origin = read(dto);  // 변경 사항 유무 확인
        if(origin.getAgentPW().equals(dto.getAgentPW()) && origin.getName().equals(dto.getName()) && origin.getErrorCount() == dto.getErrorCount() && origin.getNumberOfDevice() == dto.getNumberOfDevice()) {
            return 0;
        }

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("UPDATE AgentAccount SET agentPW = ?, name = ?, errorCount = ?, numberOfDevice = ?, otpEnable = ? WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentPW());
        pstmt.setString(2, dto.getName());
        pstmt.setInt(3, dto.getErrorCount());
        pstmt.setInt(4, dto.getNumberOfDevice());
        pstmt.setString(5, dto.getAgentID());
        pstmt.setInt(6, dto.getOtpEnable());

        pstmt.executeUpdate();
        disconnect();

        return 1;
    }

    private int existAccount(AgentAccountDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM AgentAccount WHERE agentID = ?");
        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        rs = pstmt.executeQuery();

        rs.next();
        int ret = rs.getInt("cnt");

        disconnect();
        return ret;
    }

    public void delete(AgentAccountDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("DELETE FROM AgentAccount WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        pstmt.executeUpdate();
        disconnect();
    }

    public boolean isKey(AgentAccountDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existAccount(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return true;
        else
            return false;
    }

    private void disconnect() throws SQLException {
        if(rs != null) {
            rs.close();
        }
        pstmt.close();
        con.close();
    }
}
