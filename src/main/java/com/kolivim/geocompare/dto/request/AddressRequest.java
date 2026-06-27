package com.kolivim.geocompare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    @Size(min = 10, message = "Количество символов адреса должно быть не менее 10")
    @Pattern(regexp = ".*,.*", message = "Адрес должен содержать запятую, например: Москва, Красная площадь, 1")
    @NotBlank(message = "Адрес не может быть пустым")
    private String address;
}