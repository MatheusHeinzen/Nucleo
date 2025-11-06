package com.nucleo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class SwaggerAutoOpenConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port:8080}")
    private int port;

    @Value("${app.swagger.auto-open:false}")
    private boolean autoOpen;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (autoOpen) {
            try {
                String url = "http://localhost:" + port + "/swagger-ui.html";
                System.out.println("\n🚀 Abrindo o Swagger UI automaticamente...");
                System.out.println("📄 URL: " + url + "\n");

                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(new URI(url));
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Não foi possível abrir o navegador automaticamente: " + e.getMessage());
                System.out.println("📄 Acesse manualmente: http://localhost:" + port + "/swagger-ui.html");
            }
        }
    }
}

