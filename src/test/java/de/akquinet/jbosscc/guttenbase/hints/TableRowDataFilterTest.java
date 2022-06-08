package de.akquinet.jbosscc.guttenbase.hints;

import de.akquinet.jbosscc.guttenbase.configuration.TestDerbyConnectionInfo;
import de.akquinet.jbosscc.guttenbase.configuration.TestH2ConnectionInfo;
import de.akquinet.jbosscc.guttenbase.hints.impl.DisableMultipleNumberOfRowsPerBatchHint;
import de.akquinet.jbosscc.guttenbase.mapping.TableRowDataFilter;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.tools.AbstractGuttenBaseTest;
import de.akquinet.jbosscc.guttenbase.tools.DefaultTableCopyTool;
import de.akquinet.jbosscc.guttenbase.tools.ScriptExecutorTool;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * Filter data rows
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class TableRowDataFilterTest extends AbstractGuttenBaseTest {
  public static final String SOURCE = "SOURCE";
  public static final String TARGET = "TARGET";

  @Before
  public void setup() throws Exception {
    _connectorRepository.addConnectionInfo(SOURCE, new TestDerbyConnectionInfo());
    _connectorRepository.addConnectionInfo(TARGET, new TestH2ConnectionInfo());
    new ScriptExecutorTool(_connectorRepository).executeFileScript(SOURCE, "/ddl/tables.sql");
    new ScriptExecutorTool(_connectorRepository).executeFileScript(TARGET, "/ddl/tables.sql");
    new ScriptExecutorTool(_connectorRepository).executeFileScript(SOURCE, false, false, "/data/test-data.sql");

    _connectorRepository.addConnectorHint(TARGET, new TableRowDataFilterHint() {
      @Override
      public TableRowDataFilter getValue() {
        return (sourceValues, targetValues) -> {
          final TableMetaData tableMetaData = sourceValues.keySet().stream().findAny().orElseThrow().getTableMetaData();

          if (tableMetaData.getTableName().equalsIgnoreCase("FOO_COMPANY")) {
            final Object name = sourceValues.get(tableMetaData.getColumnMetaData("NAME"));

            return !name.equals("Company 2");
          }

          return true;
        };
      }
    });
  }

  @Test(expected = SQLException.class)
  public void testExpectExeptionIfNumberOfRowsPerBatchAllowsMultipleValuesClauses() throws Exception {
    new DefaultTableCopyTool(_connectorRepository).copyTables(SOURCE, TARGET);
  }

  @Test
  public void testOmitData() throws Exception {
    _connectorRepository.addConnectorHint(TARGET, new DisableMultipleNumberOfRowsPerBatchHint());

    new DefaultTableCopyTool(_connectorRepository).copyTables(SOURCE, TARGET);

    final TableMetaData sourceTable = _connectorRepository.getDatabaseMetaData(SOURCE).getTableMetaData("FOO_COMPANY");
    final TableMetaData targetTable = _connectorRepository.getDatabaseMetaData(TARGET).getTableMetaData("FOO_COMPANY");

    assertEquals(4, sourceTable.getTotalRowCount());
    assertEquals(3, targetTable.getTotalRowCount());
  }
}
