package es.uji.ei1027.sgovi.dao;

import es.uji.ei1027.sgovi.model.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Repository
public class FakeUserProvider implements UserDao {
    final Map<String, UserDetails> knownUsers = new HashMap<String, UserDetails>();

    public FakeUserProvider() {
        UserDetails tecnico1 = new UserDetails();
        tecnico1.setEmail("tecnico1@gmail.com");
        tecnico1.setPassword("1tecnico123");
        tecnico1.setRole("TECNICO");
        knownUsers.put(tecnico1.getEmail(), tecnico1);

        UserDetails oviuser1 = new UserDetails();
        oviuser1.setEmail("oviuser@gmail.com");
        oviuser1.setPassword("1oviuser123");
        oviuser1.setRole("OVIUSER");
        knownUsers.put(oviuser1.getEmail(), oviuser1);
    }
    @Override
    public UserDetails getUserByEmail(String email) {
        UserDetails user = knownUsers.get(email.trim());
        if (user == null){
            return null;
        }
        return user;
    }

    @Override
    public Collection<UserDetails> getAllUsers() {
        return knownUsers.values();
    }
}
