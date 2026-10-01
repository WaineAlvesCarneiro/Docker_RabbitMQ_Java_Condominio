package com.condominiosaas.service;

import com.condominiosaas.domain.entity.Morador;
import com.condominiosaas.dto.MoradorDto;
import com.condominiosaas.messaging.EnvioEmailRequest;
import com.condominiosaas.messaging.RabbitProducer;
import com.condominiosaas.repository.MoradorRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import static org.springframework.data.domain.Sort.Direction.fromString;
import static org.springframework.data.domain.Sort.by;

import java.util.List;
import java.util.Optional;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.lang.Math.max;

@Service
public class MoradorService {
	private final MoradorRepository moradorRepository;
	private final RabbitProducer producer;
	private final EmailTemplateService emailTemplateService;

	public MoradorService(
		MoradorRepository moradorRepository, RabbitProducer producer, EmailTemplateService emailTemplateService) {
			this.moradorRepository = moradorRepository;
			this.producer = producer;
			this.emailTemplateService = emailTemplateService;
	}

	public List<MoradorDto> getAll() {
		return moradorRepository.findAll()
			.stream()
			.map(this::toDto)
			.collect(toList());
	}

	public Page<MoradorDto> getPaged(int page, int pageSize, String sortBy, String direction, Long empresaId, String nome) {
		Sort sort = by(fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(max(0, page - 1), pageSize, sort);

		Page<Morador> pageResult;
		pageResult = moradorRepository.findByEmpresa_IdAndNomeContainingIgnoreCase(empresaId, nome, pageable);		
		return pageResult.map(this::toDto);
	}

	public Optional<MoradorDto> getById(Long id) {
		return moradorRepository.findById(id).map(this::toDto);
	}

	public MoradorDto create(MoradorDto dto) {
		Morador m = new Morador();
		m.setNome(dto.getNome());
		m.setCelular(dto.getCelular());
		m.setEmail(dto.getEmail());
		m.setIsProprietario(dto.getIsProprietario());
		m.setDataEntrada(dto.getDataEntrada());
		m.setDataInclusao(now());
		// relacionamentos (imovel, empresa) devem ser resolvidos pelo serviço ao final
		Morador saved = moradorRepository.save(m);

		// enviar email de boas-vindas via Rabbit
		try {
			String corpo = emailTemplateService.gerarBoasVindasMorador(saved.getNome());
			EnvioEmailRequest emailRequest = new EnvioEmailRequest(
				saved.getEmail(),
				"Bem-vindo ao Sistema!",
				corpo,
				saved.getEmpresa() == null ? 0L : saved.getEmpresa().getId());
			producer.publicarMensagem(emailRequest);
		} catch (Exception ex) {
			// ignore
		}

		return toDto(saved);
	}

	public boolean update(Long id, MoradorDto dto) {
		return moradorRepository.findById(id).map(m -> {
			m.setNome(dto.getNome());
			m.setCelular(dto.getCelular());
			m.setIsProprietario(dto.getIsProprietario());
			m.setDataAlteracao(now());
			moradorRepository.save(m);
			return true;
		}).orElse(false);
	}

	public boolean delete(Long id) {
		return moradorRepository.findById(id).map(m -> {
			moradorRepository.delete(m);
			return true;
		}).orElse(false);
	}

	private MoradorDto toDto(Morador m) {
		MoradorDto d = new MoradorDto();
		d.setId(m.getId());
		d.setNome(m.getNome());
		d.setCelular(m.getCelular());
		d.setEmail(m.getEmail());
		d.setIsProprietario(m.getIsProprietario());
		if (m.getDataEntrada() != null) d.setDataEntrada(m.getDataEntrada());
		if (m.getDataSaida() != null) d.setDataSaida(m.getDataSaida());
		if (m.getImovel() != null) d.setImovelId(m.getImovel().getId());
		if (m.getEmpresa() != null) d.setEmpresaId(m.getEmpresa().getId());
		return d;
	}
}
