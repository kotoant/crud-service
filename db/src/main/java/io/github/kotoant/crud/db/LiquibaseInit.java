package io.github.kotoant.crud.db;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

@SuppressWarnings("unused")
public class LiquibaseInit {
    public static void initFunction(Connection connection) throws LiquibaseException {
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        database.setDefaultSchemaName("public");
        new Liquibase("db/init.sql", new ClassLoaderResourceAccessor(), database)
                .update(new Contexts());

        database.setDefaultSchemaName("crud_app");
        new Liquibase("db/changelog/database_changelog.xml", new ClassLoaderResourceAccessor(), database)
                .update(new Contexts());
    }
}
