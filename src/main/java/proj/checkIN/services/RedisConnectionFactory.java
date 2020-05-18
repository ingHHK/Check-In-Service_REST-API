//package proj.checkIN.services;
//
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//import java.time.Duration;
//
//@Configuration
//public class RedisConnectionFactory {
//    @Bean(name = "stringRedisTemplate")
//    public StringRedisTemplate init() {
//        GenericObjectPoolConfig poolConfig;//connection pool
//        LettucePoolingClientConfiguration poolingClientConfiguration;//lettuce connection pool configuration
//        RedisStandaloneConfiguration standaloneConfiguration;//spring redis configuration
//        LettuceConnectionFactory lettuceConnectionFactory;//lettuce connection factory
//        StringRedisTemplate stringRedisTemplate;//redis operation class
//        // connection pool
//        poolConfig = new GenericObjectPoolConfig();
//        poolConfig.setMaxIdle(100);
//        poolConfig.setMaxTotal(100);
//        poolConfig.setMaxWaitMillis(Duration.ofMinutes(1).toMillis());
//        // Connect client configuration
//        poolingClientConfiguration = LettucePoolingClientConfiguration.builder()
//                .poolConfig(poolConfig)
//                .build();
//        // connection configuration
//        standaloneConfiguration = new RedisStandaloneConfiguration("127.0.0.1", 6379);//port and address
//        standaloneConfiguration.setPassword(RedisPassword.none());// here redis does not set a password
//        standaloneConfiguration.setDatabase(0);// The default selection of the first database
//        // Connect to the factory
//        lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration, poolingClientConfiguration);
//        // remember the app settings
//        lettuceConnectionFactory.afterPropertiesSet();
//        // has serialized serialization of strings to UTF-8
//        stringRedisTemplate = new StringRedisTemplate(lettuceConnectionFactory);
//        return stringRedisTemplate;
//    }
//}
