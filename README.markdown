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

Load from Java Properties files:

    Configs.load(new PropertiesLoader("confs"));
    Config config = Configs.get("server-settings");

Create configurations programmatically:

    Config config = new Config("server-settings").set("host", "127.0.0.1").set("port", 8080);

Get values:

    config.value("host").get();

Extend configuration sets with specifics (inheriting and overriding values):

    config.subset(new Config("email-server").set("port", "25").set("protocol", "imap"));
    Config emailServer = config.subset("email-server");

    emailServer.subset(new Config("secure-email").set("port", 465").set("security", "ssh"));

This would create configuration sets like:

    SERVER-SETTINGS
      |-- host = 127.0.0.1
      |-- port = 8080
      |
      +-- EMAIL-SERVER
            |-- host = 127.0.0.1
            |-- port = 25
            |-- protocol = imap
            |
            +-- SECURE-EMAIL
                  |-- host = 127.0.0.1
                  |-- port = 465
                  |-- protocol = imap
                  |-- security = ssl


[TwigKit]: http://www.twigkit.com/
[Maven]: http://maven.apache.org/