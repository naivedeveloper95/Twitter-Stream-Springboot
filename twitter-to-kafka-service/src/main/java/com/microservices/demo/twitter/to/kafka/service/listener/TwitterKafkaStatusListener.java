package com.microservices.demo.twitter.to.kafka.service.listener;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToAvroTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterKafkaStatusListener extends StatusAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);
    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
    private final TwitterStatusToAvroTransformer transformer;

    public TwitterKafkaStatusListener(
            KafkaConfigData kafkaConfigData,
            KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
            TwitterStatusToAvroTransformer transformer
    ) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.transformer = transformer;
    }

    @Override
    public void onStatus(Status status) {
        LOG.info("Received status text {} sending to kafka topic {}", status.getText(), kafkaConfigData.getTopicName());
        TwitterAvroModel twitterAvroModel = transformer.getTwitterAvroModelFromStatus(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
