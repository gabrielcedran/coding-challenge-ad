package br.com.cedran.route.model;

import lombok.Getter;

@Getter
public enum ErrorCode {

    CITY_NAME_EXISTENT("BE00001", "City name already in use"),
    CITY_NOT_EXISTENT("BE00002", "City not existent"),
    ROUTE_WITH_DUPLICATED_CITY("BE00003", "Duplicated cities in the route"),
    DESTINATION_EXISTENT("BE00004", "Destination already existent to this origin"),
    DESTINATION_NOT_EXISTENT("BE00005", "Destination non existent"),
    UNEXPECTED_ERROR("GE00001", "Unexpected error"),
    INVALID_ARGUMENT("BE00006", "Invalid argument"),
    ORIGIN_AND_DESTINATION_THE_SAME("BE00007", "The origin and destination are the same");

    private String errorCode;
    private String message;

    ErrorCode(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
