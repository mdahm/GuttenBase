package de.akquinet.jbosscc.guttenbase.hints;

import de.akquinet.jbosscc.guttenbase.configuration.TestH2ConnectionInfo;
import de.akquinet.jbosscc.guttenbase.tools.AbstractGuttenBaseTest;
import de.akquinet.jbosscc.guttenbase.tools.ScriptExecutorTool;
import de.akquinet.jbosscc.guttenbase.tools.ScriptExecutorTool.StatementCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Execute updates on schema
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class ScriptExecutorToolTest extends AbstractGuttenBaseTest {
  public static final String TARGET = "TARGET";

  private final ScriptExecutorTool _objectUnderTest = new ScriptExecutorTool(_connectorRepository);

  @Before
  public void setup() throws Exception {
    _connectorRepository.addConnectionInfo(TARGET, new TestH2ConnectionInfo());

    _objectUnderTest.executeFileScript(TARGET, "/ddl/tables.sql");
    _objectUnderTest.executeFileScript(TARGET, false, false, "/data/test-data.sql");
  }

  @Test
  public void testAction() throws Exception {
    final List<Object> nulls1 = _objectUnderTest.executeQuery(TARGET, "SELECT * FROM FOO_USER").stream()
        .map(data -> data.get("PERSONAL_NUMBER")).filter(Objects::isNull).collect(Collectors.toList());

    Assert.assertFalse(nulls1.isEmpty());

    _objectUnderTest.executeQuery(TARGET, "SELECT * FROM FOO_USER",
        new StatementCommand("UPDATE FOO_USER SET PERSONAL_NUMBER = ? WHERE ID = ?") {
          @Override
          public void execute(final Connection connection, final Map<String, Object> data) throws SQLException {
            if (data.get("PERSONAL_NUMBER") == null) {
              final Long id = (Long) data.get("ID");

              _statement.setInt(1, 4711);
              _statement.setLong(2, id);
              _statement.execute();
            }
          }
        });

    final List<Object> nulls2 = _objectUnderTest.executeQuery(TARGET, "SELECT * FROM FOO_USER").stream()
        .map(data -> data.get("PERSONAL_NUMBER")).filter(Objects::isNull).collect(Collectors.toList());

    Assert.assertTrue(nulls2.isEmpty());
  }
}
