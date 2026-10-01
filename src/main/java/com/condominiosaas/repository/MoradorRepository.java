package com.condominiosaas.repository;

import com.condominiosaas.domain.entity.Morador;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, Long> {
    List<Morador> findByEmpresa_Id(Long empresaId);
	Page<Morador> findByEmpresa_IdAndNomeContainingIgnoreCase(Long empresaId, String nome, Pageable pageable);
}
