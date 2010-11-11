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

Load from Java Properties files

    Configs.load(new PropertiesLoader("confs"));


Create configurations programmatically

    new Config("conf").set("host", "127.0.0.1").set("port", 8080);


Get values

    config.get("host");


[TwigKit]: http://www.twigkit.com/
[Maven]: http://maven.apache.org/