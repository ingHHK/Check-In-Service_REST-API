package proj.checkIN.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
	    return new JedisConnectionFactory(config);
	}
	
	@Bean
	public RedisTemplate<String, String> redisTemplate(){
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		//redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(type));
		return redisTemplate;
	}
}