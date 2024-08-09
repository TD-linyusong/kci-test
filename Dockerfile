# RELEASE IMAGE
FROM hub.talkdeskapp.com/talkdesk/base/temurinjdk:jre-17-23.06.01-td

ENV VERSION=1.0

# change active user to root to create folders and copy resources
USER root

WORKDIR /app
COPY ./target/quarkus-app/ .
COPY ./src/main/resources/ ./resources
COPY entrypoint.sh .

# Create this folder to store the Kafka truststore file (created in entrypoint.sh)
RUN mkdir -p ./resources/truststore

# set application user as owner of the app directory
RUN chown -R tduser /app

# change active user back to the application user
USER tduser

ENTRYPOINT ["/app/entrypoint.sh"]
