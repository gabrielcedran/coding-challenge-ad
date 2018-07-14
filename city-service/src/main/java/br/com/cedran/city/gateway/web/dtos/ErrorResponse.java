package br.com.cedran.city.gateway.web.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String errorCode;
    private Object params;
    private String errorMessage;

}
