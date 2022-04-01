package de.akquinet.jbosscc.guttenbase.configuration.impl;

import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation for MARIADB data base.
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
@SuppressWarnings("JavaDoc")
public class MariaDbTargetDatabaseConfiguration extends DefaultTargetDatabaseConfiguration {
  private final boolean _disableUniqueChecks;

  /**
   * @param connectorRepository
   * @param disableUniqueChecks disable unique checks, too. Not just foreign key constraints.
   */
  public MariaDbTargetDatabaseConfiguration(final ConnectorRepository connectorRepository, final boolean disableUniqueChecks) {
    super(connectorRepository);

    _disableUniqueChecks = disableUniqueChecks;
  }

  public MariaDbTargetDatabaseConfiguration(final ConnectorRepository connectorRepository) {
    this(connectorRepository, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeTargetConnection(final Connection connection, final String connectorId) throws SQLException {
    if (connection.getAutoCommit()) {
      connection.setAutoCommit(false);
    }

    setReferentialIntegrity(connection, false);

    if (_disableUniqueChecks) {
      setUniqueChecks(connection, false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void finalizeTargetConnection(final Connection connection, final String connectorId) throws SQLException {
    setReferentialIntegrity(connection, true);

    if (_disableUniqueChecks) {
      setUniqueChecks(connection, true);
    }
  }

  private void setReferentialIntegrity(final Connection connection, final boolean enable) throws SQLException {
    executeSQL(connection, "SET FOREIGN_KEY_CHECKS = " + (enable ? "1" : "0") + ";");
  }

  private void setUniqueChecks(final Connection connection, final boolean enable) throws SQLException {
    executeSQL(connection, "SET UNIQUE_CHECKS = " + (enable ? "1" : "0") + ";");
  }
}
