package com.eventloopsoftware

import zio.*
import zio.kafka.consumer.{Consumer, Subscription}
import zio.kafka.serde.Serde
import zio.stream.ZStream

object KafkaConsumer {

  val deserializer: AvroSerde.AvroDeserializer = AvroSerde.AvroDeserializer()

  val consumer: ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .plainStream(
        subscription = Subscription.topics("random"),
        keyDeserializer = Serde.long,
        valueDeserializer = deserializer)
      .tap(r => Console.printLine(r.value))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .drain


  //    def de

}
