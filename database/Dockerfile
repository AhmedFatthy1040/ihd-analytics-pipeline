FROM postgres:17

# Set environment variables
ENV POSTGRES_USER=ihd_user
ENV POSTGRES_PASSWORD=ihd_password
ENV POSTGRES_DB=ihd_analytics
ENV PGDATA=/var/lib/postgresql/data/pgdata

# Create directory for initialization scripts
RUN mkdir -p /docker-entrypoint-initdb.d

# Copy initialization scripts
COPY ./init-scripts/ /docker-entrypoint-initdb.d/

# Add PostgreSQL config files for optimization
COPY ./config/postgresql.conf /tmp/postgresql.conf
COPY ./config/pg_hba.conf /tmp/pg_hba.conf

# Update configuration on startup
RUN echo "#!/bin/bash\n\
cp /tmp/postgresql.conf /var/lib/postgresql/data/pgdata/postgresql.conf\n\
cp /tmp/pg_hba.conf /var/lib/postgresql/data/pgdata/pg_hba.conf" > /docker-entrypoint-initdb.d/99-config-update.sh && \
chmod 755 /docker-entrypoint-initdb.d/99-config-update.sh

# Set up directories for backup
RUN mkdir -p /backups && \
    chown -R postgres:postgres /backups

# Expose the PostgreSQL port
EXPOSE 5432

# Set healthcheck
HEALTHCHECK --interval=30s --timeout=5s --retries=3 CMD pg_isready -U ihd_user -d ihd_analytics || exit 1
