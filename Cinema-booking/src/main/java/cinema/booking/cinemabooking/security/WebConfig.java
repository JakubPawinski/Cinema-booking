package cinema.booking.cinemabooking.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapowanie URL "/uploads/**" na folder fizyczny "uploads/" w katalogu projektu
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
