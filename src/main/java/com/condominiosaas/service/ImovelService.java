package com.condominiosaas.service;

import com.condominiosaas.domain.entity.Imovel;
import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.dto.ImovelDto;
import com.condominiosaas.repository.ImovelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.lang.Math.max;
import static org.springframework.data.domain.Sort.Direction.fromString;
import static org.springframework.data.domain.Sort.by;

@Service
public class ImovelService {
	private final ImovelRepository repository;

	public ImovelService(ImovelRepository repository) {
		this.repository = repository;
	}

	public List<ImovelDto> getAll(Long empresaId) {
		List<Imovel> list;
		if (empresaId == null || empresaId == 0) list = repository.findAll();
		else list = repository.findByEmpresa_Id(empresaId);
		return list.stream().map(this::toDto).collect(Collectors.toList());
	}

	public Page<ImovelDto> getPaged(int page, int pageSize, String sortBy, String direction, Long empresaId, String bloco, String apartamento) {
		Sort sort = by(fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(max(0, page - 1), pageSize, sort);

		Page<Imovel> pageResult;
		pageResult = repository.findByEmpresa_IdAndBlocoContainingIgnoreCaseAndApartamentoContainingIgnoreCase(empresaId, bloco, apartamento, pageable);
		return pageResult.map(this::toDto);
	}

	public Optional<ImovelDto> getById(Long id) {
		return repository.findById(id).map(this::toDto);
	}

	public ImovelDto create(ImovelDto dto, Long empresaId) {
		Imovel i = new Imovel();
		i.setBloco(dto.getBloco());
		i.setApartamento(dto.getApartamento());
		i.setBoxGaragem(dto.getBoxGaragem());
		// set empresa association minimal: only id
		Empresa e = new Empresa();
		e.setId(empresaId);
		i.setEmpresa(e);

		Imovel saved = repository.save(i);
		return toDto(saved);
	}

	public boolean update(Long id, ImovelDto dto, Long empresaId) {
		return repository.findById(id).map(i -> {
			i.setBloco(dto.getBloco());
			i.setApartamento(dto.getApartamento());
			i.setBoxGaragem(dto.getBoxGaragem());
			if (i.getEmpresa() == null) {
				Empresa e = new Empresa();
				e.setId(empresaId);
				i.setEmpresa(e);
			}
			repository.save(i);
			return true;
		}).orElse(false);
	}

	public boolean delete(Long id) {
		return repository.findById(id).map(i -> {
			repository.delete(i);
			return true;
		}).orElse(false);
	}

	private ImovelDto toDto(Imovel i) {
		ImovelDto d = new ImovelDto();
		d.setId(i.getId());
		d.setBloco(i.getBloco());
		d.setApartamento(i.getApartamento());
		d.setBoxGaragem(i.getBoxGaragem());
		if (i.getEmpresa() != null) d.setEmpresaId(i.getEmpresa().getId());
		return d;
	}
}
