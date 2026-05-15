package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ContractDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Contract contract) {
        String sql = "INSERT INTO Contract (wage, startDate, endDate, url, negotiation_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, contract.getWage(), Date.valueOf(contract.getStartDate()),
                Date.valueOf(contract.getEndDate()), contract.getUrl(), contract.getIdNegotiation());
    }

    public void update(Contract contract) {
        String sql = "UPDATE Contract SET wage=?, startDate=?, endDate=?, url=?, negotiation_id=? WHERE contract_id=?";
        jdbcTemplate.update(sql, contract.getWage(), Date.valueOf(contract.getStartDate()),
                Date.valueOf(contract.getEndDate()), contract.getUrl(),
                contract.getIdNegotiation(), contract.getIdContract());
    }

    public void delete(int idContract) {
        String sql = "DELETE FROM Contract WHERE contract_id=?";
        jdbcTemplate.update(sql, idContract);
    }

    public Contract get(int idContract) {
        String sql = "SELECT * FROM Contract WHERE contract_id=?";
        return jdbcTemplate.queryForObject(sql, new ContractRowMapper(), idContract);
    }

    public List<Contract> getAll() {
        String sql = "SELECT * FROM Contract";
        return jdbcTemplate.query(sql, new ContractRowMapper());
    }

    public List<Contract> getByPapPatiId(int idPapPati) {
        String sql = """
                SELECT c.*
                FROM Contract c
                JOIN Negotiation n ON c.negotiation_id = n.negotiation_id
                WHERE n.pappati_id = ?
                ORDER BY c.startDate DESC, c.contract_id DESC
                """;
        return jdbcTemplate.query(sql, new ContractRowMapper(), idPapPati);
    }

    public boolean belongsToPapPati(int idContract, int idPapPati) {
        String sql = """
                SELECT COUNT(*)
                FROM Contract c
                JOIN Negotiation n ON c.negotiation_id = n.negotiation_id
                WHERE c.contract_id = ? AND n.pappati_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idContract, idPapPati);
        return count > 0;
    }

    public boolean hasOverlappingContractForPapPati(int idPapPati, LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT COUNT(*)
                FROM Contract c
                JOIN Negotiation n ON c.negotiation_id = n.negotiation_id
                WHERE n.pappati_id = ?
                  AND c.startDate <= ?
                  AND c.endDate >= ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                idPapPati,
                Date.valueOf(endDate),
                Date.valueOf(startDate)
        );
        return count > 0;
    }
}

