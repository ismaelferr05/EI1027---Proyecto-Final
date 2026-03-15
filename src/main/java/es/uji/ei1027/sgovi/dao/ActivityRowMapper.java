package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.Activity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityRowMapper implements RowMapper<Activity> {
    @Override
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setActivity_id(rs.getInt("activity_id"));
        activity.setName(rs.getString("name"));
        activity.setDate(rs.getDate("date").toLocalDate());
        activity.setDuration(rs.getInt("duration"));
        activity.setLocation(rs.getString("location"));
        activity.setCategory(rs.getString("category"));
        activity.setDescription(rs.getString("description"));
        activity.setTrainer_id(rs.getInt("trainer_id"));
        return activity;
    }
}

