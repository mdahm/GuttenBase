package de.akquinet.jbosscc.guttenbase.mapping;

import de.akquinet.jbosscc.guttenbase.connector.DatabaseType;
import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;

import java.sql.Types;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static de.akquinet.jbosscc.guttenbase.connector.DatabaseType.*;

/**
 * Default uses same data type as source
 *
 * <p>
 * &copy; 2012-2034 akquinet tech@spree
 * </p>
 */
@SuppressWarnings("ALL")
public class DefaultColumnTypeMapper implements ColumnTypeMapper {
  private final Map<DatabaseType, Map<DatabaseType, Map<String, ColumnDefinition>>> _mappings = new HashMap<>();

  public DefaultColumnTypeMapper() {
    createPostgresToMysqlMapping();
    createOracleToPostgresMapping();
    createMysqlToPostresMapping();
    createMysqlToOracle();
    createOracleToMysql();
    createMssqlToOracle();
    createMssqlToMysql();
    createMssqlToPostgres();
    createH2ToDerbyMapping();
    createDerbyToH2Mapping();
    createDB2ToMysqlMapping();
    createDB2ToPostgresMapping();
    createMysqltoDB2Mapping();
    createPostgrestoDB2Mapping();
    createMysqlToMssqlMapping();
    createPostgresToMssqlMapping();
    createDB2ToMssqlMapping();
    createPostgresToOracleMapping();
  }

  /**
   * @return target database type including precision and optional not null constraint clause
   */
  @Override
  public String mapColumnType(final ColumnMetaData columnMetaData,
                              final DatabaseType sourceDatabaseType, final DatabaseType targetDatabaseType) {
    final String columnType = columnMetaData.getColumnTypeName().toUpperCase();
    final ColumnDefinition columnDefinition = getMapping(sourceDatabaseType, targetDatabaseType, columnType);
    final String notNull = columnMetaData.isNullable() ? "" : " NOT NULL";

    if (columnDefinition != null) {
      final String precision = createPrecisionClause(columnMetaData, columnDefinition.getPrecision());

      return columnDefinition.getType() + precision + notNull;
    } else {
      return getColumnType(columnMetaData, "") + notNull;
    }
  }

  /**
   * Override this method if you just want to change the way column types are mapped
   *
   * @return target database type including precision
   */
  protected String getColumnType(final ColumnMetaData columnMetaData, final String optionalPrecision) {
    final String precision = createPrecisionClause(columnMetaData, optionalPrecision);
    final String defaultColumnType = columnMetaData.getColumnTypeName();

    return defaultColumnType + precision;
  }

  protected String createPrecisionClause(final ColumnMetaData columnMetaData, final String optionalPrecision) {
    switch (columnMetaData.getColumnType()) {
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.VARBINARY:
        if (columnMetaData.getColumnTypeName().toUpperCase(Locale.ROOT).contains("TEXT")) {
          return ""; // TEXT does not support precision
        } else {
          final int precision = columnMetaData.getPrecision();

          return precision > 0 ? "(" + precision + ")" : "";
        }
      default:
        return optionalPrecision;
    }
  }

  public ColumnDefinition getMapping(final DatabaseType sourceDB, final DatabaseType targetDB, final String columnType) {
    final Map<DatabaseType, Map<String, ColumnDefinition>> databaseMatrix = _mappings.get(sourceDB);

    if (databaseMatrix != null) {
      final Map<String, ColumnDefinition> databaseMapping = databaseMatrix.get(targetDB);

      if (databaseMapping != null) {
        return databaseMapping.get(columnType);
      }
    }

    return null;
  }

  public final DefaultColumnTypeMapper addMapping(final DatabaseType sourceDB, final DatabaseType targetDB, final String sourceTypeName, String targetTypeName) {
    return addMapping(sourceDB, targetDB, sourceTypeName, targetTypeName, "");
  }

  public final DefaultColumnTypeMapper addMapping(final DatabaseType sourceDB, final DatabaseType targetDB, final String sourceTypeName, String targetTypeName, String precision) {
    addMappingInternal(sourceDB, targetDB, sourceTypeName, targetTypeName, precision);

    if (sourceDB == MYSQL) {
      addMappingInternal(MARIADB, targetDB, sourceTypeName, targetTypeName, precision);
    } else if (targetDB == MYSQL) {
      addMappingInternal(sourceDB, MARIADB, sourceTypeName, targetTypeName, precision);
    }

    return this;
  }

