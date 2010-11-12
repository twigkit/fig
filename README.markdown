Fig - Java Configuration Framework
========

**Simple framework for managing hierarchical configurations.**

Installation
------------

Add the fig-*{version}*.jar to your project's classpath. If you use [Maven][Maven] to manage your dependencies use add the following to your POM.xml

    <dependency>
        <groupId>twigkit</groupId>
        <artifactId>fig</artifactId>
        <version>{version}</version>
    </dependency>


Usage
-----

### Creating or getting configurations ###

Given the following Java Properties files:

    confs/
        servers.conf
         host = 127.0.0.1
         port = 8080

        servers_email.conf
         port = 25
         protocol = imap

        servers_email_secure-mail.conf
         port = 465
         security = ssl

Load configurations using:

    Fig.load( new PropertiesLoader("confs") ); // Supports multiple Loaders as varargs
    Config servers = Fig.get("servers");

...or create configurations programmatically:

    Config servers = Fig.create("servers").set("host", "127.0.0.1").set("port", 8080);

Extend configuration sets with specifics (inheriting and overriding values):

    servers.extend_with( new Config("email").set("port", 25).set("protocol", "imap") ); // Using Config constructor
    Config email = config.subset("email");

    email.extend_with( create("secure-mail").set("port", 465).set("security", "ssl") ); // Using static import of Fig

This would create configuration sets like:

    SERVERS
      |-- host = 127.0.0.1
      |-- port = 8080
      |
      +-- EMAIL
            |-- host = 127.0.0.1
            |-- port = 25
            |-- protocol = imap
            |
            +-- SECURE-MAIL
                  |-- host = 127.0.0.1
                  |-- port = 465
                  |-- protocol = imap
                  |-- security = ssl


### Using configurations ###

To find a particular configuration use:

    Config secureMail = Fig.find("secure-mail");

To get a value from that set:

    String host = secureEmail.value("host").get();

Or get all values:

    List<Value> values = secureEmail.values();


### Configuring objects ###

#### Configurable interface ####

text here.

#### Annotations ####

text here.

#### Configuring instances ####

text here.


[TwigKit]: http://www.twigkit.com/
[Maven]: http://maven.apache.org/