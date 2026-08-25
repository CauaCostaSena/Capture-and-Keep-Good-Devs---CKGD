package com.ckgd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Expõe a pasta de uploads (fotos de perfil das empresas) como recurso estático,
 * já que os arquivos ficam fora do classpath (diretório configurável via ckgd.upload.dir).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${ckgd.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Path.toUri() só garante a barra final quando o diretório já existe no momento da
        // chamada — como ele é criado sob demanda no primeiro upload, montamos a URI à mão
        // para não depender dessa checagem de existência.
        String uri = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString();
        String location = uri.endsWith("/") ? uri : uri + "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
