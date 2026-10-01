package com.condominiosaas.config;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class SwaggerBrowserOpener {

	private static final Logger logger = LoggerFactory.getLogger(SwaggerBrowserOpener.class);

	@EventListener(ApplicationReadyEvent.class)
	public void openSwaggerUi(ApplicationReadyEvent event) {
		int port = ((WebServerApplicationContext) event.getApplicationContext()).getWebServer().getPort();
		URI swaggerUri = URI.create("http://localhost:" + port + "/swagger-ui/index.html");
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(swaggerUri);
			} else {
				openWithOperatingSystem(swaggerUri);
			}
			logger.info("Abrindo a documentação Swagger em {}", swaggerUri);
		} catch (IOException | UnsupportedOperationException exception) {
			logger.warn("Não foi possível abrir o navegador automaticamente. A documentação está em {}", swaggerUri, exception);
		}
	}

	private void openWithOperatingSystem(URI uri) throws IOException {
		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		List<String> command;
		if (osName.contains("win")) {
			command = List.of("rundll32", "url.dll,FileProtocolHandler", uri.toString());
		} else if (osName.contains("mac")) {
			command = List.of("open", uri.toString());
		} else if (osName.contains("nix") || osName.contains("nux")) {
			command = List.of("xdg-open", uri.toString());
		} else {
			throw new UnsupportedOperationException("Sistema operacional não suportado para abertura automática do navegador");
		}
		new ProcessBuilder(command).start();
	}
}
