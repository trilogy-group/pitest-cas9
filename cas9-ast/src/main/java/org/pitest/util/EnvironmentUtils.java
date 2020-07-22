package org.pitest.util;

import java.lang.reflect.Field;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * Ugly hack for sharing data between the main process and the forked minion process.
 *
 * See https://dzone.com/articles/how-to-change-environment-variables-in-java
 */
@UtilityClass
public class EnvironmentUtils {

  @SneakyThrows
  public void setenv(String key, String value) {
    val processEnvironment = Class.forName("java.lang.ProcessEnvironment");
    val unmodifiableMapField = getAccessibleField(processEnvironment, "theUnmodifiableEnvironment");
    val unmodifiableMap = unmodifiableMapField.get(null);
    injectIntoUnmodifiableMap(key, value, unmodifiableMap);
  }

  private Field getAccessibleField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    val field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }

  private void injectIntoUnmodifiableMap(String key, String value, Object map) throws ReflectiveOperationException {
    val unmodifiableMap = Class.forName("java.util.Collections$UnmodifiableMap");
    val field = getAccessibleField(unmodifiableMap, "m");
    @SuppressWarnings("unchecked")
    val environment = (Map<String, String>) field.get(map);
    environment.put(key, value);
  }
}
