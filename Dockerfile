# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

# Keep Maven wrapper and dependency resolution in a cacheable layer.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -ntp -DskipTests package

FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app --shell /usr/sbin/nologin spring \
    && mkdir -p /app/media \
    && chown -R spring:spring /app \
    && command -v setpriv

COPY --from=build --chown=spring:spring \
    /workspace/target/watyouface-0.0.1-SNAPSHOT.jar /app/app.jar

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 0755 /usr/local/bin/docker-entrypoint.sh

EXPOSE 8080

# Railway may override this value with SPRING_PROFILES_ACTIVE.
ENV SPRING_PROFILES_ACTIVE=prod

# Railway mounts persistent volumes as root-owned paths. The entrypoint runs
# only long enough to transfer ownership of /app/media, then setpriv drops to
# the unprivileged spring account before the Java process is started.
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["java", "-jar", "/app/app.jar"]
