package de.akquinet.jbosscc.guttenbase.hints;

import de.akquinet.jbosscc.guttenbase.mapping.TableRowDataFilter;

/**
 * Filter rows by inspection of data
 *
 * <p>
 * &copy; 2022-2034 akquinet tech@spree
 * </p>
 */
public abstract class TableRowDataFilterHint implements ConnectorHint<TableRowDataFilter> {
  @Override
  public Class<TableRowDataFilter> getConnectorHintType() {
    return TableRowDataFilter.class;
  }
}
