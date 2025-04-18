package com.tomato.naraclub.common.strategy;

import java.util.Locale;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * spring physical naming strategy 를 복제.
 */
public class PhysicalNamingStrategy implements
    org.hibernate.boot.model.naming.PhysicalNamingStrategy {

  private static final String PREFIX_TABLE_NAME = "t_";
  private static final String PREFIX_SEQUENCE_NAME = "seq_";

  @Override
  public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(Identifier.toIdentifier(PREFIX_TABLE_NAME + name.getText()), jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(Identifier.toIdentifier(PREFIX_SEQUENCE_NAME + PREFIX_TABLE_NAME + name.getText()), jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
    return apply(name, jdbcEnvironment);
  }


  private Identifier apply(Identifier name, JdbcEnvironment jdbcEnvironment) {
    if (name == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder(name.getText().replace('.', '_'));
    for (int i = 1; i < builder.length() - 1; i++) {
      if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
        builder.insert(i++, '_');
      }
    }
    return getIdentifier(builder.toString(), name.isQuoted(), jdbcEnvironment);
  }

  /**
   * Get an identifier for the specified details. By default this method will return an identifier with the name adapted
   * based on the result of {@link #isCaseInsensitive(JdbcEnvironment)}
   *
   * @param name            the name of the identifier
   * @param quoted          if the identifier is quoted
   * @param jdbcEnvironment the JDBC environment
   * @return an identifier instance
   */
  protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment) {
    if (isCaseInsensitive(jdbcEnvironment)) {
      name = name.toLowerCase(Locale.ROOT);
    }
    return new Identifier(name, quoted);
  }

  /**
   * Specify whether the database is case sensitive.
   *
   * @param jdbcEnvironment the JDBC environment which can be used to determine case
   * @return true if the database is case insensitive sensitivity
   */
  protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
    return true;
  }

  private boolean isUnderscoreRequired(char before, char current, char after) {
    return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
  }
}
