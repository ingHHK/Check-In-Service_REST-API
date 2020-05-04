package proj.checkIN.DB;

import java.sql.SQLException;

public interface TokenKeyDAO {
    public TokenKeyDTO read(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
    public int insert(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
    public void delete(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
    public boolean isKey(TokenKeyDTO dto) throws SQLException, ClassNotFoundException;
}
