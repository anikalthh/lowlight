package sg.nus.lowlight.model;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostComment {
    private String commentId = UUID.randomUUID().toString();
    private String author;
    private long datePublished = System.currentTimeMillis();

    @NotBlank(message = "Comment cannot be blank")
    private String commentBody;
}
