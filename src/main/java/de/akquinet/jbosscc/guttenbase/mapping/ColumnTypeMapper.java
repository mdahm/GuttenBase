package de.akquinet.jbosscc.guttenbase.mapping;

import de.akquinet.jbosscc.guttenbase.connector.DatabaseType;
import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;

/**
 * Often data types of columns are not compatible: Allow user to define specific mappings.
 */
@FunctionalInterface
public interface ColumnTypeMapper {
  /**
   * @return target database type including precision and optional not null constraint clause
   */
  String mapColumnType(final ColumnMetaData columnMetaData, final DatabaseType sourceDatabase, final DatabaseType targetDatabase);
}
