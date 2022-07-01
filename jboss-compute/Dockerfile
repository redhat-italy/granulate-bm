FROM registry.access.redhat.com/ubi8/openjdk-11 as build

ADD app .
RUN mvn package

FROM registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8 

COPY --from=build --chown=jboss:0 /home/jboss/target/benchmark-1.0.0.war $JBOSS_HOME/standalone/deployments/
