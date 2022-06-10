package de.akquinet.jbosscc.guttenbase.tools;

import de.akquinet.jbosscc.guttenbase.configuration.TargetDatabaseConfiguration;
import de.akquinet.jbosscc.guttenbase.connector.Connector;
import de.akquinet.jbosscc.guttenbase.hints.TableOrderHint;
import de.akquinet.jbosscc.guttenbase.meta.IndexMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import de.akquinet.jbosscc.guttenbase.sql.SQLLexer;
import de.akquinet.jbosscc.guttenbase.utils.ScriptExecutorProgressIndicator;
import de.akquinet.jbosscc.guttenbase.utils.Util;

import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;

/**
 * Execute given SQL script or single statements separated by given delimiter. Delimiter is ';' by default.
 *
 * @author M. Dahm
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 */
public class ScriptExecutorTool {
  public static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
  private final ConnectorRepository _connectorRepository;
  private final char _delimiter;
  private ScriptExecutorProgressIndicator _progressIndicator;
  private final String _encoding;

  public ScriptExecutorTool(final ConnectorRepository connectorRepository, final char delimiter, final String encoding) {
    assert connectorRepository != null : "connectorRepository != null";
    assert encoding != null : "encoding != null";

    _connectorRepository = connectorRepository;
    _delimiter = delimiter;
    _encoding = encoding;
  }

  public ScriptExecutorTool(final ConnectorRepository connectorRepository, final String encoding) {
    this(connectorRepository, ';', encoding);
  }

  public ScriptExecutorTool(final ConnectorRepository connectorRepository, final char delimiter) {
    this(connectorRepository, delimiter, DEFAULT_ENCODING);
  }

  public ScriptExecutorTool(final ConnectorRepository connectorRepository) {
    this(connectorRepository, ';');
  }

  /**
   * Read SQL from file somewhere on class path. Each statement (not line!) must end with a ';'
   */
  public void executeFileScript(final String connectorId, final String resourceName) throws SQLException {
    executeFileScript(connectorId, true, true, resourceName);
  }

  /**
   * Read SQL from file somewhere on class path. Each statement (not line!) must end with a ';'
   */
  public void executeFileScript(final String connectorId, final boolean updateSchema, final boolean prepareTargetConnection,
                                final String resourceName) throws SQLException {
    executeScript(connectorId, updateSchema, prepareTargetConnection, Util.readLinesFromFile(resourceName, _encoding));
  }

  /**
   * Execute given lines of SQL. Each statement (not line!) must end with a ';'
   */
  public void executeScript(final String connectorId, final String... lines) throws SQLException {
    executeScript(connectorId, true, true, lines);
  }

  /**
   * Execute given lines of SQL. Each statement (not line!) must end with a ';'
   */
  public void executeScript(final String connectorId, final boolean updateSchema, final boolean prepareTargetConnection,
                            final String... lines) throws SQLException {
    executeScript(connectorId, updateSchema, prepareTargetConnection, Arrays.asList(lines));
  }

  /**
   * Execute given lines of SQL. Each statement (not line!) must with a ';'
   */
  public void executeScript(final String connectorId, final List<String> lines) throws SQLException {
    executeScript(connectorId, true, true, lines);
  }

  /**
   * Execute given lines of SQL. Each statement (not line!) must end with a ';'
   *
   * @param connectorId
   * @param scriptUpdatesSchema     The script alters the schema, scheme information needs to be reloaded
   * @param prepareTargetConnection the target connection is initialized using the appropriate {@link TargetDatabaseConfiguration}
   * @param lines                   SQL statements ending with ';'
   * @throws SQLException
   */
  @SuppressWarnings("JavaDoc")
  public void executeScript(final String connectorId, final boolean scriptUpdatesSchema, final boolean prepareTargetConnection,
                            final List<String> lines) throws SQLException {
    if (lines.isEmpty()) {
      return;
    }

    final TargetDatabaseConfiguration targetDatabaseConfiguration = _connectorRepository.getTargetDatabaseConfiguration(connectorId);
    final List<String> sqlStatements = new SQLLexer(lines, _delimiter).parse();

    _progressIndicator = _connectorRepository.getConnectorHint(connectorId, ScriptExecutorProgressIndicator.class).getValue();
    _progressIndicator.initializeIndicator();

    try (final Connector connector = _connectorRepository.createConnector(connectorId);
         final Connection connection = connector.openConnection();
         final Statement statement = connection.createStatement()) {
      if (prepareTargetConnection) {
        targetDatabaseConfiguration.initializeTargetConnection(connection, connectorId);
      }

      if (connection.getAutoCommit()) {
        connection.setAutoCommit(false);
      }

      _progressIndicator.startProcess(sqlStatements.size());

      for (final String sql : sqlStatements) {
        _progressIndicator.startExecution();
        executeSQL(statement, sql);
        _progressIndicator.endExecution(1);
        _progressIndicator.endProcess();
      }

      if (!connection.getAutoCommit() && targetDatabaseConfiguration.isMayCommit()) {
        connection.commit();
      }

      if (scriptUpdatesSchema) {
        _connectorRepository.refreshDatabaseMetaData(connectorId);
      }

      if (prepareTargetConnection) {
        targetDatabaseConfiguration.finalizeTargetConnection(connection, connectorId);
      }

      if (scriptUpdatesSchema) {
        _connectorRepository.refreshDatabaseMetaData(connectorId);
      }
    }

    _progressIndicator.finalizeIndicator();
  }

