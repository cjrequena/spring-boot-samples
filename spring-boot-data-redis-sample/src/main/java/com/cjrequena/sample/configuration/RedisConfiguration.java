package com.cjrequena.sample.configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Redis configuration for standalone, sentinel, and cluster modes.
 * Provides properly configured RedisTemplate, StringRedisTemplate, and RedisCommands beans.
 *
 * <p>Supports three deployment modes:
 * <ul>
 *   <li>Standalone - Single Redis instance</li>
 *   <li>Sentinel - High availability with automatic failover</li>
 *   <li>Cluster - Horizontal scaling with data sharding</li>
 * </ul>
 *
 * @author cjrequena
 */
@Slf4j
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisConfiguration {

  private final RedisConfigurationProperties redisConfigurationProperties;

  /**
   * Creates a LettuceConnectionFactory based on the configuration.
   * Supports standalone, sentinel, and cluster modes.
   *
   * @return configured LettuceConnectionFactory
   * @throws IllegalStateException if Redis configuration is invalid
   */
  @Bean("lettuceConnectionFactory")
  @Primary
  public LettuceConnectionFactory redisConnectionFactory() {
    log.info("Initializing Redis connection factory");

    LettuceConnectionFactory factory;

    // Cluster mode configuration
    if (isClusterMode()) {
      log.info("Configuring Redis Cluster mode");
      factory = createClusterConnectionFactory();
    }
    // Sentinel mode configuration
    else if (isSentinelMode()) {
      log.info("Configuring Redis Sentinel mode");
      factory = createSentinelConnectionFactory();
    }
    // Standalone mode configuration (default)
    else {
      log.info("Configuring Redis Standalone mode");
      factory = createStandaloneConnectionFactory();
    }

    // Enable sharing of native connections
    factory.setShareNativeConnection(true);

    // Validate connections
    factory.setValidateConnection(true);

    log.info("Redis connection factory initialized successfully");
    return factory;
  }

  /**
   * Checks if cluster mode is configured.
   */
  private boolean isClusterMode() {
    return redisConfigurationProperties.getCluster() != null
      && redisConfigurationProperties.getCluster().getNodes() != null
      && !redisConfigurationProperties.getCluster().getNodes().isEmpty();
  }

  /**
   * Checks if sentinel mode is configured.
   */
  private boolean isSentinelMode() {
    return redisConfigurationProperties.getSentinel() != null
      && redisConfigurationProperties.getSentinel().getNodes() != null
      && !redisConfigurationProperties.getSentinel().getNodes().isEmpty();
  }

  /**
   * Creates a connection factory for Redis Cluster mode.
   */
  private LettuceConnectionFactory createClusterConnectionFactory() {
    RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(redisConfigurationProperties.getCluster().getNodes());

    // Set max redirects for cluster operations
    Integer maxRedirects = redisConfigurationProperties.getCluster().getMaxRedirects();
    if (maxRedirects != null && maxRedirects > 0) {
      clusterConfig.setMaxRedirects(maxRedirects);
    }

    // Set password if configured
    if (StringUtils.hasText(redisConfigurationProperties.getPassword())) {
      clusterConfig.setPassword(RedisPassword.of(redisConfigurationProperties.getPassword()));
    }

    log.debug("Cluster nodes: {}, max redirects: {}",
      redisConfigurationProperties.getCluster().getNodes(), maxRedirects);

    return new LettuceConnectionFactory(clusterConfig, lettuceClientConfiguration());
  }

  /**
   * Creates a connection factory for Redis Sentinel mode.
   */
  private LettuceConnectionFactory createSentinelConnectionFactory() {
    String masterName = redisConfigurationProperties.getSentinel().getMaster();

    if (!StringUtils.hasText(masterName)) {
      throw new IllegalStateException("Sentinel master name must be configured");
    }

    RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
      .master(masterName);

    // Add sentinel nodes
    redisConfigurationProperties.getSentinel().getNodes().forEach(node -> {
      String[] parts = node.split(":");
      if (parts.length != 2) {
        throw new IllegalArgumentException("Invalid sentinel node format: " + node +
          " (expected format: host:port)");
      }

      try {
        String host = parts[0].trim();
        int port = Integer.parseInt(parts[1].trim());
        sentinelConfig.sentinel(host, port);
        log.debug("Added sentinel node: {}:{}", host, port);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid port number in sentinel node: " + node, e);
      }
    });

    // Set password if configured
    if (StringUtils.hasText(redisConfigurationProperties.getPassword())) {
      sentinelConfig.setPassword(RedisPassword.of(redisConfigurationProperties.getPassword()));
    }

    sentinelConfig.setDatabase(redisConfigurationProperties.getDatabase());

    log.debug("Sentinel master: {}, nodes: {}", masterName,
      redisConfigurationProperties.getSentinel().getNodes());

    return new LettuceConnectionFactory(sentinelConfig, lettuceClientConfiguration());
  }

  /**
   * Creates a connection factory for Redis Standalone mode.
   */
  private LettuceConnectionFactory createStandaloneConnectionFactory() {
    String host = redisConfigurationProperties.getHost();
    Integer port = redisConfigurationProperties.getPort();

    if (!StringUtils.hasText(host)) {
      throw new IllegalStateException("Redis host must be configured");
    }

    if (port == null || port <= 0) {
      throw new IllegalStateException("Redis port must be configured and positive");
    }

    RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(host, port);

    // Set password if configured
    if (StringUtils.hasText(redisConfigurationProperties.getPassword())) {
      standaloneConfig.setPassword(RedisPassword.of(redisConfigurationProperties.getPassword()));
    }

    standaloneConfig.setDatabase(redisConfigurationProperties.getDatabase());

    log.debug("Redis host: {}, port: {}, database: {}", host, port,
      redisConfigurationProperties.getDatabase());

    return new LettuceConnectionFactory(standaloneConfig, lettuceClientConfiguration());
  }

  /**
   * Configures the Lettuce client with timeout and SSL settings.
   */
  private LettuceClientConfiguration lettuceClientConfiguration() {
    LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
      LettuceClientConfiguration.builder();

    // Set command timeout if configured
    if (redisConfigurationProperties.getTimeout() != null &&
      redisConfigurationProperties.getTimeout() > 0) {
      Duration timeout = Duration.ofMillis(redisConfigurationProperties.getTimeout());
      builder.commandTimeout(timeout);
      log.debug("Command timeout set to: {} ms", redisConfigurationProperties.getTimeout());
    }

    // Enable SSL if configured
    if (redisConfigurationProperties.isSsl()) {
      builder.useSsl();
      log.debug("SSL enabled for Redis connection");
    }

    // Disable peer verification in SSL (only if needed for development)
    // builder.useSsl().disablePeerVerification();

    return builder.build();
  }

  /**
   * Creates a RedisTemplate with proper serialization configured.
   * Uses String serializer for keys and JSON serializer for values.
   *
   * @param connectionFactory the Lettuce connection factory
   * @return configured RedisTemplate
   */
  @Bean
  public RedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
    log.info("Configuring RedisTemplate");

    RedisTemplate<String, Object> template = new RedisTemplate<>();

    // Configure serializers
    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

    template.setConnectionFactory(connectionFactory);

    // Key serializers
    template.setKeySerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);

    // Value serializers
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);

    // Enable transaction support
    template.setEnableTransactionSupport(false);

    // Initialize the template
    template.afterPropertiesSet();

    log.debug("RedisTemplate configured with JSON serialization");
    return template;
  }

  /**
   * Creates a StringRedisTemplate for string-only operations.
   * More efficient than RedisTemplate when working with strings.
   *
   * @param connectionFactory the Lettuce connection factory
   * @return configured StringRedisTemplate
   */
  @Bean("stringRedisTemplate")
  public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
    log.info("Configuring StringRedisTemplate");

    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(connectionFactory);

    // StringRedisTemplate uses StringRedisSerializer by default
    // But we can customize if needed
    StringRedisSerializer stringSerializer = new StringRedisSerializer();
    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(stringSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(stringSerializer);

    // Initialize the template
    template.afterPropertiesSet();

    log.debug("StringRedisTemplate configured");
    return template;
  }

  /**
   * Creates a RedisCommands bean for low-level Lettuce operations.
   * Required for RediSearch and other advanced Redis modules.
   *
   * <p>IMPORTANT: This creates a StatefulRedisConnection that should be reused.
   * The connection is thread-safe and can handle concurrent operations.
   *
   * @param connectionFactory the Lettuce connection factory
   * @return RedisCommands instance for sync operations
   */
  @Bean
  public RedisCommands<String, String> redisCommands(LettuceConnectionFactory connectionFactory) {
    log.info("Configuring RedisCommands for RediSearch operations");

    try {
      // Get the native Lettuce client
      RedisClient client = (RedisClient) connectionFactory.getNativeClient();

      if (client == null) {
        throw new IllegalStateException("Unable to get native Redis client from connection factory");
      }

      // Create a stateful connection
      StatefulRedisConnection<String, String> connection = client.connect();

      // Return sync commands
      RedisCommands<String, String> commands = connection.sync();

      log.info("RedisCommands configured successfully");
      return commands;

    } catch (ClassCastException e) {
      log.error("Failed to cast native client to RedisClient. " +
        "This configuration only supports standalone Redis or Redis with Lettuce client.", e);
      throw new IllegalStateException("Unsupported Redis client type", e);
    } catch (Exception e) {
      log.error("Failed to create RedisCommands bean", e);
      throw new IllegalStateException("Failed to initialize Redis commands", e);
    }
  }

  /**
   * Alternative method to get RedisCommands using RedisTemplate connection.
   * Use this if the primary method doesn't work with your setup.
   */
  /*
  @Bean
  public RedisCommands<String, String> redisCommandsAlternative(
          RedisTemplate<String, Object> redisTemplate) {
    log.info("Configuring RedisCommands (alternative method)");

    return redisTemplate.execute((RedisCallback<RedisCommands<String, String>>) connection -> {
      Object nativeConnection = connection.getNativeConnection();

      if (nativeConnection instanceof StatefulRedisConnection) {
        @SuppressWarnings("unchecked")
        StatefulRedisConnection<String, String> statefulConnection =
                (StatefulRedisConnection<String, String>) nativeConnection;
        return statefulConnection.sync();
      } else if (nativeConnection instanceof RedisCommands) {
        @SuppressWarnings("unchecked")
        RedisCommands<String, String> commands = (RedisCommands<String, String>) nativeConnection;
        return commands;
      } else {
        throw new IllegalStateException(
                "Unexpected native connection type: " + nativeConnection.getClass().getName());
      }
    });
  }
  */
}

