package com.check_in.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.check_in.dto.UserSiteInformationDTO;

@Repository
public class UserSiteInformationDAO {
    private static UserSiteInformationDAO usiDao;
    private MariaDBConnector mdbc = MariaDBConnector.getInstance();

    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    StringBuffer query;

    private UserSiteInformationDAO() {}

    public static UserSiteInformationDAO getInstance() {
        if(usiDao == null) {
            usiDao = new UserSiteInformationDAO();
        }
        return usiDao;
    }

    public UserSiteInformationDTO read(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 가져올 데이터 유무 확인

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM UserSiteInformation WHERE ID = ? AND Name = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getName());

        rs = pstmt.executeQuery();
        UserSiteInformationDTO ret = new UserSiteInformationDTO();
        while(rs.next()) {
            ret.setAgentID(rs.getString("ID"));
            ret.setName(rs.getString("Name"));
            ret.setURL(rs.getString("URL"));
            ret.setID(rs.getString("SiteID"));
            ret.setPW(rs.getString("SitePassword"));
        }

        disconnect();
        return ret;
    }

    public List<UserSiteInformationDTO> readAll(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existAccount(dto);  // 가져올 데이터 유무 확인

        if(cnt == 0)
            return null;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT * FROM UserSiteInformation WHERE ID = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());

        rs = pstmt.executeQuery();
        List<UserSiteInformationDTO> dtos = new ArrayList<UserSiteInformationDTO>();
        while(rs.next()) {
            UserSiteInformationDTO uto = new UserSiteInformationDTO();
            uto.setAgentID(rs.getString("ID"));
            uto.setName(rs.getString("Name"));
            uto.setURL(rs.getString("URL"));
            uto.setID(rs.getString("SiteID"));
            uto.setPW(rs.getString("SitePassword"));
            dtos.add(uto);
        }

        disconnect();
        return dtos;
    }

    public synchronized int insert(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 기존 데이터와 키값 중복 여부 확인

        if(cnt != 0)
            return 0;

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("INSERT INTO UserSiteInformation VALUES(?, ?, ?, ?, ?)");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getName());
        pstmt.setString(3, dto.getURL());
        pstmt.setString(4, dto.getID());
        pstmt.setString(5, dto.getPW());

        pstmt.executeUpdate();
        disconnect();

        return 1;
    }

    public synchronized int update(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        int cnt = existKey(dto);  // 변경할 데이터 존재 여부 확인

        if(cnt == 0)
            return 0;

        UserSiteInformationDTO origin = read(dto);  // 변경 사항 유무 확인
        if(origin.getURL().equals(dto.getURL()) && origin.getID().equals(dto.getID()) && origin.getPW().equals(dto.getPW())) {
            return 0;
        }

        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("UPDATE UserSiteInformation SET URL = ?, SiteID = ?, SitePassword = ? WHERE ID = ? and Name = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getURL());
        pstmt.setString(2, dto.getID());
        pstmt.setString(3, dto.getPW());
        pstmt.setString(4, dto.getAgentID());
        pstmt.setString(5, dto.getName());

        pstmt.executeUpdate();
        disconnect();
        return 1;
    }

    public void delete(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("DELETE FROM UserSiteInformation WHERE ID = ? AND Name = ?");

        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getName());

        pstmt.executeUpdate();
        disconnect();
    }

    private int existAccount(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM UserSiteInformation WHERE ID = ?");
        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        rs = pstmt.executeQuery();

        rs.next();
        int ret = rs.getInt("cnt");

        disconnect();
        return ret;
    }

    private int existKey(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException {
        con = mdbc.getConnection();
        query = new StringBuffer();
        query.append("SELECT COUNT(*) AS cnt FROM UserSiteInformation WHERE ID = ? AND Name = ?");
        pstmt = con.prepareStatement(query.toString());
        pstmt.setString(1, dto.getAgentID());
        pstmt.setString(2, dto.getName());
        rs = pstmt.executeQuery();

        rs.next();
        int ret = rs.getInt("cnt");

        disconnect();
        return ret;
    }

    public void disconnect() throws SQLException {
        if(rs != null) {
            rs.close();
        }
        pstmt.close();
        con.close();
    }
}
