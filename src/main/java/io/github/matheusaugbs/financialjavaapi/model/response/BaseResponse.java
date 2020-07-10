package io.github.matheusaugbs.financialjavaapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class BaseResponse {
    private int statusCode;
    private String message;
}
