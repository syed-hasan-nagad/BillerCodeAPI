package com.billercode.api.billercodeapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
//@AllArgsConstructor
public class ConfirmationResponse extends Response {

}
