package sg.nus.lowlight.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JournalEntry {
    private String title;
    private String body;
    private long datePublished = System.currentTimeMillis();
    private String mood;
}
