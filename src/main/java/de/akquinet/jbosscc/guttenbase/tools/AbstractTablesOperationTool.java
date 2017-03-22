package de.akquinet.jbosscc.guttenbase.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.akquinet.jbosscc.guttenbase.hints.TableNameMapperHint;
import de.akquinet.jbosscc.guttenbase.hints.TableOrderHint;
import de.akquinet.jbosscc.guttenbase.mapping.TableNameMapper;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;

/**
 * Will execute given SQL scriptlet on all tables or single table of given connector. The table name can be referenced with @TABLE@
 * place holder.
 * <p>
 * &copy; 2012-2020 akquinet tech@spree
 * </p>
 *
 * @gb.UsesHint {@link TableNameMapperHint}
 * @author M. Dahm
 */
public abstract class AbstractTablesOperationTool
{
  public static final String TABLE_PLACEHOLDER = "@TABLE@";

  protected final ConnectorRepository _connectorRepository;
  protected final ScriptExecutorTool _scriptExecutor;
  private final String _template;

  public AbstractTablesOperationTool(final ConnectorRepository connectorRepository, final String template)
  {
    assert connectorRepository != null : "connectorRepository != null";
    assert template != null : "template != null";
    _connectorRepository = connectorRepository;
    _template = template;
    _scriptExecutor = new ScriptExecutorTool(connectorRepository);
  }

  public void executeOnAllTables(final String connectorId, final boolean updateSchema, final boolean prepareTargetConnection)
      throws SQLException
  {
    final List<TableMetaData> tables = TableOrderHint.getSortedTables(_connectorRepository, connectorId);
    final List<String> statements = new ArrayList<String>();

    for (final TableMetaData tableMetaData : tables)
    {
      if (isApplicableOnTable(tableMetaData))
      {
        statements.add(createSql(connectorId, tableMetaData));
      }
    }

    _scriptExecutor.executeScript(connectorId, updateSchema, prepareTargetConnection, statements);
  }

  public void executeOnTable(final String connectorId, final boolean updateSchema, final boolean prepareTargetConnection,
      final TableMetaData tableMetaData) throws SQLException
  {
    if (isApplicableOnTable(tableMetaData))
    {
      final String sql = createSql(connectorId, tableMetaData);
      _scriptExecutor.executeScript(connectorId, updateSchema, prepareTargetConnection, sql);
    }
  }

  private String createSql(final String connectorId, final TableMetaData tableMetaData) throws SQLException
  {
    final TableNameMapper tableNameMapper = _connectorRepository.getConnectorHint(connectorId, TableNameMapper.class).getValue();
    final String tableName = tableNameMapper.mapTableName(tableMetaData);

    return _template.replaceAll(TABLE_PLACEHOLDER, tableName);
  }

  /**
   * Override this method for specific tests
   */
  public boolean isApplicableOnTable(final TableMetaData tableMetaData)
  {
    return true;
  }
}
