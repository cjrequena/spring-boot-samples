package com.cjrequena.sample.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

  private final RedisConfigurationProperties redisConfigurationProperties;

  @Bean
  @Primary
  public LettuceConnectionFactory redisConnectionFactory() {
    if (redisConfigurationProperties.getCluster() != null &&
      redisConfigurationProperties.getCluster().getNodes() != null &&
      !redisConfigurationProperties.getCluster().getNodes().isEmpty()) {
      RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisConfigurationProperties.getCluster().getNodes());
      redisClusterConfiguration.setMaxRedirects(redisConfigurationProperties.getCluster().getMaxRedirects());
      return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration());
    } else if (redisConfigurationProperties.getSentinel() != null &&
      redisConfigurationProperties.getSentinel().getNodes() != null &&
      !redisConfigurationProperties.getSentinel().getNodes().isEmpty()) {
      RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration().master(redisConfigurationProperties.getSentinel().getMaster());
      redisConfigurationProperties.getSentinel().getNodes().forEach(node -> {
        String[] parts = node.split(":");
        redisSentinelConfiguration.sentinel(parts[0], Integer.parseInt(parts[1]));
      });
      return new LettuceConnectionFactory(redisSentinelConfiguration, lettuceClientConfiguration());
    } else {
      RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
        redisConfigurationProperties.getHost(),
        redisConfigurationProperties.getPort()
      );
      redisStandaloneConfiguration.setPassword(redisConfigurationProperties.getPassword());
      redisStandaloneConfiguration.setDatabase(redisConfigurationProperties.getDatabase());
      return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration());
    }
  }

  private LettuceClientConfiguration lettuceClientConfiguration() {
    LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
    if (redisConfigurationProperties.getTimeout() != null) {
      builder.commandTimeout(Duration.ofMillis(redisConfigurationProperties.getTimeout()));
    }
    if (redisConfigurationProperties.isSsl()) {
      builder.useSsl();
    }
    return builder.build();
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());
    return template;
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }
}
