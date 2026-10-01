package com.condominiosaas.service;

import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.messaging.EnvioEmailRequest;
import com.condominiosaas.messaging.RabbitProducer;
import com.condominiosaas.util.EncryptionHelper;
import com.condominiosaas.dto.EmpresaDto;
import com.condominiosaas.domain.enums.TipoEmpresaAtivo;

import com.condominiosaas.repository.EmpresaRepository;
import com.condominiosaas.repository.AuthUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.time.LocalDateTime.now;

@Service
public class EmpresaService {
	private final EmpresaRepository empresaRepository;
	private final RabbitProducer producer;
	private final EmailTemplateService emailTemplateService;
	private final AuthUserRepository authUserRepository;

	public EmpresaService(
		EmpresaRepository empresaRepository, RabbitProducer producer, EmailTemplateService emailTemplateService, 
		AuthUserRepository authUserRepository) {
			this.empresaRepository = empresaRepository;
			this.producer = producer;
			this.emailTemplateService = emailTemplateService;
			this.authUserRepository = authUserRepository;
	}

	public List<EmpresaDto> getAll() {
		return empresaRepository.findAll()
			.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

		public Page<EmpresaDto> getPaged(int page, int pageSize, String sortBy, String direction, Long empresaId, String bloco, String apartamento) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), pageSize, sort);

		Page<Empresa> pageResult;
		pageResult = empresaRepository.findByRazaoSocialContainingIgnoreCaseAndCnpjContainingIgnoreCase(null, null, pageable);
		return pageResult.map(this::toDto);
	}

	public Optional<EmpresaDto> getById(Long id) {
		return empresaRepository.findById(id).map(this::toDto);
	}

	public EmpresaDto create(EmpresaDto dto) {
		Empresa e = new Empresa();
		e.setRazaoSocial(dto.getRazaoSocial());
		e.setFantasia(dto.getFantasia());
		e.setCnpj(dto.getCnpj());
		e.setNome(dto.getNome());
		e.setCelular(dto.getCelular());
		e.setEmail(dto.getEmail());
		if (dto.getSenha() != null && !dto.getSenha().isEmpty()) e.setSenha(EncryptionHelper.encrypt(dto.getSenha()));
		e.setAtivo(TipoEmpresaAtivo.Ativo);
		e.setDataInclusao(now());

		Empresa saved = empresaRepository.save(e);

		// enviar email de boas-vindas via Rabbit
		try {
			String corpo = emailTemplateService.gerarBoasVindasEmpresa(saved.getRazaoSocial());
			EnvioEmailRequest req = new EnvioEmailRequest(
				saved.getEmail(),
				"Bem-vindo ao Sistema",
				corpo, saved.getId());
			producer.publicarMensagem(req);
		} catch (Exception ex) {
			// não interrompe fluxo
		}

		return toDto(saved);
	}

	public boolean update(Long id, EmpresaDto dto) {
		return empresaRepository.findById(id).map(e -> {
			boolean statusMudouParaInativo = e.getAtivo() == TipoEmpresaAtivo.Ativo && dto.getAtivo() != TipoEmpresaAtivo.Ativo;

			e.setRazaoSocial(dto.getRazaoSocial());
			e.setFantasia(dto.getFantasia());
			e.setCnpj(dto.getCnpj());
			if (dto.getSenha() != null && !dto.getSenha().isEmpty())e.setSenha(EncryptionHelper.encrypt(dto.getSenha()));
			e.setNome(dto.getNome());
			e.setCelular(dto.getCelular());
			e.setEmail(dto.getEmail());
			e.setDataAlteracao(now());
			empresaRepository.save(e);
			// sincronizar status dos usuários se necessário
			if (statusMudouParaInativo) {
				try {
					var usuarios = authUserRepository.findByEmpresaId(e.getId());
					for (var usuario : usuarios) {
						usuario.setEmpresaAtiva(TipoEmpresaAtivo.Inativo);
						usuario.setDataAlteracao(now());
					}
					authUserRepository.saveAll(usuarios);
				} catch (Exception ex) {
					// ignore
				}
			}

			// enviar email de alteração de dados cadastrais
			try {
				String corpo = emailTemplateService.gerarEmpresaAlterada(e.getRazaoSocial());
				EnvioEmailRequest req = new EnvioEmailRequest(e.getEmail(), 
					"Empresa alteração de Dados Cadastrais", corpo, e.getId());
				producer.publicarMensagem(req);
			} catch (Exception ex) {
				// ignore
			}
			return true;
		}).orElse(false);
	}

	public boolean delete(Long id) {
		return empresaRepository.findById(id).map(e -> {
			empresaRepository.delete(e);
			return true;
		}).orElse(false);
	}

	private EmpresaDto toDto(Empresa e) {
		EmpresaDto d = new EmpresaDto();
		d.setId(e.getId());
		d.setRazaoSocial(e.getRazaoSocial());
		d.setFantasia(e.getFantasia());
		d.setCnpj(e.getCnpj());
		d.setNome(e.getNome());
		d.setCelular(e.getCelular());
		d.setEmail(e.getEmail());

		return d;
	}
}
