#!/bin/bash

set -e

echo Starting the application java-reference-implementation
echo Connecting to database server "$DB_HOST":"$DB_PORT" and database "$DB_DATABASE"

# use variable to create truststore file
if [ -n "$KAFKA_SSL_TRUSTSTORE_BASE64" ]; then
  echo "Saving truststore content to file '${KAFKA_SSL_TRUSTSTORE_LOCATION:-/app/resources/truststore/truststore.jks}'"
  echo "$KAFKA_SSL_TRUSTSTORE_BASE64" | base64 --decode > "${KAFKA_SSL_TRUSTSTORE_LOCATION:-/app/resources/truststore/truststore.jks}"
fi

java -XX:MaxRAMPercentage="${JVM_MAX_RAM_PERCENTAGE:-50}" \
     -XX:+UseG1GC \
     -XshowSettings:vm \
     -javaagent:/app/lib/main/com.newrelic.agent.java.newrelic-agent-8.2.0.jar \
     -Dnewrelic.config.file=/app/resources/newrelic.yml \
     -Dssl="${PGSQL_SSL_ENABLED:-true}" \
     -Djavax.net.ssl.trustStore=mystore \
     -Djavax.net.ssl.trustStorePassword=changeit \
     -Dwfm.messaging.sslProtocol="${RMQ_SSL_ENABLED:-true}" \
     $* -jar /app/quarkus-run.jar
