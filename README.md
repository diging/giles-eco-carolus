# Carolus (Giles Ecosystem)

Carolus is an additional component for the Giles Ecosystem. It uses the species name recognition library linnaeus (http://linnaeus.sourceforge.net/) to extract species names from submitted texts. For each text file stored in the Giles Ecosystem (or more precisely in Nepomuk), Carolus will extract species and generate a csv file to be stored in Nepomuk as well. 

Carolus is an optional component that can be added to the Ecosystem if species names extraction is desired. 

## Installation Notes

When building Carolus using Maven, note that the Linnaeus library is not available in a Maven repository. Hence, the library has to be installed by hand first:

```
mvn install:install-file -Dfile=linnaeus/bin/linnaeus-2.0.jar -DgroupId=bergmanlab -DartifactId=linnaeus -Dversion=2.0 -Dpackaging=jar -DgeneratePom=true
```

Once Linnaeus is install, you can package Carolus like so:
```
clean package -Dlog.level=info -Dadmin.password=adminPassword -Dkafka.hosts=your-kafka-host:port -Dgeco.requests.version=0.7 -Dcarolus.base.url=https://your-carolus-host/carolus -Dcarolus.baseDir=/path/to/folder/carolus/has/access/to -Dcarolus.tmp.folder=aFolderInBaseDir
```
