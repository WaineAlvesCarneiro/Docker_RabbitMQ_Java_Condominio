package com.condominiosaas.repository;

import com.condominiosaas.domain.entity.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthUserRepositoryImpl implements AuthUserRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<AuthUser> getAllPaged(int page, int pageSize, String orderBy, String direction, Long empresaId, String userName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AuthUser> cq = cb.createQuery(AuthUser.class);
		Root<AuthUser> root = cq.from(AuthUser.class);

		List<Predicate> predicates = new ArrayList<>();
		if (empresaId != null && empresaId != 0)
			predicates.add(cb.equal(root.get("empresaId"), empresaId));
		if (userName != null && !userName.isBlank())
			predicates.add(cb.like(cb.lower(root.get("userName")), "%" + userName.toLowerCase() + "%"));
		if (!predicates.isEmpty())
			cq.where(predicates.toArray(new Predicate[0]));

		// ordering
		if (orderBy == null || orderBy.isBlank()) orderBy = "id";
		boolean asc = "ASC".equalsIgnoreCase(direction);
		switch (orderBy.toLowerCase()) {
			case "username":
				cq.orderBy(asc ? cb.asc(root.get("userName")) : cb.desc(root.get("userName")));
				break;
			case "email":
				cq.orderBy(asc ? cb.asc(root.get("email")) : cb.desc(root.get("email")));
				break;
			default:
				cq.orderBy(asc ? cb.asc(root.get("id")) : cb.desc(root.get("id")));
				break;
		}

		TypedQuery<AuthUser> query = em.createQuery(cq.select(root));

		int actualPage = Math.max(1, page);
		int actualPageSize = Math.max(1, pageSize);
		int firstResult = (actualPage - 1) * actualPageSize;

		query.setFirstResult(firstResult);
		query.setMaxResults(actualPageSize);

		List<AuthUser> resultList = query.getResultList();

		// count
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AuthUser> countRoot = countQuery.from(AuthUser.class);
		List<Predicate> countPreds = new ArrayList<>();
		if (empresaId != null && empresaId != 0)
			countPreds.add(cb.equal(countRoot.get("empresaId"), empresaId));
		if (userName != null && !userName.isBlank())
			countPreds.add(cb.like(cb.lower(countRoot.get("userName")), "%" + userName.toLowerCase() + "%"));
		if (!countPreds.isEmpty())
			countQuery.where(countPreds.toArray(new Predicate[0]));
		countQuery.select(cb.count(countRoot));
		Long total = em.createQuery(countQuery).getSingleResult();

		Pageable pageable = PageRequest.of(actualPage - 1, actualPageSize);
		return new PageImpl<>(resultList, pageable, total);
	}
}
