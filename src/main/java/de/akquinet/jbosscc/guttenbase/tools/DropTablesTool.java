package de.akquinet.jbosscc.guttenbase.tools;

import de.akquinet.jbosscc.guttenbase.connector.ConnectorInfo;
import de.akquinet.jbosscc.guttenbase.connector.DatabaseType;
import de.akquinet.jbosscc.guttenbase.hints.TableOrderHint;
import de.akquinet.jbosscc.guttenbase.mapping.TableMapper;
import de.akquinet.jbosscc.guttenbase.meta.ForeignKeyMetaData;
import de.akquinet.jbosscc.guttenbase.meta.IndexMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import de.akquinet.jbosscc.guttenbase.utils.Util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Will drop tables in given schema. USE WITH CARE!
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 * Hint is used by {@link TableOrderHint} to determine order of tables
 */
public class DropTablesTool {
  private static final String DEFAULT_INDEX_DROP = "DROP INDEX @@EXISTS@@ @@FULL_INDEX_NAME@@;";
  private static final String POSTGRES_CONSTRAINT_DROP = "ALTER TABLE @@FULL_TABLE_NAME@@ DROP CONSTRAINT @@EXISTS@@ @@INDEX_NAME@@;";

  final ConnectorRepository _connectorRepository;
  private final String _dropTablesSuffix;

  /**
   * @param connectorRepository connector repository
   * @param dropTablesSuffix    Some DBs support cascading DROPs, e.g. Postgresql DROP TABLE .... CASCADE;
   */
  public DropTablesTool(final ConnectorRepository connectorRepository, final String dropTablesSuffix) {
    assert connectorRepository != null : "connectorRepository != null";
    assert dropTablesSuffix != null : "dropTablesSuffix != null";

    _dropTablesSuffix = dropTablesSuffix;
    _connectorRepository = connectorRepository;
  }

  public DropTablesTool(final ConnectorRepository connectorRepository) {
    this(connectorRepository, "");
  }

  public List<String> createDropForeignKeyStatements(final String connectorId) {
    final List<TableMetaData> tableMetaData = new TableOrderTool().getOrderedTables(
        TableOrderHint.getSortedTables(_connectorRepository, connectorId), false);
    final TableMapper tableMapper = _connectorRepository.getConnectorHint(connectorId, TableMapper.class).getValue();
    final List<String> statements = new ArrayList<>();
    final ConnectorInfo connectionInfo = _connectorRepository.getConnectionInfo(connectorId);
    final String constraintClause = getConstraintClause(connectionInfo);

    for (final TableMetaData table : tableMetaData) {
      for (final ForeignKeyMetaData foreignKey : table.getImportedForeignKeys()) {
        statements.add("ALTER TABLE " + tableMapper.fullyQualifiedTableName(table, table.getDatabaseMetaData())
            + " DROP" + constraintClause + foreignKey.getForeignKeyName() + ";");
      }
    }

    return statements;
  }

  private String getConstraintClause(final ConnectorInfo connectionInfo) {
    switch (connectionInfo.getDatabaseType()) {
      case MARIADB:
      case MYSQL:
        return " FOREIGN KEY ";
      case POSTGRESQL:
        return " CONSTRAINT IF EXISTS ";
      default:
        return " CONSTRAINT ";
    }
  }

  public List<String> createDropIndexesStatements(final String connectorId) {
    final List<TableMetaData> tableMetaData = new TableOrderTool().getOrderedTables(
        TableOrderHint.getSortedTables(_connectorRepository, connectorId), false);
    final List<String> statements = new ArrayList<>();
    final ConnectorInfo connectionInfo = _connectorRepository.getConnectionInfo(connectorId);
    final TableMapper tableMapper = _connectorRepository.getConnectorHint(connectorId, TableMapper.class).getValue();
    final boolean posgresql = connectionInfo.getDatabaseType() == DatabaseType.POSTGRESQL;

    for (final TableMetaData table : tableMetaData) {
      final String schemaPrefix = table.getDatabaseMetaData().getSchemaPrefix();
      final String fullTableName = tableMapper.fullyQualifiedTableName(table, table.getDatabaseMetaData());

      for (final IndexMetaData index : table.getIndexes()) {
        if (!index.isPrimaryKeyIndex()) {
          final String fullIndexName = schemaPrefix + index.getIndexName();
          final String existsClause = posgresql ? "IF EXISTS" : "";
          final String constraintClause = (posgresql && index.isUnique()) ? POSTGRES_CONSTRAINT_DROP : DEFAULT_INDEX_DROP;

          statements.add(constraintClause
              .replaceAll("@@EXISTS@@", existsClause)
              .replaceAll("@@INDEX_NAME@@", index.getIndexName())
              .replaceAll("@@FULL_INDEX_NAME@@", fullIndexName)
              .replaceAll("@@FULL_TABLE_NAME@@", fullTableName));
        }
      }
    }

    return statements;
  }

  public List<String> createDropTableStatements(final String connectorId) {
    return createTableStatements(connectorId, "DROP TABLE", _dropTablesSuffix);
  }

  public List<String> createDeleteTableStatements(final String connectorId) {
    return createTableStatements(connectorId, "DELETE FROM", "");
  }

  public void dropTables(final String targetId) throws SQLException {
    new ScriptExecutorTool(_connectorRepository).executeScript(targetId, true, true, createDropTableStatements(targetId));
  }

  public void clearTables(final String targetId) throws SQLException {
    new ScriptExecutorTool(_connectorRepository).executeScript(targetId, true, true, createDeleteTableStatements(targetId));
  }

  public void dropIndexes(final String targetId) throws SQLException {
    new ScriptExecutorTool(_connectorRepository).executeScript(targetId, true, false, createDropIndexesStatements(targetId));
  }

  public void dropForeignKeys(final String targetId) throws SQLException {
    new ScriptExecutorTool(_connectorRepository).executeScript(targetId, true, false, createDropForeignKeyStatements(targetId));
  }

  private List<String> createTableStatements(final String connectorId, final String clausePrefix, final String clauseSuffix) {
    final List<TableMetaData> tableMetaData = new TableOrderTool().getOrderedTables(
        TableOrderHint.getSortedTables(_connectorRepository, connectorId), false);
    final List<String> statements = new ArrayList<>();
    final TableMapper tableMapper = _connectorRepository.getConnectorHint(connectorId, TableMapper.class).getValue();
    final String suffix = "".equals(Util.trim(clauseSuffix)) ? "" : " " + clauseSuffix;

    for (final TableMetaData table : tableMetaData) {
      statements.add(clausePrefix + " " + tableMapper.fullyQualifiedTableName(table, table.getDatabaseMetaData()) + suffix + ";");
    }

    return statements;
  }
}
