# Etapa 1: Compilar el proyecto con Maven y Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copiamos primero el pom.xml para validar la estructura del proyecto
COPY pom.xml .

# Copiamos el resto de las carpetas (como src)
COPY src ./src

# Ejecutamos el empaquetado
RUN mvn clean package -DskipTests

# Etapa 2: Copiar el WAR generado al servidor Tomcat 10
FROM tomcat:10.1-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]