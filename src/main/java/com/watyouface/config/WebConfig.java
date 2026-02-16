package com.watyouface.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path mediaDir = Paths.get(System.getProperty("user.dir"), "media")
        .toAbsolutePath().normalize();

    registry.addResourceHandler("/media/**")
        .addResourceLocations("file:" + mediaDir + "/")
        .setCachePeriod(3600);
  }
}