  private void addMappingInternal(DatabaseType sourceDB, DatabaseType targetDB, String sourceTypeName, String targetTypeName, String precision) {
    final Map<DatabaseType, Map<String, ColumnDefinition>> databaseMatrix = _mappings.computeIfAbsent(sourceDB, k -> new HashMap<>());
    final Map<String, ColumnDefinition> mapping = databaseMatrix.computeIfAbsent(targetDB, k -> new HashMap<>());

    mapping.put(sourceTypeName, new ColumnDefinition(targetTypeName, precision));
  }

  private void createPostgresToMysqlMapping() {
    addMapping(POSTGRESQL, MYSQL, "INT8", "TINYINT");
    addMapping(POSTGRESQL, MYSQL, "ARRAY", "LONGTEXT");
    addMapping(POSTGRESQL, MYSQL, "BIGSERIAL", "BIGINT");
    addMapping(POSTGRESQL, MYSQL, "BOOLEAN", "TINYINT(1)");
    addMapping(POSTGRESQL, MYSQL, "BOX", "POLYGON");
    addMapping(POSTGRESQL, MYSQL, "BYTEA", "LONGBLOB");
    addMapping(POSTGRESQL, MYSQL, "CIDR", "VARCHAR(43)");
    addMapping(POSTGRESQL, MYSQL, "CIRCLE", "POLYGON");
    addMapping(POSTGRESQL, MYSQL, "DOUBLE PRECISION", "DOUBLE");
    addMapping(POSTGRESQL, MYSQL, "INET", "VARCHAR", "(43)");
    addMapping(POSTGRESQL, MYSQL, "INTERVAL", "TIME");
    addMapping(POSTGRESQL, MYSQL, "JSON", "LONGTEXT");
    addMapping(POSTGRESQL, MYSQL, "LINE", "LINESTRING");
    addMapping(POSTGRESQL, MYSQL, "LSEG", "LINESTRING");
    addMapping(POSTGRESQL, MYSQL, "MACADDR", "VARCHAR", "(17)");
    addMapping(POSTGRESQL, MYSQL, "MONEY", "DECIMAL", "(19,2)");
    addMapping(POSTGRESQL, MYSQL, "NATIONAL CHARACTER VARYING", "VARCHAR");
    addMapping(POSTGRESQL, MYSQL, "NATIONAL CHARACTER", "CHAR");
    addMapping(POSTGRESQL, MYSQL, "NUMERIC", "DECIMAL");
    addMapping(POSTGRESQL, MYSQL, "PATH", "LINESTRING");
    addMapping(POSTGRESQL, MYSQL, "REAL", "FLOAT");
    addMapping(POSTGRESQL, MYSQL, "SERIAL", "INT");
    addMapping(POSTGRESQL, MYSQL, "SMALLSERIAL", "SMALLINT");
    addMapping(POSTGRESQL, MYSQL, "TEXT", "LONGTEXT");
    addMapping(POSTGRESQL, MYSQL, "TIMESTAMP", "DATETIME");
    addMapping(POSTGRESQL, MYSQL, "TSQUERY", "LONGTEXT");
    addMapping(POSTGRESQL, MYSQL, "TSVECTOR", "LONGTEXT");
    addMapping(POSTGRESQL, MYSQL, "TXID_SNAPSHOT", "VARCHAR");
    addMapping(POSTGRESQL, MYSQL, "UUID", "VARCHAR", "(36)");
    addMapping(POSTGRESQL, MYSQL, "XML", "LONGTEXT");
    addMapping(POSTGRESQL, MYSQL, "OID", "BLOB");
  }

  private void createOracleToPostgresMapping() {
    addMapping(ORACLE, POSTGRESQL, "NUMBER", "NUMERIC");
    addMapping(ORACLE, POSTGRESQL, "VARCHAR2", "VARCHAR");
    addMapping(ORACLE, POSTGRESQL, "RAW", "BYTEA");
  }

