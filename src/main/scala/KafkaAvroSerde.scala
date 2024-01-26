package com.eventloopsoftware

import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.header.Headers
import zio.kafka.serde.{Deserializer, Serializer}
import zio.{RIO, ZIO}

import scala.jdk.CollectionConverters.MapHasAsJava

object KafkaAvroSerde {

  val kafkaProps = Map(
    "schema.registry.url" -> "http://localhost:8081",
    // SpecificDatumReader will be used for deserialization. This enables the use of SpecificRecord instead of GenericRecord
    KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> "true",
    // Support for logical types, e.g. Java BigDecimal
    KafkaAvroDeserializerConfig.AVRO_USE_LOGICAL_TYPE_CONVERTERS_CONFIG -> "true",
    //    SpecificData.get().addLogicalTypeConversion(new Conversions.DecimalConversion());
  )

  case class AvroSerializer[T <: SpecificRecord]() extends Serializer[Any, T] {
    val kafkaAvroSerializer = new KafkaAvroSerializer()
    kafkaAvroSerializer.configure(kafkaProps.asJava, false)

    override def serialize(topic: String, headers: Headers, data: T): RIO[Any, Array[Byte]] =
      ZIO.attempt(kafkaAvroSerializer.serialize(topic, data))
  }

  case class AvroDeserializer[T <: SpecificRecord]() extends Deserializer[Any, T] {
    private val kafkaAvroDeserializer = new KafkaAvroDeserializer()
    kafkaAvroDeserializer.configure(kafkaProps.asJava, false)

    override def deserialize(topic: String, headers: Headers, data: Array[Byte]): RIO[Any, T] =
      ZIO.attempt(kafkaAvroDeserializer.deserialize(topic, data).asInstanceOf[T])
  }

}
