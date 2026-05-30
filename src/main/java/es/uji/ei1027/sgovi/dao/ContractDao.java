package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ContractDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int add(Contract contract) {
        String sql = "INSERT INTO Contract (wage, startDate, endDate, url, negotiation_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, contract.getWage());
            ps.setDate(2, Date.valueOf(contract.getStartDate()));
            ps.setDate(3, Date.valueOf(contract.getEndDate()));
            ps.setString(4, contract.getUrl());
            if (contract.getIdNegotiation() != null) {
                ps.setInt(5, contract.getIdNegotiation());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);
        Number generatedKey = keyHolder.getKey();
        int generatedId = generatedKey == null ? 0 : generatedKey.intValue();
        contract.setIdContract(generatedId);
        return generatedId;
    }

    public void updateUrl(int idContract, String url) {
        String sql = "UPDATE Contract SET url=? WHERE contract_id=?";
        jdbcTemplate.update(sql, url, idContract);
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

    public List<Contract> getByOviUserId(int idOviUser) {
        String sql = """
                SELECT c.*
                FROM Contract c
                JOIN Negotiation n ON c.negotiation_id = n.negotiation_id
                JOIN Request r ON n.request_id = r.request_id
                WHERE r.oviuser_id = ?
                ORDER BY c.startDate DESC, c.contract_id DESC
                """;
        return jdbcTemplate.query(sql, new ContractRowMapper(), idOviUser);
    }

                    public Contract getByNegotiationId(int idNegotiation) {
                        String sql = "SELECT * FROM Contract WHERE negotiation_id=?";
                        List<Contract> contracts = jdbcTemplate.query(sql, new ContractRowMapper(), idNegotiation);
                        return contracts.isEmpty() ? null : contracts.get(0);
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

    public boolean belongsToOviUser(int idContract, int idOviUser) {
        String sql = """
                SELECT COUNT(*)
                FROM Contract c
                JOIN Negotiation n ON c.negotiation_id = n.negotiation_id
                JOIN Request r ON n.request_id = r.request_id
                WHERE c.contract_id = ? AND r.oviuser_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idContract, idOviUser);
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
