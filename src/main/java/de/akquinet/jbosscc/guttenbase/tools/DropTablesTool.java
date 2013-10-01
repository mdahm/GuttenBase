package de.akquinet.jbosscc.guttenbase.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.akquinet.jbosscc.guttenbase.hints.TableOrderHint;
import de.akquinet.jbosscc.guttenbase.mapping.TableNameMapper;
import de.akquinet.jbosscc.guttenbase.meta.ForeignKeyMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;

/**
 * Will drop tables in given schema. USE WITH CARE!
 * <p>
 * &copy; 2012 akquinet tech@spree
 * </p>
 * 
 * @Uses-Hint {@link TableOrderHint} to determine order of tables
 * @author M. Dahm
 */
public class DropTablesTool
{
  private final ConnectorRepository _connectorRepository;
  private final ScriptExecutorTool _scriptExecutor;

  public DropTablesTool(final ConnectorRepository connectorRepository)
  {
    assert connectorRepository != null : "connectorRepository != null";
    _connectorRepository = connectorRepository;
    _scriptExecutor = new ScriptExecutorTool(connectorRepository);
  }

  /**
   * Drop all tables, foreign keys and indexes
   * 
   * @throws SQLException
   */
  public void dropTables(final String connectorId, final boolean updateSchema) throws SQLException
  {
    dropForeignKeys(connectorId, updateSchema);

  }

  public void dropForeignKeys(final String connectorId, final boolean updateSchema) throws SQLException
  {
    final List<TableMetaData> tableMetaData = TableOrderHint.getSortedTables(_connectorRepository, connectorId);
    final TableNameMapper tableNameMapper = _connectorRepository.getConnectorHint(connectorId, TableNameMapper.class).getValue();
    final List<String> statements = new ArrayList<String>();

    for (final TableMetaData table : tableMetaData)
    {
      for (final ForeignKeyMetaData foreignKey : table.getImportedForeignKeys())
      {
        statements.add("ALTER TABLE " + tableNameMapper.mapTableName(table)
            + " DROP CONSTRAINT "
            + foreignKey.getForeignKeyName()
            + ";");
      }
    }

    _scriptExecutor.executeScript(connectorId, updateSchema, false, statements);
  }
}
