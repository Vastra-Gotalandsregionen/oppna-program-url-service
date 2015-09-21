
<td id="wikicontent" class="psdescription">
  <p>
    The project aims to implement a set of services to enable management of persistent URLs across an enterprise. This includes: 
  </p>
  <ul>
    <li>
      URL shortener, ala bit.ly. Short links will be available both as global and in a user context. Statistics, metadata and history will be maintined for each link. 
    </li>
    <li>
      Redirect rules, ala Apache HTTPD mod_redirect 
    </li>
    <li>
      Batch import of static redirects 
    </li>
    <li>
      Link monitoring, for detecting dead links 
    </li>
  </ul>
  <p>
  </p>
  <p>
    The application is designed as a classical web application on Spring MVC and JPA. It should run on any servlet platform and on the common SQL databases. Our primary platform during development is Apache Tomcat, Hibernate and PostgreSQL.  
  </p>
  <p>
    The short link service uses the bit.ly API and implements the shorten, expand and lookup methods. That means it will be usable from e.g. Twitter clients which supports custom short link services. It also provides a simple web GUI, usable from a bookmarklet. 
  </p>
  <p>
    Configuration of redirect rules, static redirects and link monitoring is managed through a web interface, available as a JSR-286 portlet (but also possible to repackage as a standalone web application).  
  </p>
</td>
