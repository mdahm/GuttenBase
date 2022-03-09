package de.akquinet.jbosscc.guttenbase.tools.schema.customSchemaFromDerbyToH2;


import de.akquinet.jbosscc.guttenbase.configuration.TestDerbyConnectionInfo;
import de.akquinet.jbosscc.guttenbase.configuration.TestH2ConnectionInfo;
import de.akquinet.jbosscc.guttenbase.hints.*;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper;
import de.akquinet.jbosscc.guttenbase.mapping.TableMapper;
import de.akquinet.jbosscc.guttenbase.tools.AbstractGuttenBaseTest;
import de.akquinet.jbosscc.guttenbase.tools.ScriptExecutorTool;
import de.akquinet.jbosscc.guttenbase.tools.schema.CopySchemaTool;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateSchemaWithAllFiltersToolTest extends AbstractGuttenBaseTest {
  private static final String SOURCE_CONNECTOR_ID = "derby";
  private static final String TARGET_CONNECTOR_ID = "hsqldb";

  private final CopySchemaTool _objectUnderTest = new CopySchemaTool(_connectorRepository);

  @Before
  public void setup() throws Exception {
    _connectorRepository.addConnectionInfo(SOURCE_CONNECTOR_ID, new TestDerbyConnectionInfo());
    _connectorRepository.addConnectionInfo(TARGET_CONNECTOR_ID, new TestH2ConnectionInfo());
    new ScriptExecutorTool(_connectorRepository).executeFileScript(SOURCE_CONNECTOR_ID, "/ddl/derby/script-allshop-derby-mod.sql");

    _connectorRepository.addConnectorHint(TARGET_CONNECTOR_ID, new TableMapperHint() {
      @Override
      public TableMapper getValue() {
        return new TestTableRenameNameMapper()
            .addReplacement("OFFICES", "TAB_OFFICES")
            .addReplacement("ORDERS", "TAB_ORDERS");
      }
    });

    _connectorRepository.addConnectorHint(TARGET_CONNECTOR_ID, new ColumnMapperHint() {
      @Override
      public ColumnMapper getValue() {
        return new TestColumnRenameNameMapper()
            .addReplacement("OFFICECODE", "ID_OFFICECODE")
            .addReplacement("ORDERNUMBER", "ID_ORDERNUMBER")
            .addReplacement("PHONE", "ID_PHONE")
            .addReplacement("CITY", "ID_CITY");
      }
    });

//      //ConnectionHint for Mapping ColumnType
//      _connectorRepository.addConnectorHint(SOURCE_CONNECTOR_ID, new ColumnTypeMapperHint() {
//          @Override
//          public ColumnTypeMapper getValue() {
//              return new DefaultColumnTypeMapper(DatabaseType.DERBY, DatabaseType.H2DB);
//          }
//      });


  }


  @Test
  public void testScript() throws Exception {

    _objectUnderTest.copySchema(SOURCE_CONNECTOR_ID, TARGET_CONNECTOR_ID);

    assertEquals("After", "TAB_OFFICES", _connectorRepository.getDatabaseMetaData(TARGET_CONNECTOR_ID).
        getTableMetaData("TAB_OFFICES").getTableName());

    assertEquals("After", "TAB_ORDERS", _connectorRepository.getDatabaseMetaData(TARGET_CONNECTOR_ID).
        getTableMetaData("TAB_ORDERS").getTableName());

    assertEquals("After", "ID_CITY", _connectorRepository.getDatabaseMetaData(TARGET_CONNECTOR_ID)
        .getTableMetaData("CUSTOMERS").getColumnMetaData("ID_CITY").getColumnName());

    assertEquals("After", "ID_ORDERNUMBER", _connectorRepository.getDatabaseMetaData(TARGET_CONNECTOR_ID)
        .getTableMetaData("ORDERDETAILS").getColumnMetaData("ID_ORDERNUMBER").getColumnName());


    assertEquals("Before", 10, _connectorRepository.getDatabaseMetaData(SOURCE_CONNECTOR_ID).getTableMetaData().size());
    assertEquals("Before", 13, _connectorRepository.getDatabaseMetaData(SOURCE_CONNECTOR_ID).getTableMetaData("CUSTOMERS").getColumnCount());
    assertEquals("Before", 8, _connectorRepository.getDatabaseMetaData(SOURCE_CONNECTOR_ID).getTableMetaData("EMPLOYEES").getColumnCount());

    _connectorRepository.addConnectorHint(SOURCE_CONNECTOR_ID, new TestTableNameFilterHint());
    _connectorRepository.addConnectorHint(SOURCE_CONNECTOR_ID, new TestColumnNameFilterHint());
    _connectorRepository.refreshDatabaseMetaData(SOURCE_CONNECTOR_ID);

    assertEquals("After", 9, _connectorRepository.getDatabaseMetaData(SOURCE_CONNECTOR_ID).getTableMetaData().size());
    assertEquals("After", 10, _connectorRepository.getDatabaseMetaData(SOURCE_CONNECTOR_ID).getTableMetaData("CUSTOMERS").getColumnCount());
    assertEquals("Before", 5, _connectorRepository.getDatabaseMetaData(SOURCE_CONNECTOR_ID).getTableMetaData("EMPLOYEES").getColumnCount());

  }
}
