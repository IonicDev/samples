export MAVEN_OPTS="-Djava.library.path=$IONIC_SDK_LIBDIR"
mvn -X clean compile exec:java
