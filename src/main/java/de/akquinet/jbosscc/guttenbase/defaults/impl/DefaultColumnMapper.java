package de.akquinet.jbosscc.guttenbase.defaults.impl;

import de.akquinet.jbosscc.guttenbase.hints.CaseConversionMode;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper;
import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * By default, return column with same name ignoring case. You may however configure case and escaping the column names explicitely.
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public class DefaultColumnMapper implements ColumnMapper {
  private final CaseConversionMode _caseConversionMode;
  private final String _escapeCharacter;

  public DefaultColumnMapper(final CaseConversionMode caseConversionMode, final String escapeCharacter) {
    assert caseConversionMode != null : "caseConversionMode != null";
    assert escapeCharacter != null : "escapeCharacter != null";

    _escapeCharacter = escapeCharacter;
    _caseConversionMode = caseConversionMode;
  }

  public DefaultColumnMapper() {
    this(CaseConversionMode.NONE, "");
  }

  @Override
  public String mapColumnName(final ColumnMetaData columnMetaData, final TableMetaData targetTableMetaData) {
    return _escapeCharacter + _caseConversionMode.convert(columnMetaData.getColumnName()) + _escapeCharacter;
  }

  @Override
  public ColumnMapperResult map(final ColumnMetaData source, final TableMetaData targetTableMetaData) {
    final String columnName = source.getColumnName();
    final ColumnMetaData columnMetaData = targetTableMetaData.getColumnMetaData(columnName);
    final List<ColumnMetaData> result = columnMetaData != null ? new ArrayList<>(Collections.singletonList(columnMetaData))
        : new ArrayList<>();
    return new ColumnMapperResult(result);
  }
}
