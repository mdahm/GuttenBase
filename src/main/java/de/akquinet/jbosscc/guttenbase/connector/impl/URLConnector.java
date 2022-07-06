package de.akquinet.jbosscc.guttenbase.connector.impl;

import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection info via explicit URL and driver.
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class URLConnector extends AbstractURLConnector {
  protected static final Logger LOG = LoggerFactory.getLogger(URLConnector.class);

  public URLConnector(final ConnectorRepository connectorRepository, final String connectorId, final URLConnectorInfo urlConnectionInfo) {
    super(connectorRepository, connectorId, urlConnectionInfo);
  }
}