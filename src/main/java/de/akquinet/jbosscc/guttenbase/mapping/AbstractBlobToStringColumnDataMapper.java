package de.akquinet.jbosscc.guttenbase.mapping;


import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Map BLOB object to String using given charset. Use in conjunction with {@link DefaultColumnDataMapperProvider}
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 *
 * @author M. Dahm
 */
public abstract class AbstractBlobToStringColumnDataMapper implements ColumnDataMapper {
  private final Charset _charset;

  public AbstractBlobToStringColumnDataMapper(final Charset charset) {
    _charset = charset;
  }

  public AbstractBlobToStringColumnDataMapper() {
    this(StandardCharsets.UTF_8);
  }

  @Override
  public Object map(ColumnMetaData sourceColumnMetaData, ColumnMetaData targetColumnMetaData, Object value) throws SQLException {
    final Blob blob = (Blob) value;

    try (final InputStream binaryStream = blob.getBinaryStream(); final InputStreamReader stream = new InputStreamReader(binaryStream, _charset)) {
      final int available = binaryStream.available();
      final char[] bytes = new char[available];
      final int read = stream.read(bytes);
      final int availableAfterRead = binaryStream.available();

      if (read < available && availableAfterRead > 0) {
        throw new SQLException("Bytes read " + read + " < available " + available);
      }

      return new String(bytes);
    } catch (final IOException e) {
      throw new SQLException("getBinaryStream", e);
    } finally {
      blob.free();
    }
  }
}
