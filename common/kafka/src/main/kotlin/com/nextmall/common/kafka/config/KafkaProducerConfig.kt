package com.nextmall.common.kafka.config

import com.nextmall.common.kafka.producer.EventPublisher
import com.nextmall.common.kafka.producer.KafkaEventPublisher
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import tools.jackson.databind.ObjectMapper

@Configuration
@ConditionalOnProperty(name = ["nextmall.kafka.enabled"], havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(KafkaProperties::class)
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val props = kafkaProperties.producer
        val configs =
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.ACKS_CONFIG to props.acks,
                ProducerConfig.RETRIES_CONFIG to props.retries,
                ProducerConfig.BATCH_SIZE_CONFIG to props.batchSize,
                ProducerConfig.LINGER_MS_CONFIG to props.lingerMs,
            )
        return DefaultKafkaProducerFactory(configs)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> =
        KafkaTemplate(producerFactory)

    @Bean
    fun eventPublisher(
        kafkaTemplate: KafkaTemplate<String, String>,
        objectMapper: ObjectMapper,
    ): EventPublisher = KafkaEventPublisher(kafkaTemplate, objectMapper)
}
