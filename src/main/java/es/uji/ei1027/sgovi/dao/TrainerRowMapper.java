package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Trainer;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainerRowMapper implements RowMapper<Trainer> {
    @Override
    public Trainer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Trainer trainer = new Trainer();
        trainer.setTrainer_id(rs.getInt("id_trainer"));
        trainer.setName(rs.getString("name"));
        trainer.setOccupation(rs.getString("occupation"));
        trainer.setLastName(rs.getString("last_name"));
        trainer.setEmail(rs.getString("email"));
        trainer.setPhone(rs.getString("phone"));
        trainer.setAddress(rs.getString("address"));
        return trainer;
    }
}

