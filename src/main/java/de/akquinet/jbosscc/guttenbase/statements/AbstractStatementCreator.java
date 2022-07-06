package de.akquinet.jbosscc.guttenbase.statements;

import de.akquinet.jbosscc.guttenbase.hints.ColumnMapperHint;
import de.akquinet.jbosscc.guttenbase.hints.ColumnOrderHint;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper.ColumnMapperResult;
import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains some helper methods for implementing classes.
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 * Hint is used by {@link ColumnMapperHint} to map column names
 * Hint is used by {@link ColumnOrderHint} to determine column order
 */
public abstract class AbstractStatementCreator {
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractStatementCreator.class);

  protected final ConnectorRepository _connectorRepository;
  protected final String _connectorId;
  protected final ColumnMapper _columnMapper;

  public AbstractStatementCreator(final ConnectorRepository connectorRepository, final String connectorId) {
    assert connectorRepository != null : "connectorRepository != null";
    assert connectorId != null : "connectorId != null";
    _connectorId = connectorId;
    _connectorRepository = connectorRepository;
    _columnMapper = _connectorRepository.getConnectorHint(connectorId, ColumnMapper.class).getValue();
  }

  protected String createColumnClause(final List<ColumnMetaData> columns) {
    final StringBuilder columnBuf = new StringBuilder();

    for (final ColumnMetaData columnMetaData : columns) {
      columnBuf.append(_columnMapper.mapColumnName(columnMetaData, columnMetaData.getTableMetaData())).append(", ");
    }

    columnBuf.setLength(columnBuf.length() - 2);

    return columnBuf.toString();
  }

  protected String createWhereClause(final TableMetaData tableMetaData) {
    return "";
  }

  /**
   * Get the list of target columns with appropriate mappings as defined by {@link ColumnMapperHint}
   */
  public List<ColumnMetaData> getMappedTargetColumns(final TableMetaData sourceTableMetaData,
                                                     final TableMetaData targetTableMetaData, final String sourceConnectorId) {
    // Use same order as in SELECT clause
    final List<ColumnMetaData> sourceColumns = ColumnOrderHint.getSortedColumns(_connectorRepository, sourceConnectorId,
        sourceTableMetaData);
    final List<ColumnMetaData> columns = new ArrayList<>();
    final ColumnMapper columnMapper = _connectorRepository.getConnectorHint(_connectorId, ColumnMapper.class).getValue();

    for (final ColumnMetaData sourceColumnMetaData : sourceColumns) {
      final ColumnMapperResult mapping = columnMapper.map(sourceColumnMetaData, targetTableMetaData);
      columns.addAll(mapping.getColumns());
    }

    return columns;
  }
}
