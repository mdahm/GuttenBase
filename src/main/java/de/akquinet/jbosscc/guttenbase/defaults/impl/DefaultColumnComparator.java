package de.akquinet.jbosscc.guttenbase.defaults.impl;

import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;

import java.util.Comparator;

/**
 * By default order by natural order of column names.
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public final class DefaultColumnComparator implements Comparator<ColumnMetaData> {
  @Override
  public int compare(final ColumnMetaData c1, final ColumnMetaData c2) {
    return c1.compareTo(c2);
  }
}
