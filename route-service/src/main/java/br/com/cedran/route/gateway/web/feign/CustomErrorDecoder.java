package br.com.cedran.route.gateway.web.feign;

import br.com.cedran.route.usecase.exceptions.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            new NotFoundException();
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
