FROM java:8u91-jre

COPY target/scala-2.11/Planner.jar /data/planner.jar

EXPOSE 8080


CMD ["java","-jar","/data/planner.jar"]
