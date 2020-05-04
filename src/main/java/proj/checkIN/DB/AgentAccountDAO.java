package proj.checkIN.DB;

import com.check_in.dto.AgentAccountDTO;

import java.sql.SQLException;

public interface AgentAccountDAO {
    public AgentAccountDTO read(AgentAccountDTO dto) throws SQLException, ClassNotFoundException;
    public int insert(AgentAccountDTO dto) throws SQLException, ClassNotFoundException;
    public int update(AgentAccountDTO dto) throws SQLException, ClassNotFoundException;
    public void delete(AgentAccountDTO dto) throws SQLException, ClassNotFoundException;
    public boolean isKey(AgentAccountDTO dto) throws SQLException, ClassNotFoundException;
}
