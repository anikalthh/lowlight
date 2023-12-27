package sg.nus.lowlight.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {

    @NotBlank(message = "Title cannot be blank.")
    private String title;

    private String author;

    @NotBlank(message = "Post content cannot be blank.")
    @Size(min = 10, message = "Post content must have at least 10 characters.")
    private String body;

    private long datePublished = System.currentTimeMillis();
    private Boolean isAnonymous;

    @NotBlank(message = "Which mood best resonates with this post?")
    private String mood;
}
