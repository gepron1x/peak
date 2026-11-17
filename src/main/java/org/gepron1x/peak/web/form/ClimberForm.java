package org.gepron1x.peak.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimberForm {

    @NotBlank(message = "Укажите фамилию")
    @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
    private String lastName;

    @NotBlank(message = "Укажите имя")
    @Size(max = 200, message = "Имя не должно превышать 200 символов")
    private String firstName;

    @NotBlank(message = "Укажите отчество")
    @Size(max = 100, message = "Отчество не должно превышать 100 символов")
    private String middleName;

    @NotBlank(message = "Укажите контактный телефон")
    @Size(max = 30, message = "Телефон не должен превышать 30 символов")
    @Pattern(regexp = "^(?=.*\\d)[+]?[0-9()\\-\\s]{5,30}$",
            message = "Укажите корректный номер телефона (цифры, +, -, скобки)")
    private String phone;

    @NotBlank(message = "Укажите адрес")
    @Size(max = 300, message = "Адрес не должен превышать 300 символов")
    private String address;
}
