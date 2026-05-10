package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;

import java.util.Collection;

public interface UserDao {
    UserDetails getUserByEmail(String email);
    Collection<UserDetails> getAllUsers();
}
