package io.github.jianzhiunique.lag;

import io.github.jianzhiunique.config.KafkaConfig;
import kafka.common.OffsetAndMetadata;
import kafka.coordinator.group.GroupMetadataManager;
import kafka.coordinator.group.GroupTopicPartition;
import kafka.coordinator.group.OffsetKey;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import kafka.coordinator.*;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class LagMetrics {

    public static Map<GroupTopicPartition, Long> lagMap = new HashMap();

    @Autowired
    KafkaConfig kafkaConfig;

    @PostConstruct
    void runConsumer() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Properties props = new Properties();
                props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-jmx-exporter");
                props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getConfig().get("server"));
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getCanonicalName());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getCanonicalName());
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
                if (Boolean.valueOf(kafkaConfig.getConfig().get("sasl"))) {
                    props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, kafkaConfig.getConfig().get("sasl_protocol"));
                    props.put(SaslConfigs.SASL_MECHANISM, kafkaConfig.getConfig().get("sasl_mechanism"));
                    props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaConfig.getConfig().get("sasl_user") + "\" password=\"" + kafkaConfig.getConfig().get("sasl_pass") + "\";");
                }

                KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer(props);
                consumer.subscribe(Collections.singleton("__consumer_offsets"));
                consumer.poll(Duration.ofSeconds(5));

                consumer.seekToBeginning(consumer.assignment());
                while (true) {
                    try {
                        System.out.println("poll");
                        ConsumerRecords<byte[], byte[]> consumerRecords = consumer.poll(Duration.ofSeconds(5));

                        consumerRecords.forEach(record -> {
                            byte[] key = record.key();
                            if (key != null) {
                                Object o = GroupMetadataManager.readMessageKey(ByteBuffer.wrap(key));
                                if (o != null && o instanceof OffsetKey) {
                                    OffsetKey offsetKey = (OffsetKey) o;
                                    GroupTopicPartition groupTopicPartition = offsetKey.key();

                                    byte[] value = record.value();
                                    if (value != null) {
                                        OffsetAndMetadata offsetAndMetadata = GroupMetadataManager.readOffsetMessageValue(ByteBuffer.wrap(value));
                                        System.out.println(groupTopicPartition.group());
                                        System.out.println(groupTopicPartition.topicPartition().topic());
                                        System.out.println(groupTopicPartition.topicPartition().partition());

                                        System.out.println(offsetAndMetadata.offset());
                                        lagMap.put(groupTopicPartition, offsetAndMetadata.offset());
                                    }
                                }
                            }

                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.setDaemon(true);
        thread.start();


    }
}
