package de.akquinet.jbosscc.guttenbase.tools;

import de.akquinet.jbosscc.guttenbase.configuration.TestDerbyConnectionInfo;
import de.akquinet.jbosscc.guttenbase.hints.TableOrderHint;
import de.akquinet.jbosscc.guttenbase.hints.impl.HighPriorityTableOrderHint;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class HighPriorityTableOrder_Test extends AbstractGuttenBaseTest {
  private static final String DB = "db";
  private List<TableMetaData> _tableMetaData;

  @Before
  public void setup() throws SQLException {
    _connectorRepository.addConnectionInfo(DB, new TestDerbyConnectionInfo());
    _connectorRepository.addConnectorHint(DB, new HighPriorityTableOrderHint(List.of("FOO_USER_ROLES")));

    new ScriptExecutorTool(_connectorRepository).executeFileScript(DB, "/ddl/tables.sql");

    _tableMetaData = TableOrderHint.getSortedTables(_connectorRepository, DB);
  }

  @Test
  public void testOrder() {
    final var tables = _tableMetaData.stream().map(t -> t.getTableName().toUpperCase(Locale.ROOT)).collect(Collectors.toList());

    assertEquals(0, tables.indexOf("FOO_USER_ROLES"));
    assertEquals(1, tables.indexOf("FOO_COMPANY"));
  }
}
