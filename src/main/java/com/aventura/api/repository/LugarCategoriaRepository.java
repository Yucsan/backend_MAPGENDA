package com.aventura.api.repository;

import com.aventura.api.entity.LugarCategoria;
import com.aventura.api.entity.LugarCategoriaId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarCategoriaRepository extends JpaRepository<LugarCategoria, LugarCategoriaId> {

}
