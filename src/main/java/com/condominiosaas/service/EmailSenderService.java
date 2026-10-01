package com.condominiosaas.service;

import com.condominiosaas.domain.entity.Empresa;
import com.condominiosaas.repository.EmpresaRepository;
import com.condominiosaas.util.EncryptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailSenderService {
	private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);
	private final EmpresaRepository empresaRepository;

	public EmailSenderService(EmpresaRepository empresaRepository) {
		this.empresaRepository = empresaRepository;
	}

	public boolean enviarSmtpAsync(String para, String assunto, String corpo, Long empresaId) {
		try {
			var opt = empresaRepository.findById(empresaId);
			if (opt.isEmpty()) {
				logger.error("[SMTP] Configurações não encontradas para empresa {}", empresaId);
				return false;
			}

			Empresa empresa = opt.get();
			if (empresa.getSenha() == null || empresa.getSenha().isEmpty()) {
				logger.error("[SMTP] Senha SMTP não configurada para empresa {}", empresaId);
				return false;
			}

			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost(empresa.getHost());
			mailSender.setPort(empresa.getPorta());
			mailSender.setUsername(empresa.getEmail());
			// senha no banco está criptografada conforme .NET EncryptionHelper
			String senhaReal = empresa.getSenha();
			try {
				senhaReal = EncryptionHelper.decrypt(empresa.getSenha());
			} catch (Exception ex) {
				logger.warn("[SMTP] Falha ao descriptografar senha, usando valor bruto", ex);
			}
			// define a senha do mail sender
			mailSender.setPassword(senhaReal);

			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.debug", "false");

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(empresa.getEmail(), empresa.getFantasia());
			helper.setTo(para);
			helper.setSubject(assunto);
			helper.setText(corpo, true);

			// send asynchronously
			CompletableFuture.runAsync(() -> {
				try {
					mailSender.send(message);
					logger.info("[SMTP] E-mail enviado para {} via host {}", para, empresa.getHost());
				} catch (Exception ex) {
					logger.error("[SMTP] Falha ao enviar e-mail", ex);
				}
			});

			return true;
		} catch (Exception ex) {
			logger.error("[SMTP] Erro ao montar/disparar e-mail", ex);
			return false;
		}
	}
}
