package proj.checkIN.DB;

import java.sql.SQLException;
import java.util.List;

public interface AgentAccountLogDAO {
    public List<AgentAccountLogDTO> readAll(AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException;
    public int insert(AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException;
    public void delete(AgentAccountLogDTO dto) throws SQLException, ClassNotFoundException;
}