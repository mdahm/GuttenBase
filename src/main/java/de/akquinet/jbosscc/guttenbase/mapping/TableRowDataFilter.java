package de.akquinet.jbosscc.guttenbase.mapping;

import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;

import java.util.Map;

/**
 * Filter rows by inspection of data
 *
 * <p>
 * &copy; 2022-2034 akquinet tech@spree
 * </p>
 */
@FunctionalInterface
public interface TableRowDataFilter {
  boolean accept(final Map<ColumnMetaData, Object> sourceValues, final Map<ColumnMetaData, Object> targetValues);
}
