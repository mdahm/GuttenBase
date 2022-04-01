package de.akquinet.jbosscc.guttenbase.defaults.impl;

import de.akquinet.jbosscc.guttenbase.meta.DatabaseMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.DatabaseTableFilter;
import de.akquinet.jbosscc.guttenbase.utils.Util;

import java.util.Locale;

public class DefaultDatabaseTableFilter implements DatabaseTableFilter {
  @Override
  public String getCatalog(final DatabaseMetaData databaseMetaData) {
    switch (databaseMetaData.getDatabaseType()) {
      case MARIADB:
      case MYSQL:
        return databaseMetaData.getSchema();
      default:
        return null;
    }
  }

  @Override
  public String getSchema(final DatabaseMetaData databaseMetaData) {
    return "".equals(Util.trim(databaseMetaData.getSchema())) ? null : databaseMetaData.getSchema();
  }

  @Override
  public String getSchemaPattern(final DatabaseMetaData databaseMetaData) {
    switch (databaseMetaData.getDatabaseType()) {
      case MARIADB:
      case MYSQL:
        return null;
      default:
        return getSchema(databaseMetaData);
    }
  }

  @Override
  public String getTableNamePattern(final DatabaseMetaData databaseMetaData) {
    return "%";
  }

  @Override
  public String[] getTableTypes(final DatabaseMetaData databaseMetaData) {
    return new String[]{"TABLE"};
  }

  @Override
  public boolean accept(final TableMetaData table) {
    switch (table.getDatabaseMetaData().getDatabaseType()) {
      case POSTGRESQL:
        return !table.getTableName().toLowerCase(Locale.ROOT).startsWith("sql_");
      default:
        return true;
    }
  }
}
