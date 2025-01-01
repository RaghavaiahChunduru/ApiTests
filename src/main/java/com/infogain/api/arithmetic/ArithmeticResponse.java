package com.infogain.api.arithmetic;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder(setterPrefix = "set")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArithmeticResponse {

    private Long id;
    private Double firstOperand;
    private Double secondOperand;
    private Integer operatorId;
    private Double result;
    private String createdBy;
    private String createdAt;
}
