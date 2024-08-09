.PHONY: install install-quick dev-mode

MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
MAVEN_ARGS=-T 1.5C -Dorg.slf4j.simpleLogger.log.io.repaint.maven.tiles=error

install:
	MAVEN_OPTS=${MAVEN_OPTS} mvn \
		${MAVEN_ARGS} install

install-quick:
	MAVEN_OPTS=${MAVEN_OPTS} mvn \
		${MAVEN_ARGS} install -DskipTests

dev-mode:
	MAVEN_OPTS=${MAVEN_OPTS} mvn \
		${MAVEN_ARGS} compile quarkus:dev
