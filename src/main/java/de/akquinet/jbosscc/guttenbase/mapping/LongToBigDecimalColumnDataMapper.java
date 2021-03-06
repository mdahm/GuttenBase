package de.akquinet.jbosscc.guttenbase.mapping;

import java.math.BigDecimal;
import java.sql.SQLException;

import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;

/**
 * Map Long/Bigint to BigDecimal
 * 
 * <p>
 * &copy; 2012-2020 akquinet tech@spree
 * </p>
 * 
 * @author M. Dahm
 */
public class LongToBigDecimalColumnDataMapper implements ColumnDataMapper {
	@Override
	public Object map(final ColumnMetaData sourceColumnMetaData, final ColumnMetaData targetColumnMetaData, final Object value)
			throws SQLException {
		return value == null ? null : new BigDecimal(value.toString());
	}

	@Override
	public boolean isApplicable(final ColumnMetaData sourceColumnMetaData, final ColumnMetaData targetColumnMetaData) throws SQLException {
		return true;
	}
}