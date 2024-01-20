package com.eventloopsoftware

import com.aap.personkafka.PersonAvro
import org.apache.avro.Schema
import org.apache.avro.io.{BinaryEncoder, DecoderFactory, EncoderFactory}
import org.apache.avro.specific.{SpecificDatumReader, SpecificDatumWriter}
import org.apache.kafka.common.header.Headers
import zio.{RIO, ZIO}
import zio.kafka.serde.{Deserializer, Serializer}

import java.io.ByteArrayOutputStream

object AvroSerde {

  case class AvroSerializer() extends Serializer[Any, PersonAvro] {
    val datumWriter: SpecificDatumWriter[PersonAvro] = new SpecificDatumWriter[PersonAvro](PersonAvro().getSchema)
    val encoderFactory: EncoderFactory = EncoderFactory.get()
    val encoder: BinaryEncoder = encoderFactory.binaryEncoder(ByteArrayOutputStream(0), null)

    override def serialize(topic: String, headers: Headers, personAvro: PersonAvro): RIO[Any, Array[Byte]] =
      ZIO.attempt {
        val baos = ByteArrayOutputStream()
        val encoderR = encoderFactory.binaryEncoder(baos, encoder)
        val result = datumWriter.write(personAvro, encoderR)
        encoderR.flush()

        baos.toByteArray
      }
  }

  case class AvroDeserializer() extends Deserializer[Any, PersonAvro] {
    val datumReader = new SpecificDatumReader[PersonAvro](PersonAvro().getSchema)
    val decoderFactory = DecoderFactory.get()
    val decoder = decoderFactory.binaryDecoder(new Array[Byte](0), null)


    override def deserialize(topic: String, headers: Headers, avroBytes: Array[Byte]): RIO[Any, PersonAvro] =
      ZIO.attempt {
        val decoderR = decoderFactory.binaryDecoder(avroBytes, decoder)
        // check reuse
        val result: PersonAvro = datumReader.read(null, decoderR)

        result
      }
  }

}
