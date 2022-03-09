package de.akquinet.jbosscc.guttenbase.meta;

/**
 * Extension for internal access.
 *
 * <p>&copy; 2012-2020 akquinet tech@spree</p>
 *
 * @author M. Dahm
 */
public interface InternalIndexMetaData extends IndexMetaData {
  void addColumn(ColumnMetaData columnMetaData);
}
