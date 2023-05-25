package de.akquinet.jbosscc.guttenbase.tools;

import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;

import java.sql.PreparedStatement;

/**
 * How many rows will be inserted in single transaction? This is an important performance issue.
 * <p>
 * We support two ways to insert multiple rows in one batch: Either with the {@link PreparedStatement#addBatch()} method or with multiple
 * VALUES() clauses for an INSERT statement. The latter method is much faster in most cases, but not all databases support this, so the
 * value must be 1 then.
 * <p>
 * The value also must not be too high so data buffers are not exceeded, especially when the table contains BLOBs.
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 * @see MaxNumberOfDataItems
 */
@FunctionalInterface
public interface NumberOfRowsPerBatch {
  int getNumberOfRowsPerBatch(TableMetaData targetTableMetaData);

  /**
   * Use VALUES() clauses or {@link PreparedStatement#addBatch()} as discussed above
   */
  default boolean useMultipleValuesClauses(TableMetaData targetTableMetaData) {
    return true;
  }
}
