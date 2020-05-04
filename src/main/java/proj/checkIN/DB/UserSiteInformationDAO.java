package proj.checkIN.DB;

import java.sql.SQLException;
import java.util.List;

public interface UserSiteInformationDAO {
    public UserSiteInformationDTO read(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException;
    public List<UserSiteInformationDTO> readAll(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException;
    public int insert(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException;
    public int update(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException;
    public void delete(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException;
    public boolean isKey(UserSiteInformationDTO dto) throws SQLException, ClassNotFoundException;
}
