The project aims to implement a set of services to enable management of persistent URLs across an enterprise. This includes:
  * URL shortener, ala bit.ly. Short links will be available both as global and in a user context. Statistics, metadata and history will be maintined for each link.
  * Redirect rules, ala Apache HTTPD mod\_redirect
  * Batch import of static redirects
  * Link monitoring, for detecting dead links

The application is designed as a classical web application on Spring MVC and JPA. It should run on any servlet platform and on the common SQL databases. Our primary platform during development is Apache Tomcat, Hibernate and PostgreSQL.

The short link service uses the bit.ly API and implements the shorten, expand and lookup methods. That means it will be usable from e.g. Twitter clients which supports custom short link services. It also provides a simple web GUI, usable from a bookmarklet.

Configuration of redirect rules, static redirects and link monitoring is managed through a web interface, available as a JSR-286 portlet (but also possible to repackage as a standalone web application).