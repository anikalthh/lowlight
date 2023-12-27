package sg.nus.lowlight;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sg.nus.lowlight.model.Channel;
import sg.nus.lowlight.service.ChannelsService;
import sg.nus.lowlight.service.LoadDummyDataService;

@SpringBootApplication
public class LowlightApplication implements CommandLineRunner {

	@Autowired 
	private LoadDummyDataService dummyDataSvc;

	@Autowired
	private ChannelsService chSvc;

	public static void main(String[] args) {
		SpringApplication.run(LowlightApplication.class, args);
	}

	@Override
	public void run(String... args) throws FileNotFoundException, IOException {
		List<Channel> dummyChannels = dummyDataSvc.getDummyChannels();

		for (Channel c : dummyChannels) {
			chSvc.saveChannel(c.getChannelName(), c.getDescription());
		}

		System.out.println("-------- DUMMY DATA LOADED --------");
	}
}
