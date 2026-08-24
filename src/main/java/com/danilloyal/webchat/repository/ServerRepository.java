package com.danilloyal.webchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danilloyal.webchat.model.Server;
import java.util.List;


public interface ServerRepository extends JpaRepository<Server,Long>{
    List<Server> findByOwnerId(Long ownerId);
}
