/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Playtika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.playtika.test.clickhouse;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static com.playtika.test.clickhouse.ClickHouseProperties.BEAN_NAME_EMBEDDED_CLICK_HOUSE;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = BaseEmbeddedClickHouseTest.TestConfiguration.class)
@ActiveProfiles("enabled")
public abstract class BaseEmbeddedClickHouseTest {

    @Autowired
    protected ConfigurableListableBeanFactory beanFactory;

    @Autowired
    protected ConfigurableEnvironment environment;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Test
    public void shouldConnectToClickHouse() throws Exception {
        assertThat(jdbcTemplate.queryForObject("select version()", String.class)).contains("20.5");
    }

    @Test
    public void propertiesAreAvailable() {
        assertThat(environment.getProperty("embedded.clickhouse.schema")).isNotEmpty();
        assertThat(environment.getProperty("embedded.clickhouse.host")).isNotEmpty();
        assertThat(environment.getProperty("embedded.clickhouse.port")).isNotEmpty();
        assertThat(environment.getProperty("embedded.clickhouse.user")).isNotEmpty();
        assertThat(environment.getProperty("embedded.clickhouse.password")).isNotNull();
    }

    protected void hasDependsOn(String beanName) {
        assertThat(beanFactory.getBeanDefinition(beanName).getDependsOn())
                .isNotNull()
                .isNotEmpty()
                .contains(BEAN_NAME_EMBEDDED_CLICK_HOUSE);
    }

    @EnableAutoConfiguration
    @Configuration
    static class TestConfiguration {
    }
}