  /**
   * Execute query (i.e. SELECT...) and return the result set as a list of Maps where the key is the column name and the value the
   * respective data.
   *
   * @throws SQLException
   */
  @SuppressWarnings("JavaDoc")
  public List<Map<String, Object>> executeQuery(final String connectorId, final String sql) throws SQLException {
    try (final Connector connector = _connectorRepository.createConnector(connectorId)) {
      final Connection connection = connector.openConnection();
      return executeQuery(connection, sql);
    }
  }

  /**
   * Execute query (i.e. SELECT...) and execute the given command on each row of data
   *
   * @throws SQLException
   */
  public void executeQuery(final String connectorId, final String sql, final Command action) throws SQLException {
    try (final Connector connector = _connectorRepository.createConnector(connectorId)) {
      final Connection connection = connector.openConnection();
      executeQuery(connection, sql, action);
    }
  }

  public interface Command {
    /**
     * Called before first execution
     *
     * @param connection
     * @throws SQLException
     */
    default void initialize(final Connection connection) throws SQLException {
    }

    /**
     * Executed for each row of data
     *
     * @param connection
     * @param data
     * @throws SQLException
     */
    void execute(final Connection connection, final Map<String, Object> data) throws SQLException;
  }

  /**
   * Execute query (i.e. SELECT...) and return the result set as a list of Maps where the key is the column name and the value the
   * respective data.
   *
   * @throws SQLException
   */
  public List<Map<String, Object>> executeQuery(final Connection connection, final String sql) throws SQLException {
    final List<Map<String, Object>> result = new ArrayList<>();

    try (final Statement statement = connection.createStatement(); final ResultSet resultSet = statement.executeQuery(sql)) {
      readMapFromResultSet(connection, resultSet, (tool, data) -> result.add(data));
    }

    return result;
  }

  /**
   * Execute query (i.e. SELECT...) and execute the given command on each row of data
   *
   * @throws SQLException
   */
  public void executeQuery(final Connection connection, final String sql, final Command action) throws SQLException {
    try (final Statement statement = connection.createStatement();
         final ResultSet resultSet = statement.executeQuery(sql)) {
      readMapFromResultSet(connection, resultSet, action);
    }
  }

  private void readMapFromResultSet(final Connection connection, final ResultSet resultSet, final Command action) throws SQLException {
    final ResultSetMetaData metaData = resultSet.getMetaData();

    action.initialize(connection);

    while (resultSet.next()) {
      final int columnCount = metaData.getColumnCount();
      final Map<String, Object> map = new HashMap<>();

      for (int i = 1; i <= columnCount; i++) {
        final String columnName = metaData.getColumnName(i);
        final Object value = resultSet.getObject(i);

        map.put(columnName, value);
      }

      action.execute(connection, map);
    }
  }

  private void executeSQL(final Statement statement, final String sql) throws SQLException {
    _progressIndicator.info("Executing: " + sql);

    final boolean result = statement.execute(sql);

    if (result) {
      try (final ResultSet resultSet = statement.getResultSet()) {
        readMapFromResultSet(statement.getConnection(), resultSet, (tool, map) -> _progressIndicator.info("Query result: " + map));
      }
    } else {
      final int updateCount = statement.getUpdateCount();
      _progressIndicator.info("Update count: " + updateCount);
    }
  }

  public void dropIndexes(final DropTablesTool dropTablesTool, final String connectorId, final boolean updateSchema) throws SQLException {
    final List<TableMetaData> tableMetaData = TableOrderHint.getSortedTables(dropTablesTool._connectorRepository, connectorId);
    final List<String> statements = new ArrayList<>();

    for (final TableMetaData table : tableMetaData) {
      for (final IndexMetaData index : table.getIndexes()) {
        statements.add("DROP INDEX " + index.getIndexName() + ";");
      }
    }

    executeScript(connectorId, updateSchema, false, statements);
  }
}
