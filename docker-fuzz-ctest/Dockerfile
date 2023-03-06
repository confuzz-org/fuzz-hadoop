FROM ubuntu:20.04

LABEL Tony Wang<tony.wanghao@stu.pku.edu.cn>

ARG USER_HOME_DIR="/root"

# Setup Basic Tools
RUN apt-get update
RUN apt-get -y install openjdk-8-jdk
RUN apt-get -y install wget curl git
RUN apt-get -y install openjdk-11-jdk

# Setup Maven
ARG MAVEN_VERSION=3.8.6

RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz

RUN echo "Unziping maven" 
RUN mkdir /usr/share/maven
RUN tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /usr/share/maven --strip-components=1 

RUN echo "Cleaning and setting links" 
RUN rm -f apache-maven-${MAVEN_VERSION}-bin.tar.gz 
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

WORKDIR /root
# Clone Confuzz
RUN git clone https://github.com/xlab-uiuc/confuzz/
# Clone Meringue
RUN git clone https://github.com/neu-se/meringue/
#ARG CACHEBUST=1 
# Clone the forked hadoop repository
RUN git clone https://github.com/shuaiwang516/fuzz-hadoop

# use java11 for the install next
WORKDIR /root/meringue
RUN JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/ mvn install -DskipTests

WORKDIR /root/confuzz
RUN JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/ mvn install -DskipTests

WORKDIR /root/fuzz-hadoop
# RUN mvn install -DskipTests
RUN git checkout 2facf6724d5590c2dd7977fc6aad286990c3bccd
RUN mvn install -pl hadoop-common-project/hadoop-common,hadoop-hdfs-project/hadoop-hdfs -am -DskipTests

CMD [""]
