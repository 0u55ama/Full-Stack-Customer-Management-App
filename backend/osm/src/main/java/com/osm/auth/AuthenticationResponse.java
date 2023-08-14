package com.osm.auth;

import com.osm.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
){

}
