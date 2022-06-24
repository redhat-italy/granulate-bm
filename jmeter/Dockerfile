FROM registry.access.redhat.com/ubi8/ubi:latest as build

RUN dnf install wget -y \
        && wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.5.tgz -P /opt \
        && cd /opt \
        && tar -xvf apache-jmeter-5.5.tgz \
        && rm apache-jmeter-5.5.tgz

FROM registry.access.redhat.com/ubi8/openjdk-17

VOLUME /inputs

COPY --from=build --chown=jboss:0 /opt/apache-jmeter-5.5 .

CMD sh /home/jboss/bin/jmeter.sh -n -t /inputs/simulation.jmx