package sg.nus.lowlight.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.username}")
    private String redisUser;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.database}")
    private Integer redisDatabase;

    public JedisConnectionFactory createJedisFac() {
        // Create a redis configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);

        // Only set the username and passwrd if they are not set
        if (!"NOT_SET".equals(redisUser.trim())) {
            config.setUsername(redisUser);
            config.setPassword(redisPassword);
        }

        // Configure Jedis for connection
        JedisClientConfiguration jedisClient = JedisClientConfiguration
                .builder().build();
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet(); //used to trigger the initialization of the underlying Jedis connection factory based on the configured properties
        return jedisFac;
    }

    @Bean("redis")
    public RedisTemplate<String, String> createRedisConnection() {
        JedisConnectionFactory jedisFac = createJedisFac();
        // Create Redis Template and establish connection
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisFac);

        // Serializer because Java objects are unreadable by humans and our written programs
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}
