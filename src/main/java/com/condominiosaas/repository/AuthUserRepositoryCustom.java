package com.condominiosaas.repository;

import com.condominiosaas.domain.entity.AuthUser;
import org.springframework.data.domain.Page;

public interface AuthUserRepositoryCustom {
	Page<AuthUser> getAllPaged(int page, int pageSize, String orderBy, String direction, Long empresaId, String userName);
}
