package com.aventura.api.repository;

import com.aventura.api.entity.CiudadOffline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadOfflineRepository extends JpaRepository<CiudadOffline, Long> {

}
