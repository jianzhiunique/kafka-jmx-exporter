# Kafka JMX exporter

This is an empty jar for running https://github.com/prometheus/jmx_exporter.

The aim is to avoid reloading kafka when config file changed.

## Usage
```
java -javaagent:jmx_prometheus_javaagent-0.13.0.jar=8070:config.yaml -jar kafka-jmx-exporter-1.0.0.jar
```

## Config

for more details, please refer to https://github.com/prometheus/jmx_exporter.

### kafka 0.11.0.x
```yaml
hostPort: 127.0.0.1:9999
whitelistObjectNames: 
    - "kafka.server:type=BrokerTopicMetrics,name=*PerSec,topic=*" 
    - "kafka.server:type=BrokerTopicMetrics,name=#PerSec" 
    - "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce"
    - "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer"
    - "kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchFollower"
    - "kafka.network:type=RequestMetrics,name=*TimeMs,request=Produce"
    - "kafka.network:type=RequestMetrics,name=*TimeMs,request=FetchConsumer"
    - "kafka.network:type=RequestMetrics,name=*TimeMs,request=FetchFollower"
    - "kafka.controller:type=KafkaController,name=*"
    - "kafka.controller:type=ControllerStats,name=LeaderElectionRateAndTimeMs"
    - "kafka.controller:type=ControllerStats,name=UncleanLeaderElectionsPerSec"
    - "kafka.server:type=ReplicaManager,name=*"
    # - "kafka.server:type=ReplicaFetcherManager,name=MaxLag,clientId=Replica"
    # - "kafka.server:type=FetcherLagMetrics,name=ConsumerLag,clientId=*,topic=*,partition=*"
    - "kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Produce"
    - "kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Fetch"
    - "kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent"
    - "kafka.server:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent"
    - "kafka.network:type=RequestChannel,name=RequestQueueSize"
    - "kafka.network:type=RequestChannel,name=ResponseQueueSize"
    - "kafka.server:type=SessionExpireListener,name=*"
    - "kafka.log:type=Log,name=Size,topic=*,partition=*"
    # for jvm, on openjdk 1.8
    - "java.nio:type=BufferPool,name=*"
    - "java.lang:type=OperatingSystem"
    - "java.lang:type=GarbageCollector,name=*"
rules:
    - pattern : kafka.server<type=(.+), name=(.+), topic=(.+)><>Count
      name: kafka_server_$1_$2_Count_per_topic
      type: COUNTER
      labels:
          topic: "$3"
    - pattern : kafka.server<type=(.+), name=(.+), topic=(.+)><>OneMinuteRate
      name: kafka_server_$1_$2_OneMinuteRate_per_topic
      type: GAUGE
      labels:
          topic: "$3"
    - pattern : kafka.server<type=(.+), name=(.+)><>Count
      name: kafka_server_$1_$2_Count
      type: COUNTER
    - pattern : kafka.server<type=(.+), name=(.+)><>OneMinuteRate
      name: kafka_server_$1_$2_OneMinuteRate
      type: GAUGE
    - pattern : kafka.network<type=(.+), name=(.+PerSec), request=(.+)><>Count
      name: kafka_network_$1_$2_Count
      type: COUNTER
      labels:
          request: "$3"
    - pattern : kafka.network<type=(.+), name=(.+PerSec), request=(.+)><>OneMinuteRate
      name: kafka_network_$1_$2_OneMinuteRate
      type: GAUGE
      labels:
          request: "$3"
    - pattern : kafka.network<type=(.+), name=(.+TimeMs), request=(.+)><>Max
      name: kafka_network_$1_$2_Max
      type: GAUGE
      labels:
          request: "$3"
    - pattern : kafka.network<type=(.+), name=(.+TimeMs), request=(.+)><>95thPercentile
      name: kafka_network_$1_$2_95thPercentile
      type: GAUGE
      labels:
          request: "$3"
    - pattern : kafka.controller<type=KafkaController, name=(.+)><>Value
      name: kafka_controller_KafkaController_$1_Value
      type: GAUGE
    - pattern : kafka.controller<type=ControllerStats, name=(.+)><>OneMinuteRate
      name: kafka_controller_ControllerStats_$1_OneMinuteRate
      type: GAUGE
    - pattern : kafka.controller<type=ControllerStats, name=(.+)><>Count
      name: kafka_controller_ControllerStats_$1_Count
      type: COUNTER
    - pattern : kafka.server<type=ReplicaManager, name=(.+)><>Value
      name: kafka_server_ReplicaManager_$1_Value
      type: GAUGE
    - pattern : kafka.server<type=ReplicaManager, name=(.+)><>OneMinuteRate
      name: kafka_server_ReplicaManager_$1_OneMinuteRate
      type: GAUGE
    - pattern : kafka.server<type=ReplicaManager, name=(.+)><>Count
      name: kafka_server_ReplicaManager_$1_Count
      type: COUNTER
    #- pattern : kafka.server<type=ReplicaFetcherManager, name=MaxLag, clientId=Replica><>Value
     # name: kafka_server_ReplicaFetcherManager_ReplicaMaxLag_Value
     # type: GAUGE
    #- pattern : kafka.server<type=FetcherLagMetrics, name=ConsumerLag, clientId=(.+), topic=(.+), partition=(.+)><>Value
     # name: kafka_server_FetcherLagMetrics_ConsumerLag_Value
      #labels:
       #   clientId: "$1"
        #  topic: "$2"
         # partition: "$3"
      #type: GAUGE
    - pattern : kafka.server<type=DelayedOperationPurgatory, name=PurgatorySize, delayedOperation=(.+)><>Value
      name: kafka_server_DelayedOperationPurgatory_PurgatorySize_$1_Value
      type: GAUGE
    - pattern : kafka.network<type=SocketServer, name=NetworkProcessorAvgIdlePercent><>Value
      name: kafka_network_SocketServer_NetworkProcessorAvgIdlePercent_Value
      type: GAUGE
    - pattern : kafka.server<type=KafkaRequestHandlerPool, name=RequestHandlerAvgIdlePercent><>OneMinuteRate
      name: kafka_server_KafkaRequestHandlerPool_RequestHandlerAvgIdlePercent_OneMinuteRate
      type: GAUGE
    - pattern : kafka.network<type=RequestChannel, name=(.+)><>Value
      name: kafka_network_RequestChannel_$1_Value
      type: GAUGE
    - pattern : kafka.server<type=SessionExpireListener, name=ZooKeeperAuthFailuresPerSec><>OneMinuteRate
      name: kafka_server_SessionExpireListener_$1_OneMinuteRate
      type: GAUGE
    - pattern : kafka.server<type=SessionExpireListener, name=(.+)><>Count
      name: kafka_server_SessionExpireListener_$1_Count
      type: COUNTER
    - pattern : kafka.log<type=Log, name=Size, topic=(.+), partition=(.+)><>Value
      name: kafka_log_LogSize_Value
      type: GAUGE
      labels:
          topic: "$1"
          partition: "$2"
    # for jvm, on openjdk 1.8
    - pattern : java.nio<type=BufferPool, name=(.+)><>Count
      name: kafka_java_nio_BufferPool_$1_Count
      type: GAUGE
    - pattern : java.nio<type=BufferPool, name=(.+)><>MemoryUsed
      name: kafka_java_nio_BufferPool_$1_MemoryUsed
      type: GAUGE
    - pattern : java.nio<type=BufferPool, name=(.+)><>TotalCapacity
      name: kafka_java_nio_BufferPool_$1_TotalCapacity
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>TotalSwapSpaceSize
      name: kafka_java_lang_OperatingSystem_TotalSwapSpaceSize
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>TotalPhysicalMemorySize
      name: kafka_java_lang_OperatingSystem_TotalPhysicalMemorySize
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>SystemLoadAverage
      name: kafka_java_lang_OperatingSystem_SystemLoadAverage
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>SystemCpuLoad
      name: kafka_java_lang_OperatingSystem_SystemCpuLoad
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>ProcessCpuTime
      name: kafka_java_lang_OperatingSystem_ProcessCpuTime
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>ProcessCpuLoad
      name: kafka_java_lang_OperatingSystem_ProcessCpuLoad
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>OpenFileDescriptorCount
      name: kafka_java_lang_OperatingSystem_OpenFileDescriptorCount
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>MaxFileDescriptorCount
      name: kafka_java_lang_OperatingSystem_MaxFileDescriptorCount
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>FreeSwapSpaceSize
      name: kafka_java_lang_OperatingSystem_FreeSwapSpaceSize
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>FreePhysicalMemorySize
      name: kafka_java_lang_OperatingSystem_FreePhysicalMemorySize
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>CommittedVirtualMemorySize
      name: kafka_java_lang_OperatingSystem_CommittedVirtualMemorySize
      type: GAUGE
    - pattern : java.lang<type=OperatingSystem><>AvailableProcessors
      name: kafka_java_lang_OperatingSystem_AvailableProcessors
      type: GAUGE
    - pattern : java.lang<type=GarbageCollector, name=(.+)><>CollectionTime
      name: kafka_java_lang_GarbageCollector_CollectionTime
      type: GAUGE
      labels:
          name: "$1"
    - pattern : java.lang<type=GarbageCollector, name=(.+)><>CollectionCount
      name: kafka_java_lang_GarbageCollector_CollectionCount
      type: GAUGE
      labels:
          name: "$1"
```
