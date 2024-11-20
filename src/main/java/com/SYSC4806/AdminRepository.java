package com.SYSC4806;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository  extends CrudRepository<Admin, Integer> {
    Optional<Admin> findAdminByUsername(String username);
}
