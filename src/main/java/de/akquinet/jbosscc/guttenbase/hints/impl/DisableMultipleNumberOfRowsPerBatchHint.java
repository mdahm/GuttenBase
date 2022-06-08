package de.akquinet.jbosscc.guttenbase.hints.impl;

import de.akquinet.jbosscc.guttenbase.hints.NumberOfRowsPerBatchHint;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.tools.NumberOfRowsPerBatch;

/**
 * Disable multiple value clause feature
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class DisableMultipleNumberOfRowsPerBatchHint extends NumberOfRowsPerBatchHint {
  @Override
  public NumberOfRowsPerBatch getValue() {
    return new NumberOfRowsPerBatch() {
      @Override
      public int getNumberOfRowsPerBatch(TableMetaData targetTableMetaData) {
        return 1;
      }

      @Override
      public boolean useMultipleValuesClauses(TableMetaData targetTableMetaData) {
        return false;
      }
    };
  }
}