  private void createPostgresToOracleMapping() {
    addMapping(POSTGRESQL, ORACLE, "BPCHAR", "CHAR", "(1)");
    addMapping(POSTGRESQL, ORACLE, "BPCHAR", "CHAR");
    addMapping(POSTGRESQL, ORACLE, "INT8", "NUMBER", "(3)");
    addMapping(POSTGRESQL, ORACLE, "INT4", "NUMBER", "(5)");
    addMapping(POSTGRESQL, ORACLE, "INT16", "NUMBER", "(10)");
    addMapping(POSTGRESQL, ORACLE, "BIGSERIAL", "NUMBER", "(19)");
    addMapping(POSTGRESQL, ORACLE, "OID", "BLOB");
  }

  private void createH2ToDerbyMapping() {
    addMapping(DatabaseType.H2DB, DatabaseType.DERBY, "LONGTEXT", "CLOB");
    addMapping(DatabaseType.H2DB, DatabaseType.DERBY, "LONGBLOB", "BLOB");
  }

  private void createDerbyToH2Mapping() {
    addMapping(DatabaseType.DERBY, DatabaseType.H2DB, "LONGTEXT", "CLOB");
    addMapping(DatabaseType.DERBY, DatabaseType.H2DB, "LONGBLOB", "BLOB");
  }

  private void createMysqltoDB2Mapping() {
    addMapping(MYSQL, DatabaseType.DB2, "LONGTEXT", "VARCHAR", "(4000)"); //CHAR(254)
    addMapping(MYSQL, DatabaseType.DB2, "LONGBLOB", "BLOB"); //CLOB (2G)
    addMapping(MYSQL, DatabaseType.DB2, "DECIMAL", "DECIMAL", "(16)"); //CLOB (2G)
  }

  private void createPostgrestoDB2Mapping() {

    addMapping(POSTGRESQL, DatabaseType.DB2, "TEXT", "VARCHAR", "(4000)"); //CHAR(254)
    addMapping(POSTGRESQL, DatabaseType.DB2, "BYTEA", "BLOB"); //CLOB (2G) LONGBLOB
    addMapping(POSTGRESQL, DatabaseType.DB2, "NUMERIC", "DECIMAL", "(16)");
    addMapping(POSTGRESQL, DatabaseType.DB2, "INT(2)", "DECIMAL", "(16)");
    addMapping(POSTGRESQL, DatabaseType.DB2, "INT(4)", "DECIMAL", "(16)");

  }

  private void createDB2ToMysqlMapping() {
    addMapping(DatabaseType.DB2, MYSQL, "CHAR", "CHAR");
    addMapping(DatabaseType.DB2, MYSQL, "CLOB", "LONGBLOB");
    addMapping(DatabaseType.DB2, MYSQL, "INTEGER", "INT", "(11)");


  }

  private void createDB2ToPostgresMapping() {
    addMapping(DatabaseType.DB2, POSTGRESQL, "BLOB", "BYTEA");
  }

  private void createDB2ToMssqlMapping() {
    addMapping(DatabaseType.DB2, MSSQL, "BLOB", "VARBINARY");
  }

  private void createMysqlToPostresMapping() {
    addMapping(MYSQL, POSTGRESQL, "BIGINT AUTO_INCREMENT", "BIGSERIAL");
    addMapping(MYSQL, POSTGRESQL, "BIGINT UNSIGNED", "NUMERIC", "(20)");
    addMapping(MYSQL, POSTGRESQL, "INTEGER UNSIGNED", "BIGINT");
    addMapping(MYSQL, POSTGRESQL, "INT UNSIGNED", "BIGINT");
    addMapping(MYSQL, POSTGRESQL, "MEDIUMINT UNSIGNED", "INTEGER");
    addMapping(MYSQL, POSTGRESQL, "BINARY", "BYTEA");
    addMapping(MYSQL, POSTGRESQL, "BLOB", "BYTEA");
    addMapping(MYSQL, POSTGRESQL, "DATETIME", "TIMESTAMP");
    addMapping(MYSQL, POSTGRESQL, "DOUBLE", "DOUBLE PRECISION");
    addMapping(MYSQL, POSTGRESQL, "FLOAT", "REAL");
    addMapping(MYSQL, POSTGRESQL, "INTEGER AUTO_INCREMENT", "SERIAL");
    addMapping(MYSQL, POSTGRESQL, "LONGBLOB", "BYTEA");
    addMapping(MYSQL, POSTGRESQL, "MEDIUMINT", "INTEGER");
    addMapping(MYSQL, POSTGRESQL, "MEDIUMBLOB", "BYTEA");
    addMapping(MYSQL, POSTGRESQL, "TINYBLOB", "BYTEA");
    addMapping(MYSQL, POSTGRESQL, "LONGTEXT", "TEXT");
    addMapping(MYSQL, POSTGRESQL, "MEDIUMTEXT", "TEXT");
    addMapping(MYSQL, POSTGRESQL, "SMALLINT AUTO_INCREMENT", "SMALLSERIAL");
    addMapping(MYSQL, POSTGRESQL, "SMALLINT UNSIGNED", "INTEGER");
    addMapping(MYSQL, POSTGRESQL, "TINYINT", "NUMERIC(1)");
    addMapping(MYSQL, POSTGRESQL, "TINYINT AUTO_INCREMENT", "SMALLSERIAL");
    addMapping(MYSQL, POSTGRESQL, "TINYINT UNSIGNED", "SMALLSERIAL");
    addMapping(MYSQL, POSTGRESQL, "TINYTEXT", "TEXT");
    addMapping(MYSQL, POSTGRESQL, "VARBINARY", "BYTEA");
  }

