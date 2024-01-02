package sg.nus.lowlight.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.nus.lowlight.validationgroups.SignUpValidationGroup;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @NotBlank(message = "Username cannot be blank.")
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min=5, message = "Password has to have at least 5 characters.")
    private String password;

    @Email(message = "Invalid email.", groups = SignUpValidationGroup.class)
    @NotBlank(message = "Email cannot be blank.", groups = SignUpValidationGroup.class)
    private String email;
    private String lastViewedChannel = "User has not viewed any channels yet.";
    private long lastActiveDate;
    private Integer[] moodtracker = {0, 0, 0, 0, 0}; // [happy, peaceful, sad, angry, unmotivated]
    private List<Post> postsMade = new LinkedList<>();
    private List<JournalEntry> entriesMade = new LinkedList<>();

    // Add new post to List of posts made
    public void addPost(Post post) {
        postsMade.add(0, post);
    }

    // Remove a post
    public void removePost(int index) {
        String moodToRemove = postsMade.get(index).getMood();
        removeMood(moodToRemove);
        postsMade.remove(index);
    }

    // Add new entry to List of entries made
    public void addJournalEntry(JournalEntry entry) {
        entriesMade.add(0, entry);
    }

    // Remove an entry
    public void removeJournalEntry(int index) {
        String moodToRemove = entriesMade.get(index).getMood();
        removeMood(moodToRemove);
        entriesMade.remove(index);
    }

    // Add mood data to moodtracker
    public void addMood(String mood) {
        switch (mood) {
            case "Happy 😄":
                moodtracker[0] ++;
                break;

            case "Peaceful 😌":
                moodtracker[1] ++;
                break;

            case "Sad 😞":
                moodtracker[2] ++;
                break;

            case "Angry 😡":
                moodtracker[3] ++;
                break;

            case "Unmotivated 🫠":
                moodtracker[4] ++;
                break;
        }
    }

    public void removeMood(String mood) {
        switch (mood) {
            case "Happy 😄":
                moodtracker[0] --;
                break;

            case "Peaceful 😌":
                moodtracker[1] --;
                break;

            case "Sad 😞":
                moodtracker[2] --;
                break;

            case "Angry 😡":
                moodtracker[3] --;
                break;

            case "Unmotivated 🫠":
                moodtracker[4] --;
                break;
        }
    }
}
