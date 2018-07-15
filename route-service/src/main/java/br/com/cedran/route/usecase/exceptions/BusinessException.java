package br.com.cedran.route.usecase.exceptions;

import br.com.cedran.route.model.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private String errorCode;
    private Object params;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getErrorCode();
    }

    public BusinessException(ErrorCode errorCode, Object params) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getErrorCode();
        this.params = params;
    }
}
