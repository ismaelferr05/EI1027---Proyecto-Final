package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Contract;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContractRowMapper implements RowMapper<Contract> {
    @Override
    public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contract contract = new Contract();
        contract.setIdContract(rs.getInt("contract_id"));
        contract.setWage(rs.getString("wage"));
        contract.setStartDate(rs.getDate("startDate").toLocalDate());
        contract.setEndDate(rs.getDate("endDate").toLocalDate());
        contract.setUrl(rs.getString("url"));
        contract.setNegotiation_id(rs.getInt("negotiation_id"));
        return contract;
    }
}

