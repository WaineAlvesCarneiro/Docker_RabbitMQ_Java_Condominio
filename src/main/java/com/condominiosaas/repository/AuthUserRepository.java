package com.condominiosaas.repository;

import com.condominiosaas.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID>, AuthUserRepositoryCustom {
	Optional<AuthUser> findByUserName(String userName);
	java.util.List<AuthUser> findByEmpresaId(Long empresaId);
}
