package sg.nus.lowlight.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Channel {

    @NotBlank(message = "Channel name cannot be blank.")
    private String channelName;

    @NotBlank(message = "Channel description cannot be blank.")
    @Size(min = 10, message = "Channel description must have at least 10 characters.")
    private String description;
}
