package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TrainerDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Trainer trainer) {
        String sql = "INSERT INTO trainer (name, occupation, last_name, email, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, trainer.getName(), trainer.getOccupation(), trainer.getLastName(),
                trainer.getEmail(), trainer.getPhone(), trainer.getAddress());
    }

    public void update(Trainer trainer) {
        String sql = "UPDATE trainer SET name=?, occupation=?, last_name=?, email=?, phone=?, address=? WHERE id_trainer=?";
        jdbcTemplate.update(sql, trainer.getName(), trainer.getOccupation(), trainer.getLastName(),
                trainer.getEmail(), trainer.getPhone(), trainer.getAddress(), trainer.getIdTrainer());
    }

    public void delete(int idTrainer) {
        String sql = "DELETE FROM trainer WHERE id_trainer=?";
        jdbcTemplate.update(sql, idTrainer);
    }

    public Trainer get(int idTrainer) {
        String sql = "SELECT * FROM trainer WHERE id_trainer=?";
        return jdbcTemplate.queryForObject(sql, new TrainerRowMapper(), idTrainer);
    }

    public List<Trainer> getAll() {
        String sql = "SELECT * FROM trainer";
        return jdbcTemplate.query(sql, new TrainerRowMapper());
    }
}

