package com.condominiosaas.repository;

import com.condominiosaas.domain.entity.Empresa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Page<Empresa> findByRazaoSocialContainingIgnoreCaseAndCnpjContainingIgnoreCase(
            String razaoSocial,
            String cnpj,
            Pageable pageable
    );
}
