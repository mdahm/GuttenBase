package de.akquinet.jbosscc.guttenbase.configuration.impl;

import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;

/**
 * Implementation for MARIADB data base.
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class MariaDbSourceDatabaseConfiguration extends DefaultSourceDatabaseConfiguration {
  public MariaDbSourceDatabaseConfiguration(final ConnectorRepository connectorRepository) {
    super(connectorRepository);
  }
}
