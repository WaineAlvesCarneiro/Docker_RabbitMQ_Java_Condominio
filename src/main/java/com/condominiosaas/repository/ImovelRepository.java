package com.condominiosaas.repository;

import com.condominiosaas.domain.entity.Imovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, Long> {
	List<Imovel> findByEmpresa_Id(Long empresaId);
	Page<Imovel> findByEmpresa_IdAndBlocoContainingIgnoreCaseAndApartamentoContainingIgnoreCase(Long empresaId, String bloco, String apartamento, Pageable pageable);
}
