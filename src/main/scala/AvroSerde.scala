package com.eventloopsoftware

import org.apache.avro.Schema
import org.apache.avro.io.{BinaryEncoder, DecoderFactory, EncoderFactory}
import org.apache.avro.specific.{SpecificDatumReader, SpecificDatumWriter, SpecificRecord}
import org.apache.kafka.common.header.Headers
import zio.{RIO, ZIO}
import zio.kafka.serde.{Deserializer, Serializer}

import java.io.ByteArrayOutputStream

object AvroSerde {

  case class AvroSerializer[T <: SpecificRecord](schema: Schema) extends Serializer[Any, T] {
    val datumWriter: SpecificDatumWriter[T] = new SpecificDatumWriter[T](schema)
    val encoderFactory: EncoderFactory = EncoderFactory.get()
    val reusableEncoder: BinaryEncoder = encoderFactory.binaryEncoder(ByteArrayOutputStream(0), null)

    override def serialize(topic: String, headers: Headers, data: T): RIO[Any, Array[Byte]] =
      ZIO.attempt {
        val baos = ByteArrayOutputStream()
        val binaryEncoder = encoderFactory.binaryEncoder(baos, reusableEncoder)
        val result = datumWriter.write(data, binaryEncoder)
        binaryEncoder.flush()

        baos.toByteArray
      }
  }

  case class AvroDeserializer[T <: SpecificRecord](schema: Schema) extends Deserializer[Any, T] {
    val datumReader = new SpecificDatumReader[T](schema)
    val decoderFactory = DecoderFactory.get()
    val reusableDecoder = decoderFactory.binaryDecoder(new Array[Byte](0), null)


    override def deserialize(topic: String, headers: Headers, avroBytes: Array[Byte]): RIO[Any, T] =
      ZIO.attempt {
        val binaryDecoder = decoderFactory.binaryDecoder(avroBytes, reusableDecoder)
        // check reuse of T
        val result: T = datumReader.read(null.asInstanceOf[T], binaryDecoder)

        result
      }
  }

}
