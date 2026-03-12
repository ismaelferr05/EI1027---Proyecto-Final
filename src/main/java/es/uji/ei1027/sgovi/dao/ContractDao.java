package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public class ContractDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Contract contract) {
        String sql = "INSERT INTO contract (wage, start_date, end_date, url) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, contract.getWage(), Date.valueOf(contract.getStartDate()),
                Date.valueOf(contract.getEndDate()), contract.getUrl());
    }

    public void update(Contract contract) {
        String sql = "UPDATE contract SET wage=?, start_date=?, end_date=?, url=? WHERE id_contract=?";
        jdbcTemplate.update(sql, contract.getWage(), Date.valueOf(contract.getStartDate()),
                Date.valueOf(contract.getEndDate()), contract.getUrl(), contract.getIdContract());
    }

    public void delete(int idContract) {
        String sql = "DELETE FROM contract WHERE id_contract=?";
        jdbcTemplate.update(sql, idContract);
    }

    public Contract get(int idContract) {
        String sql = "SELECT * FROM contract WHERE id_contract=?";
        return jdbcTemplate.queryForObject(sql, new ContractRowMapper(), idContract);
    }

    public List<Contract> getAll() {
        String sql = "SELECT * FROM contract";
        return jdbcTemplate.query(sql, new ContractRowMapper());
    }
}

