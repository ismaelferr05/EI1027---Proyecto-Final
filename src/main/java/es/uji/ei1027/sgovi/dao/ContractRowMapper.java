package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Contract;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContractRowMapper implements RowMapper<Contract> {
    @Override
    public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contract contract = new Contract();
        contract.setIdContract(rs.getInt("id_contract"));
        contract.setWage(rs.getString("wage"));
        contract.setStartDate(rs.getDate("start_date").toLocalDate());
        contract.setEndDate(rs.getDate("end_date").toLocalDate());
        contract.setUrl(rs.getString("url"));
        return contract;
    }
}

