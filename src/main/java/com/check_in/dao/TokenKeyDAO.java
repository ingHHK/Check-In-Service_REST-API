package com.check_in.dao;

import com.check_in.dto.TokenKeyDTO;

import java.sql.SQLException;

public interface TokenKeyDAO {
    public TokenKeyDTO read(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
    public int insert(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
    public void delete(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
}
