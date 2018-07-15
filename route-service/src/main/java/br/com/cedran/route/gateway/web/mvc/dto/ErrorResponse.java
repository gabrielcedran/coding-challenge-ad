package br.com.cedran.route.gateway.web.mvc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String errorCode;
    private Object params;
    private String errorMessage;

}
