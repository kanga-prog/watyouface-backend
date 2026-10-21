package com.watyouface.config;

import com.watyouface.media.MediaStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final MediaStorageService mediaStorage;

  public WebConfig(MediaStorageService mediaStorage) {
    this.mediaStorage = mediaStorage;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path mediaDir = Paths.get(mediaStorage.getBaseDir()).toAbsolutePath().normalize();

    registry.addResourceHandler("/media/**")
        .addResourceLocations("file:" + mediaDir + "/")
        .setCachePeriod(3600);
  }
}
