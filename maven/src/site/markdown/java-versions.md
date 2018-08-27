## Java versions
MPXJ is targeted to run against Java 1.6, and should therefore work with all versions after that.

Up to version 10 Java shipped with the Java EE libraries MPXJ depends on to read XML files (JAXB), however in
Java 10 these are deprecated, although they are present in the Java install, they are not
enabled by default. Some notes on the issue can be found [here](https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j/46455026).
One approach is to enable the JAXB libraries by adding the following to the Java command line when launching your application: 

```
--add-modules java.xml.bind
```

In Java 11 the JAXB libraries are not shipped with Java at all, so you will need to supply you own copies of the libraries.
The JAXB libraries can be found  [here](https://javaee.github.io/jaxb-v2/).  