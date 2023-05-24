package de.akquinet.jbosscc.guttenbase.meta.impl;

import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;
import de.akquinet.jbosscc.guttenbase.meta.ForeignKeyMetaData;
import de.akquinet.jbosscc.guttenbase.meta.InternalForeignKeyMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Information about a foreign key between table columns.
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
@SuppressWarnings("com.haulmont.jpb.EqualsDoesntCheckParameterClass")
public class ForeignKeyMetaDataImpl implements InternalForeignKeyMetaData {
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(ForeignKeyMetaDataImpl.class);

  private final String _foreignKeyName;
  private final Set<ColumnMetaData> _referencingColumns = new LinkedHashSet<>();
  private final Set<ColumnMetaData> _referencedColumns = new LinkedHashSet<>();
  private final TableMetaData _tableMetaData;

  public ForeignKeyMetaDataImpl(final TableMetaData tableMetaData, final String foreignKeyName,
                                final ColumnMetaData referencingColumn,
                                final ColumnMetaData referencedColumn) {
    assert tableMetaData != null : "tableMetaData != null";
    assert foreignKeyName != null : "foreignKeyName != null";
    assert referencingColumn != null : "referencingColumn != null";
    assert referencedColumn != null : "referencedColumn != null";

    _tableMetaData = tableMetaData;
    _foreignKeyName = foreignKeyName;
    _referencingColumns.add(referencingColumn);
    _referencedColumns.add(referencedColumn);
  }

  @Override
  public TableMetaData getTableMetaData()
  {
    return _tableMetaData;
  }

  @Override
  public String getForeignKeyName()
  {
    return _foreignKeyName;
  }

  @Override
  public List<ColumnMetaData> getReferencingColumns()
  {
    return new ArrayList<>(_referencingColumns);
  }

  @Override
  public TableMetaData getReferencingTableMetaData()
  {
    assert !getReferencingColumns().isEmpty() : "no referencing columns";
    return getReferencingColumns().get(0).getTableMetaData();
  }

  @Override
  public TableMetaData getReferencedTableMetaData()
  {
    assert !getReferencedColumns().isEmpty() : "no referenced columns";
    return getReferencedColumns().get(0).getTableMetaData();
  }

  @Override
  public List<ColumnMetaData> getReferencedColumns()
  {
    return new ArrayList<>(_referencedColumns);
  }

  @Override
  public void addColumnTuple(final ColumnMetaData referencingColumn, final ColumnMetaData referencedColumn) {
    if (!_referencingColumns.add(referencingColumn)) {
      LOG.warn("Referencing column already added: " + referencingColumn);
    }

    if (!_referencedColumns.add(referencedColumn)) {
      LOG.warn("Referenced column already added: " + referencedColumn);
    }
  }

  @Override
  public int compareTo(final ForeignKeyMetaData that)
  {
    return this.getForeignKeyName().toUpperCase().compareTo(that.getForeignKeyName().toUpperCase());
  }

  @Override
  public String toString()
  {
    return getTableMetaData() + ":" + getForeignKeyName() + ":" + getReferencingColumns() + "->" + getReferencedColumns();
  }

  @Override
  public int hashCode()
  {
    return getForeignKeyName().toUpperCase().hashCode();
  }

  @Override
  public boolean equals(final Object obj)
  {
    final ForeignKeyMetaData that = (ForeignKeyMetaData) obj;

    return this.getForeignKeyName().equalsIgnoreCase(that.getForeignKeyName());
  }
}
