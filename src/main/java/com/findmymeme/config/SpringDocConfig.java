package com.findmymeme.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Profile("!prod")
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Find My Meme API Document")
                .version("v0.0.1")
                .description("Find My Meme 프로젝트의 API 명세서입니다.");

        String jwtSchemeName = "JWT Authentication";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .tags(List.of(
                        new Tag().name("1. Authentication").description("사용자 인증 및 회원가입 관련 API"),
                        new Tag().name("2. Users").description("사용자 정보 조회 및 관리 API"),
                        new Tag().name("3. Meme Posts").description("밈 게시물 관련 API"),
                        new Tag().name("4. Find Posts").description("'찾아줘' 게시판 게시글 관련 API"),
                        new Tag().name("5. Find Post Comments").description("'찾아줘' 게시판 댓글 관련 API"),
                        new Tag().name("6. Tags").description("태그 관련 API"),
                        new Tag().name("7. Files").description("파일 업로드 및 관리 관련 API"),
                        new Tag().name("8. Admin").description("관리자용 API")
                ))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}