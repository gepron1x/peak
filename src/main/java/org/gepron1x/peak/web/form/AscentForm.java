package org.gepron1x.peak.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Getter
@Setter
public class AscentForm {

    @NotBlank(message = "Укажите название группы")
    @Size(max = 150, message = "Название группы не должно превышать 150 символов")
    private String groupName;

    @NotNull(message = "Выберите гору")
    private Long mountainId;

    @NotNull(message = "Укажите дату и время начала")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime startTime;
}
