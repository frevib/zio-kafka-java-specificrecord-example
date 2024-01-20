package com.eventloopsoftware

import com.aap.personkafka.PersonAvro
import zio.kafka.producer.Producer
import zio.kafka.serde.Serde
import zio.stream.ZStream
import zio.*

import java.util.UUID


object KafkaProducer {
  val serializer = AvroSerde.AvroSerializer[PersonAvro](PersonAvro.getClassSchema)

  val producer: ZStream[Producer, Throwable, Nothing] =
    ZStream
      .repeatZIO(Random.nextIntBetween(0, Int.MaxValue))
      .schedule(Schedule.fixed(2.seconds))
      .mapZIO { random =>
        Producer.produce[Any, Long, PersonAvro](
          topic = "random",
          key = random % 4,
          value = PersonAvro(
            UUID.randomUUID(),
            java.math.BigDecimal(random),
            "w00t",
            java.math.BigDecimal(23)
          ),
          keySerializer = Serde.long,
          valueSerializer = serializer
        )
      }
      .drain

}