  private void createMysqlToOracle() {
    addMapping(MYSQL, ORACLE, "BIT", "RAW");
    addMapping(MYSQL, ORACLE, "BIGINT", "NUMBER", "(19, 0)");
    addMapping(MYSQL, ORACLE, "DATETIME", "DATE");
    addMapping(MYSQL, ORACLE, "DECIMAL", "FLOAT", "(24)");
    addMapping(MYSQL, ORACLE, "DOUBLE", "FLOAT", "(24)");
    addMapping(MYSQL, ORACLE, "DOUBLE PRECISION", "FLOAT", "(24)");
    addMapping(MYSQL, ORACLE, "ENUM", "VARCHAR2");
    addMapping(MYSQL, ORACLE, "INT", "NUMBER", "(10, 0)");
    addMapping(MYSQL, ORACLE, "INTEGER", "NUMBER", "(10, 0)");
    addMapping(MYSQL, ORACLE, "LONGBLOB", "BLOB");
    addMapping(MYSQL, ORACLE, "LONGTEXT", "CLOB");
    addMapping(MYSQL, ORACLE, "MEDIUMBLOB", "BLOB");
    addMapping(MYSQL, ORACLE, "MEDIUMINT", "NUMBER", "(7, 0)");
    addMapping(MYSQL, ORACLE, "MEDIUMTEXT", "CLOB");
    addMapping(MYSQL, ORACLE, "NUMERIC", "NUMBER");
    addMapping(MYSQL, ORACLE, "REAL", "FLOAT", "(24)");
    addMapping(MYSQL, ORACLE, "SET", "VARCHAR2");
    addMapping(MYSQL, ORACLE, "SMALLINT", "NUMBER", "(5, 0)");
    addMapping(MYSQL, ORACLE, "TEXT", "VARCHAR2");
    addMapping(MYSQL, ORACLE, "TIME", "DATE");
    addMapping(MYSQL, ORACLE, "TIMESTAMP", "DATE");
    addMapping(MYSQL, ORACLE, "TINYBLOB", "RAW");
    addMapping(MYSQL, ORACLE, "TINYINT", "NUMBER", "(3, 0)");
    addMapping(MYSQL, ORACLE, "TINYTEXT", "VARCHAR2");
    addMapping(MYSQL, ORACLE, "YEAR", "NUMBER");
    addMapping(MYSQL, ORACLE, "VARBINARY", "BYTEA");
  }

  private void createOracleToMysql() {
    addMapping(ORACLE, MYSQL, "RAW", "BIT");                  //TINYBLOB
    addMapping(ORACLE, MYSQL, "NUMBER(19, 0)", "BIGINT");
    addMapping(ORACLE, MYSQL, "DATE", "DATETIME");
    addMapping(ORACLE, MYSQL, "FLOAT (24)", "DECIMAL");       // DOUBLE, DOUBLE PRECISION, REAL
    addMapping(ORACLE, MYSQL, "VARCHAR2", "VARCHAR");
    addMapping(ORACLE, MYSQL, "NUMBER(10, 0)", "INT");        // INTEGER
    addMapping(ORACLE, MYSQL, "BLOB", "LONGBLOB");            // MEDIUMBLOB
    addMapping(ORACLE, MYSQL, "CLOB", "MEDIUMTEXT");
    addMapping(ORACLE, MYSQL, "NUMBER(7, 0)", "MEDIUMINT");
    addMapping(ORACLE, MYSQL, "NUMBER", "NUMERIC");           //YEAR
    addMapping(ORACLE, MYSQL, "NUMBER(5, 0)", "SMALLINT");
    addMapping(ORACLE, MYSQL, "BYTEA", "VARBINARY");
  }

