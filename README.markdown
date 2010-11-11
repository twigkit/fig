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

    Configs.load(new PropertiesLoader("confs"));
    Config servers = Configs.get("servers");

...or create configurations programmatically:

    Config servers = new Config("servers").set("host", "127.0.0.1").set("port", 8080);

Extend configuration sets with specifics (inheriting and overriding values):

    servers.extendWith(new Config("email").set("port", 25).set("protocol", "imap"));
    Config email = config.subset("email");

    email.extendWith(new Config("secure-mail").set("port", 465).set("security", "ssl"));

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

To find a particular configuration use:

    Config secureMail = Configs.find("secure-mail");

To get a value from that set:

    String host = secureEmail.value("host").get();

Or get all values:

    List<Value> values = secureEmail.values();


[TwigKit]: http://www.twigkit.com/
[Maven]: http://maven.apache.org/