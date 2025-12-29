package com.iyzico.challenge.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LazyDataSourceConfigTest {

    private final LazyDataSourceConfig sut = new LazyDataSourceConfig();

    @Test
    void shouldReturnSameBean_whenBeanNameIsNotDataSource() {
        Object anyBean = new Object();

        Object result = sut.postProcessAfterInitialization(anyBean, "someOtherBean");

        assertThat(result).isSameAs(anyBean);
    }

    @Test
    void shouldReturnSameBean_whenBeanNameIsDataSourceButBeanIsNotADataSource() {
        Object notADataSource = new Object();

        Object result = sut.postProcessAfterInitialization(notADataSource, "dataSource");

        assertThat(result).isSameAs(notADataSource);
    }

}
