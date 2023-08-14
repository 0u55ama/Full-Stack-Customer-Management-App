package com.osm.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(1L);
        //when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("age")).thenReturn(19);
        when(resultSet.getString("name")).thenReturn("lzzy");
        when(resultSet.getString("email")).thenReturn("lzzy@mail.com");
        when(resultSet.getString("gender")).thenReturn("FEMALE"); // Set the gender value here



        // When
        Customer actual = customerRowMapper.mapRow(
                resultSet,
                1
        );

        // Then
        Customer expected = new Customer(
                1L,
                "lzzy",
                "lzzy@mail.com",
                "azerty.123..", 19,
                Gender.FEMALE

        );
        assertThat(actual).isEqualTo(expected);
    }
}