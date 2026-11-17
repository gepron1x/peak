package org.gepron1x.peak.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.gepron1x.peak.domain.Mountain;

@Getter
@Setter
public class MountainForm {

    @NotBlank(message = "Укажите название вершины")
    @Size(max = 150, message = "Название не должно превышать 150 символов")
    private String name;

    @NotNull(message = "Укажите высоту")
    @Min(value = 1, message = "Высота должна быть положительным числом метров")
    private Integer height;

    @NotBlank(message = "Укажите страну")
    @Size(max = 100, message = "Страна не должна превышать 100 символов")
    private String country;

    @NotBlank(message = "Укажите район")
    @Size(max = 100, message = "Район не должен превышать 100 символов")
    private String region;

    public static MountainForm from(Mountain mountain) {
        MountainForm form = new MountainForm();
        form.setName(mountain.getName());
        form.setHeight(mountain.getHeight());
        form.setCountry(mountain.getCountry());
        form.setRegion(mountain.getRegion());
        return form;
    }
}
