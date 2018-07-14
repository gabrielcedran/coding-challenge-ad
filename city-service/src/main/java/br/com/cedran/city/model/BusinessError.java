package br.com.cedran.city.model;

import lombok.Getter;

@Getter
public enum BusinessError {


    CITY_NAME_EXISTENT("BE00001", "City name already in use"),
    CITY_NOT_EXISTENT("BE00002", "City not existent"),
    ROUTE_WITH_DUPLICATED_CITY("BE00003", "Duplicated cities in the route"),
    DESTINATION_EXISTENT("BE00004", "Destination already existent to this origin"),
    DESTINATION_NOt_EXISTENT("BE00005", "Destination non existent");

    private String errorCode;
    private String message;

    BusinessError(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
