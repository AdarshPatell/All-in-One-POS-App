package org.example.newchronopos.service;

import org.example.newchronopos.dao.OwnerDAO;
import org.example.newchronopos.dao.UserDAO;
import org.example.newchronopos.model.Owner;
import org.example.newchronopos.model.User;

public class AuthService {
    private final OwnerDAO ownerDAO = new OwnerDAO();
    private final UserDAO userDAO = new UserDAO();

    public Object login(String username, String plainPassword) {
        Owner owner = ownerDAO.findByUsername(username);
        if (owner != null && ownerDAO.checkPassword(owner, plainPassword)) {
            return owner;
        }

        User user = userDAO.findByUsername(username);
        if (user != null && userDAO.checkPassword(user, plainPassword)) {
            return user;
        }

        return null;
    }
}