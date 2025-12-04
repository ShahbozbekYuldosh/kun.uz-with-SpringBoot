package dasturlash.uz.config; // yoki sizning loyihadagi config paketi

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    @Value("${attach.upload.folder}")
    private String attachUploadFolder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + attachUploadFolder + "/");
    }
}
