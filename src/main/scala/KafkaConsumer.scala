package com.eventloopsoftware


import com.eventloopsoftware.avro.{MonkeyAvro, PersonAvro}
import zio.*
import zio.kafka.consumer.{Consumer, Subscription}
import zio.kafka.serde.Serde
import zio.stream.ZStream

object KafkaConsumer {

  // deserializer without schema registry
  private val deserializer = AvroSerde.AvroDeserializer[MonkeyAvro](MonkeyAvro.getClassSchema)

  // deserializer with schema registry
  val deserializerSchemaRegistry = KafkaAvroSerde.AvroDeserializer[PersonAvro]()

  def consume(topic: String): ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .plainStream(
        subscription = Subscription.topics(topic),
        keyDeserializer = Serde.long,
        valueDeserializer = deserializerSchemaRegistry)
      .tap(r => Console.printLine(s"consuming: ${r.value}"))
      .map(r => r.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .drain

}
