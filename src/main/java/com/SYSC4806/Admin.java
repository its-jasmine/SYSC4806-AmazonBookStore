package com.SYSC4806;

import jakarta.persistence.Entity;

@Entity
public class Admin extends AppUser {

    public Admin() {
        super();
    }

    public Admin(String username, String password) {
        super(username, password);
    }


    //TO DO
    /**
     * add admin specific functions
     */





}
