package proj.checkIN.DB;

import proj.checkIN.DB.RemoteDeviceDTO;

import java.sql.SQLException;
import java.util.List;

public interface RemoteDeviceDAO {
    public RemoteDeviceDTO read(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException;
    public List<RemoteDeviceDTO> readAll(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException;
    public int insert(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException;
    public int update(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException;
    public void delete(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException;
    public boolean isKey(RemoteDeviceDTO dto) throws SQLException, ClassNotFoundException;
}
