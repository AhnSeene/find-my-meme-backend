package com.findmymeme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class FileConfig implements WebMvcConfigurer {

    @Value("${file.upload.temp-dir}")
    private String tempDir;

    @Value("${file.upload.image-dir}")
    private String imageDir;

    @Value("${file.base-dir}")
    private String baseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/temps/**")
                .addResourceLocations("file:" + baseDir + "/" + tempDir + "/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + baseDir + "/" + imageDir + "/");
    }

}
