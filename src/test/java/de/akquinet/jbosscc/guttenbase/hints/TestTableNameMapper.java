package de.akquinet.jbosscc.guttenbase.hints;

import de.akquinet.jbosscc.guttenbase.defaults.impl.DefaultTableMapper;
import de.akquinet.jbosscc.guttenbase.meta.DatabaseMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;

public class TestTableNameMapper extends DefaultTableMapper {
  @Override
  public String fullyQualifiedTableName(final TableMetaData source, final DatabaseMetaData targetDatabaseMetaData) {
    final String tableName = mapTableName(source, targetDatabaseMetaData);

    return "\"" + tableName + "\"";
  }
}
