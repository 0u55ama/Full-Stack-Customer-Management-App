package com.osm.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT * FROM customer
                """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomersById(int id) {
        var sql = """ 
                SELECT * FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate
                .query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
        INSERT INTO customer(name, email, age, gender)
        VALUES (?, ?, ?, ?)
        """;
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getGender().toString()  // Convert enum value to string
        );
        System.out.println("jdbcTemplate.update = " + result);
    }


    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, customerId);
        System.out.println("deleted customer by ID = " + result);

    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null){
            String sql = " UPDATE customer SET name = ? WHERE id = ?";
            int nameResult = jdbcTemplate.update(sql, update.getName(), update.getId());
            System.out.println("updated customer name result = " + nameResult);
        }

        if (update.getAge() != null){
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int ageResult = jdbcTemplate.update(sql, update.getAge(), update.getId());
            System.out.println("updated customer age result = " + ageResult);
        }

        if (update.getEmail() != null){
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int emailResult = jdbcTemplate.update(sql, update.getEmail(), update.getId());
            System.out.println("updated customer email result = " + emailResult);
        }
/*
        if (update.getGender() != null) {
            String sql = "UPDATE customer SET gender = ? WHERE id = ?";

            // Convert the enum value to lowercase before passing it to the query
            String lowercaseGender = update.getGender().toString().toLowerCase();

            int genderResult = jdbcTemplate.update(sql, lowercaseGender, update.getId());
            System.out.println("updated customer gender result = " + genderResult);
        }

        if (update.getGender() != null) {
            String sql = "UPDATE customer SET gender = ? WHERE id = ?";
            int genderResult = jdbcTemplate.update(
                    sql,
                    update.getGender().toString(), // Use enum's string representation
                    update.getId()
            );
            System.out.println("updated customer gender result = " + genderResult);
        }

 */

    }
}