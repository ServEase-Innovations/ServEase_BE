package com.springboot.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration
// implements WebMvcConfigurer
{

    @Bean

    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override

            public void addCorsMappings(CorsRegistry registry) {
                // allow all origins to access our service
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowedHeaders("*");
            }
        };
    }

    /*
     * @Override
     * public void addCorsMappings(CorsRegistry registry) {
     * 
     * registry
     * // Enable cross-origin request handling for the specified path pattern.
     * // Exact path mapping URIs (such as "/admin") are supported as well as
     * Ant-style path patterns (such as "/admin/**").
     * //.addMapping("/*")
     * //.allowedOrigins("*")
     * // .allowedOriginPatterns("")
     * //.allowCredentials(false)
     * //.allowedHeaders("*")
     * //.exposedHeaders("*")
     * //.maxAge(60 *30)
     * //.allowedMethods("*")
     * .addMapping("/**")
     * .allowedOriginPatterns("*")
     * .allowCredentials(true)
     * .allowedHeaders("*")
     * .maxAge(60*30)
     * .allowedMethods("GET","POST","PATCH","DELETE","PUT","OPTIONS");
     * ;
     * }
     * 
     * @Bean
     * CorsConfigurationSource corsConfigurationSource() {
     * CorsConfiguration configuration = new CorsConfiguration();
     * configuration.setAllowCredentials(true);
     * configuration.setAllowedOriginPatterns(Arrays.asList("*"));
     * //configuration.setAllowedOrigins(Arrays.asList("*"));
     * configuration.setAllowedMethods(Arrays.asList("*"));
     * configuration.setAllowedHeaders(Arrays.asList("*"));
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * source.registerCorsConfiguration("/**", configuration);
     * return source;
     * }
     */

}
