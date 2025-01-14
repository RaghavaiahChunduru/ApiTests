package com.infogain.api.arithmetic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.infogain.utils.InfinitySerializer;

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
public class ValidArithmeticResponse {

    private Long id;
    private Double firstOperand;
    private Double secondOperand;
    private Integer operatorId;

    @JsonSerialize(using = InfinitySerializer.class)
    private Double result;

    // private String createdBy;
    // private String createdAt;
}
