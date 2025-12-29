package com.iyzico.challenge.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConfigurationTest {

    @Test
    void dataSource_shouldCreateHikariDataSource_withExpectedConfig() {
        // given
        DatabaseConfiguration cfg = new DatabaseConfiguration();

        ReflectionTestUtils.setField(cfg, "driverClassName", "org.h2.Driver");
        ReflectionTestUtils.setField(cfg, "url", "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        ReflectionTestUtils.setField(cfg, "username", "sa");
        ReflectionTestUtils.setField(cfg, "password", "");

        // when
        DataSource dataSource = cfg.dataSource();

        // then
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);

        HikariDataSource hikari = (HikariDataSource) dataSource;
        assertThat(hikari.getDriverClassName()).isEqualTo("org.h2.Driver");
        assertThat(hikari.getJdbcUrl()).contains("jdbc:h2:mem:testdb");
        assertThat(hikari.getUsername()).isEqualTo("sa");
        assertThat(hikari.getPoolName()).isEqualTo("my db pool");
        assertThat(hikari.getMaximumPoolSize()).isEqualTo(2);

        hikari.close();
    }
}
