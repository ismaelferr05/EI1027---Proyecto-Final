package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public class ActivityDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(Activity activity) {
        String sql = "INSERT INTO activity (name, date, duration, location, category, description, id_trainer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, activity.getName(), Date.valueOf(activity.getDate()), activity.getDuration(),
                activity.getLocation(), activity.getCategory(), activity.getDescription(), activity.getIdTrainer());
    }

    public void update(Activity activity) {
        String sql = "UPDATE activity SET name=?, date=?, duration=?, location=?, category=?, description=?, id_trainer=? WHERE id_activity=?";
        jdbcTemplate.update(sql, activity.getName(), Date.valueOf(activity.getDate()), activity.getDuration(),
                activity.getLocation(), activity.getCategory(), activity.getDescription(), activity.getIdTrainer(), activity.getIdActivity());
    }

    public void delete(int idActivity) {
        String sql = "DELETE FROM activity WHERE id_activity=?";
        jdbcTemplate.update(sql, idActivity);
    }

    public Activity get(int idActivity) {
        String sql = "SELECT * FROM activity WHERE id_activity=?";
        return jdbcTemplate.queryForObject(sql, new ActivityRowMapper(), idActivity);
    }

    public List<Activity> getAll() {
        String sql = "SELECT * FROM activity";
        return jdbcTemplate.query(sql, new ActivityRowMapper());
    }

    public List<Activity> getByTrainer(int idTrainer) {
        String sql = "SELECT * FROM activity WHERE id_trainer=?";
        return jdbcTemplate.query(sql, new ActivityRowMapper(), idTrainer);
    }
}

