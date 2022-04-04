package de.akquinet.jbosscc.guttenbase.hints.impl;

import de.akquinet.jbosscc.guttenbase.mapping.TableOrderComparatorFactory;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Order by natural order of table names with some high priority exceptions ranked first
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class HighPriorityTableOrderHint extends DefaultTableOrderHint {
  private final List<String> _tableNames;

  public HighPriorityTableOrderHint(final List<String> tableNames) {
    assert tableNames != null : "assert tableNames != null";
    _tableNames = tableNames.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
  }

  @Override
  public TableOrderComparatorFactory getValue() {
    return () -> (t1, t2) -> {
      if (_tableNames.contains(t1.getTableName().toLowerCase(Locale.ROOT))) {
        return -1;
      } else if (_tableNames.contains(t2.getTableName().toLowerCase(Locale.ROOT))) {
        return 1;
      } else {
        return t1.compareTo(t2);
      }
    };
  }
}
