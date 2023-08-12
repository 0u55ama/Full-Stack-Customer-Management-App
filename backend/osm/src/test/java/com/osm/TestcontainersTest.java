package com.osm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest Never use this annotation for integration test it just use a lot of beans for no reason, and it makes the operation slow
public class TestcontainersTest extends AbstractTestcontainers {

    @Test
    void canStartPostgresDB() {
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        assertThat(postgreSQLContainer.isCreated()).isTrue();
    }
}