  private void createMssqlToOracle() {
    addMapping(MSSQL, ORACLE, "BIGINT", "NUMBER", "(19)");
    addMapping(MSSQL, ORACLE, "BINARY", "RAW");
    addMapping(MSSQL, ORACLE, "BIT", "NUMBER", "(1)");
    addMapping(MSSQL, ORACLE, "DATETIME", "DATE");
    addMapping(MSSQL, ORACLE, "DECIMAL", "NUMBER", "(10)");
    addMapping(MSSQL, ORACLE, "FLOAT", "FLOAT", "(49)");
    addMapping(MSSQL, ORACLE, "IMAGE", "LONG RAW");
    addMapping(MSSQL, ORACLE, "INTEGER", "NUMBER", "(10");
    addMapping(MSSQL, ORACLE, "MONEY", "NUMBER", "(19,4)");
    addMapping(MSSQL, ORACLE, "NTEXT", "LONG");
    addMapping(MSSQL, ORACLE, "NVARCHAR", "NCHAR");
    addMapping(MSSQL, ORACLE, "NUMERIC", "NUMBER", "(10)");
    addMapping(MSSQL, ORACLE, "REAL", "FLOAT", "(23)");
    addMapping(MSSQL, ORACLE, "SMALL DATETIME", "DATE");
    addMapping(MSSQL, ORACLE, "SMALL MONEY", "NUMBER", "(10,4)");
    addMapping(MSSQL, ORACLE, "SMALLINT", "NUMBER", "(5)");
    addMapping(MSSQL, ORACLE, "TEXT", "LONG");
    addMapping(MSSQL, ORACLE, "TIMESTAMP", "RAW");
    addMapping(MSSQL, ORACLE, "TINYINT", "NUMBER", "(3)");
    addMapping(MSSQL, ORACLE, "UNIQUEIDENTIFIER", "CHAR", "(36)");
    addMapping(MSSQL, ORACLE, "VARBINARY", "RAW");
    addMapping(MSSQL, ORACLE, "VARCHAR", "VARCHAR2");
  }

  private void createMysqlToMssqlMapping() {
    addMapping(MYSQL, MSSQL, "LONGTEXT", "NVARCHAR", "(4000)");
    addMapping(MYSQL, MSSQL, "LONGBLOB", "VARBINARY");
    addMapping(MYSQL, MSSQL, "VARCHAR", "NVARCHAR", "(4000)");
    addMapping(MYSQL, MSSQL, "DECIMAL", "DECIMAL", "(38)");
  }

  private void createPostgresToMssqlMapping() {
    addMapping(POSTGRESQL, MSSQL, "TEXT", "NVARCHAR", "(4000)");
    addMapping(POSTGRESQL, MSSQL, "BYTEA", "BINARY");
    addMapping(POSTGRESQL, MSSQL, "INT4", "INT");
    addMapping(POSTGRESQL, MSSQL, "INT2", "INT");
  }

  private void createMssqlToMysql() {
    addMapping(MSSQL, MYSQL, "NVARCHAR", "VARCHAR", "(255)");
    addMapping(MSSQL, MYSQL, "VARBINARY", "BLOB");
  }

  private void createMssqlToPostgres() {
    addMapping(MSSQL, POSTGRESQL, "NVARCHAR", "TEXT");
    addMapping(MSSQL, POSTGRESQL, "VARBINARY", "BYTEA");
  }

  private static class ColumnDefinition {
    private final String type;
    private final String precision;

    public ColumnDefinition(final String type, final String precision) {
      this.type = type;
      this.precision = precision;
    }

    public String getType() {
      return type;
    }

    public String getPrecision() {
      return precision;
    }
  }
}
