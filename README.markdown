Fig - The Easy Java Configuration Framework
========

**Create and manage hierarchical configurations with Java.**

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

#### From Properties files ####

Given the following files:

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

#### Creating configurations programmatically ####

    Config servers = Fig.create("servers").set("host", "127.0.0.1").set("port", 8080);

Extend configuration sets with specifics (inheriting and overriding values):

    servers.extend_with( new Config("email").set("port", 25).set("protocol", "imap") ); // Using Config constructor
    Config email = servers.subset("email");

    email.extend_with( create("secure-mail").set("port", 465).set("security", "ssl") ); // Using static import of Fig

This would create hierarchical configuration sets (which extend and override) such as:

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

To get a particular configuration you can either get it from the top level or down the hierarchy:

    Config server = Fig.get("servers");
        // or
    Config secure = Fig.get("servers", "email", "secure-mail");

To find a particular configuration use:

    Config secure = Fig.find("secure-mail");

To get a value from that set:

    String host = secure.value("host").get();

Or get all values:

    List<Value> values = secure.values();


### Configuring objects ###

#### Configurable interface ####

This is simple enough.

    public interface Configurable {

        public void configure(Config config);

    }

#### Annotations ####

Fig supports annotating classes to configure certain members or get a reference to a **Config**:

    public class EmailServer {

        @Configure
        private Config config;

        @Configure.Value( name = "host" )
        private String hostUrl;

        @Configure.Value
        private int port;

        ...
    }

In this example the *config* member would be given a reference to the **Config** object, the *hostUrl* member would be
given the value labelled *host* in the configuration file, and finally the *port* member would be provided with the integer
value labelled *port* in the configuration file.

#### Configuring instances ####

To configure objects annotated with **@Configure** or that implement **Configurable** simply:

    EmailServer emailserver = new EmailServer();

    Fig.with("secure-mail").configure(emailserver); // Pick a configuration from Fig
        // or
    Fig.with(secure).configure(emailserver); // Using an instance of a configuration

Configure returns an instance of the configured object so you can then use that:

    Fig.with("secure-mail").configure(emailserver).sendMail(...);


[TwigKit]: http://www.twigkit.com/
[Maven]: http://maven.apache.org/