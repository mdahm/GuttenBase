package de.akquinet.jbosscc.guttenbase.hints.impl;

import de.akquinet.jbosscc.guttenbase.hints.TableRowDataFilterHint;
import de.akquinet.jbosscc.guttenbase.mapping.TableRowDataFilter;

/**
 * By default, accept all data
 */
public class DefaultTableRowDataFilterHint extends TableRowDataFilterHint {
  @Override
  public TableRowDataFilter getValue() {
    return (sourceValues, targetValues) -> true;
  }
}
