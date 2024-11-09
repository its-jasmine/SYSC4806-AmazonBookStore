package com.SYSC4806;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    Iterable<Object> findByUsername(String user);
}