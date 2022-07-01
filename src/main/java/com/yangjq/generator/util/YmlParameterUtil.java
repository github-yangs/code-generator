package com.yangjq.generator.util;

import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/**
 * @author yangjq
 * @since 2022/6/28
 */
public class YmlParameterUtil {

  private static final String APPLICATION_YML = "application.yml";
  private static final String PROPERTY_NAME;

  static {
    PROPERTY_NAME = getYmlParameter("generator.file.active", APPLICATION_YML);
  }

  public static String getYmlParameter(String key) {
    return getYmlParameter(key, PROPERTY_NAME);
  }

  private static String getYmlParameter(String key, String fileName) {
    Properties properties;
    try {
      ClassPathResource resource = new ClassPathResource(fileName);
      YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
      factoryBean.setResources(resource);
      properties = factoryBean.getObject();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return properties.getProperty(key);
  }

}
