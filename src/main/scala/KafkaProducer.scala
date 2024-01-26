package com.eventloopsoftware


import com.eventloopsoftware.avro.{MonkeyAvro, PersonAvro}
import zio.*
import zio.kafka.producer.Producer
import zio.kafka.serde.Serde
import zio.stream.ZStream

import java.util.UUID


object KafkaProducer {

  //   serializer without schema registry
  val serializer = AvroSerde.AvroSerializer[MonkeyAvro](MonkeyAvro.getClassSchema)

  // serializer with schema registry
  val serializerSchemaRegistry = KafkaAvroSerde.AvroSerializer[PersonAvro]()

  def producer(topic: String): ZStream[Producer, Throwable, Nothing] =
    ZStream
      .repeatZIO(Random.nextIntBetween(0, Int.MaxValue))
      .schedule(Schedule.fixed(2.seconds))
      .mapZIO { random =>
        Producer.produce[Any, Long, PersonAvro](
          topic = topic,
          key = random % 4,
          value = PersonAvro(
            UUID.randomUUID(),
            java.math.BigDecimal(random),
            "w00t",
            java.math.BigDecimal(23),
            "yeahhhh!"
          ),
          keySerializer = Serde.long,
          valueSerializer = serializerSchemaRegistry
        )
      }
      .drain

}
