package proj.checkIN.DB;

import proj.checkIN.DB.RemoteDeviceDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class RemoteDeviceDAOImpl implements RemoteDeviceDAO {
    private static RemoteDeviceDAOImpl rdDao;
    private MariaDBConnector mdbc = MariaDBConnector.getInstance();

    private Connection con;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private StringBuffer query;

    private RemoteDeviceDAOImpl () {}

    public static RemoteDeviceDAOImpl getInstance() {
        if (rdDao == null) {
            rdDao = new RemoteDeviceDAOImpl();
        }
        return rdDao;
    }

    public RemoteDeviceDTO read(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 가져올 데이터 유무 확인

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM RemoteDevice WHERE agentID = ? AND deviceID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getDeviceID());

        rs = pstmt.executeQuery();
        RemoteDeviceDTO ret = new RemoteDeviceDTO();
        while(rs.next()) {
            ret.setAgentID(rs.getString("agentID"));
            ret.setDeviceID(rs.getString("deviceID"));
            ret.setDeviceName(rs.getString("deviceName"));
            ret.setEnrollmentDate(rs.getString("enrollmentDate"));
            ret.setDeviceEnable(rs.getBoolean("deviceEnable"));
        }

        disconnect();
        return ret;
    }

    public List<RemoteDeviceDTO> readAll(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existAccount(dto);  // 가져올 데이터 유무 확인

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM RemoteDevice WHERE agentID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        rs = pstmt.executeQuery();
        List<RemoteDeviceDTO> dtos = new ArrayList<RemoteDeviceDTO>();
        while(rs.next()) {
            RemoteDeviceDTO rto = new RemoteDeviceDTO();
            rto.setAgentID(rs.getString("agentID"));
            rto.setDeviceID(rs.getString("deviceID"));
            rto.setDeviceName(rs.getString("deviceName"));
            rto.setEnrollmentDate(rs.getString("enrollmentDate"));
            rto.setDeviceEnable(rs.getBoolean("deviceEnable"));
            dtos.add(rto);
        }

        disconnect();
        return dtos;
    }

    public synchronized int insert(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return 0;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("INSERT INTO RemoteDevice VALUES(?, ?, ?, ?, ?)");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getDeviceID());
        pstmt.setString(3, dto.getDeviceName());
        pstmt.setString(4, dto.getEnrollmentDate());
        pstmt.setBoolean(5, dto.isDeviceEnable());

        pstmt.executeUpdate();
        disconnect();

        return 1;
    }

    public synchronized int update(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 변경할 데이터 존재 여부 확인

        if(cnt == 0)
            return 0;

        RemoteDeviceDTO origin = read(dto);  // 변경 사항 유무 확인
        if(origin.getDeviceName().equals(dto.getDeviceName()) && origin.getEnrollmentDate().equals(dto.getEnrollmentDate()) && origin.isDeviceEnable() == dto.isDeviceEnable()) {
            return 0;
        }

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("UPDATE RemoteDevice SET deviceName = ?, enrollmentDate = ?, deviceEnable = ? WHERE agentID = ? and deviceID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getDeviceName());
        pstmt.setString(2, dto.getEnrollmentDate());
        pstmt.setBoolean(3, dto.isDeviceEnable());
        pstmt.setString(4, dto.getAgentID());
        pstmt.setString(5, dto.getDeviceID());

        pstmt.executeUpdate();
        disconnect();
        return 1;
    }

    public void delete(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("DELETE FROM RemoteDevice WHERE agentID = ? AND deviceID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getDeviceID());

        pstmt.executeUpdate();
        disconnect();
    }

    public boolean isKey(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return true;
        else
            return false;
    }

    private int existAccount(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM RemoteDevice WHERE agentID = ?");
        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        rs = pstmt.executeQuery();

        rs.next();
        int ret = rs.getInt("cnt");

        disconnect();
        return ret;
    }

    private int existKey(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM RemoteDevice WHERE agentID = ? AND deviceID = ?");
        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getDeviceID());
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