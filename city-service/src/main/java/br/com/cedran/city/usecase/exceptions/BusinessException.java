package br.com.cedran.city.usecase.exceptions;

import br.com.cedran.city.model.BusinessError;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private String errorCode;
    private Object params;

    public BusinessException(BusinessError businessError) {
        super(businessError.getMessage());
        this.errorCode = businessError.getErrorCode();
    }

    public BusinessException(BusinessError businessError, Object params) {
        super(businessError.getMessage());
        this.errorCode = businessError.getErrorCode();
        this.params = params;
    }
}
