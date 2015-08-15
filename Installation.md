# Introduction #

The URL application can be installed on any Servlet engine. The web module will build a WAR file that can be deployed to the servlet engine, e.g. using file based deployment in Apache Tomcat.

In addition, you'll need a database compliant with Hibernate, e.g. PostgreSQL, MySQL or Oracle RDBMS. By default, the URL application will create the necessary tables and indexes using the Hibernate DDL mechanism. Note however that this might not be desired in a production environment.

To configure the application, you'll need to create a configuration file in ${user.home}/.urlapp/urlapp.properties. The file should contain the following:

```
database.driver=org.hsqldb.jdbcDriver
database.url=jdbc:hsqldb:mem:push
database.user=sa
database.password=
hibernate.database.dialect=org.hibernate.dialect.HSQLDialect

hibernate.database.showsql=false
hibernate.database.schema=create
```

In the example above, the HSQL JDBC driver is used. Replace this and the related properties to match your database setup.

Set hibernate.database.showsql to true to enable debug logging of SQL statements.

Set hibernate.database.schema to the appropriate value according to how you want to use Hibernates DDL support. See http://docs.jboss.org/hibernate/core/3.3/reference/en/html/session-configuration.html and the hibernate.hbm2ddl.auto property on what values can be used.