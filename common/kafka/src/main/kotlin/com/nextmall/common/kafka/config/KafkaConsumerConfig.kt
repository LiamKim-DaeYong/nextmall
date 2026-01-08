package com.nextmall.common.kafka.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@Configuration
@ConditionalOnProperty(prefix = "nextmall.kafka.consumer", name = ["group-id"])
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props = kafkaProperties.consumer!!
        val configs =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.GROUP_ID_CONFIG to props.groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to props.autoOffsetReset,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to props.enableAutoCommit,
            )
        return DefaultKafkaConsumerFactory(configs)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>,
    ): ConcurrentKafkaListenerContainerFactory<String, String> =
        ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            setConsumerFactory(consumerFactory)
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        }
}
