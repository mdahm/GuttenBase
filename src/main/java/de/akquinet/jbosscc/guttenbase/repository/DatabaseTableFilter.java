package de.akquinet.jbosscc.guttenbase.repository;

import de.akquinet.jbosscc.guttenbase.meta.DatabaseMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.impl.DatabaseMetaDataInspectorTool;

/**
 * Regard which tables when @see {@link DatabaseMetaDataInspectorTool} is inquiring the database for tables. The methods refer to
 * the parameters passed to JDBC data base meta data methods such as
 * {@linkplain DatabaseMetaData#getTableMetaData(String)}
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
@SuppressWarnings("RedundantThrows")
public interface DatabaseTableFilter {
  String getCatalog(final DatabaseMetaData databaseMetaData);

  String getSchema(final DatabaseMetaData databaseMetaData);

  String getSchemaPattern(final DatabaseMetaData databaseMetaData);

  String getTableNamePattern(final DatabaseMetaData databaseMetaData);

  String[] getTableTypes(final DatabaseMetaData databaseMetaData);

  /**
   * Additionally you may add checks to the resulting meta data object
   *
   * @return true if the table should be added the database meta data
   */
  boolean accept(TableMetaData table);
}
