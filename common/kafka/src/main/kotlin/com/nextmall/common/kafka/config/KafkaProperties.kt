package com.nextmall.common.kafka.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nextmall.kafka")
data class KafkaProperties(
    val bootstrapServers: String,
    val producer: ProducerProperties = ProducerProperties(),
    val consumer: ConsumerProperties? = null,
) {
    data class ProducerProperties(
        val acks: String = "all",
        val retries: Int = 3,
        val batchSize: Int = 16384,
        val lingerMs: Int = 5,
    )

    data class ConsumerProperties(
        val groupId: String,
        val autoOffsetReset: String = "earliest",
        val enableAutoCommit: Boolean = false,
    )
}
