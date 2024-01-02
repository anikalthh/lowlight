package sg.nus.lowlight;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.model.Post;
import sg.nus.lowlight.model.User;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.ChatbotService;
import sg.nus.lowlight.service.LoadDummyDataService;
import sg.nus.lowlight.service.UserService;

@SpringBootApplication
public class LowlightApplication implements CommandLineRunner {

	@Autowired 
	private LoadDummyDataService dummyDataSvc;

	@Autowired
	private ChannelsService chSvc;

	@Autowired
	private UserService userSvc;

	@Autowired
	ChatbotService cbSvc;

	public static void main(String[] args) {
		SpringApplication.run(LowlightApplication.class, args);
	}

	@Override
	public void run(String... args) throws FileNotFoundException, IOException {

		// One-time dummy data population -- comment out once in redis
		// // Channels
		// List<Channel> dummyChannels = dummyDataSvc.getDummyChannels();

		// for (Channel c : dummyChannels) {
		// 	chSvc.saveChannel(c.getChannelName(), c.getDescription());
		// }

		// System.out.println("-------- CHANNELS DUMMY DATA LOADED --------");

		// // Users
		// List<User> dummyUsers = dummyDataSvc.getDummyUsers();

		// for (User u : dummyUsers) {
		// 	userSvc.saveUser(u);
		// 	userSvc.registrationSaveUser(u.getUsername(), u.getPassword());
		// }

		// System.out.println("-------- USERS DUMMY DATA LOADED --------");

		// // Posts
		// List<Post> dummyPosts = dummyDataSvc.getDummyPosts();

		// for (Post p : dummyPosts) {
		// 	// Save post to channel
		// 	chSvc.savePost(p.getChannelName(), p);

		// 	// Save post in user details
		// 	userSvc.addPostToUserDetails(p.getAuthor(), p);

		// 	// Update mood in user details
		// 	userSvc.addMoodToUserDetails(p.getAuthor(), p.getMood());
		// }

		// System.out.println("-------- POSTS DUMMY DATA LOADED --------");

	}
}
