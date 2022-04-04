package de.akquinet.jbosscc.guttenbase.hints.impl;

import de.akquinet.jbosscc.guttenbase.hints.ColumnMapperHint;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper;

/**
 * By default return column with same name ignoring case.
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class DefaultColumnMapperHint extends ColumnMapperHint {
  private final ColumnMapper _columnMapper;

  public DefaultColumnMapperHint(final ColumnMapper columnMapper) {
    _columnMapper = columnMapper;
  }

  @Override
  public ColumnMapper getValue() {
    return _columnMapper;
  }
}
