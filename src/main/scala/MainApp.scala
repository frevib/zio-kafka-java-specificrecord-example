package com.eventloopsoftware


import zio.*
import zio.kafka.consumer.{Consumer, ConsumerSettings}
import zio.kafka.producer.{Producer, ProducerSettings}


object MainApp extends ZIOAppDefault {

  val topic = "random12"
  def producerLayer =
    ZLayer.scoped(
      Producer.make(
        settings = ProducerSettings(List("localhost:9092"))
      )
    )

  def consumerLayer =
    ZLayer.scoped(
      Consumer.make(
        settings = ConsumerSettings(List("localhost:9092")).withGroupId("group")
      )
    )

  val myApp: ZIO[Any, Throwable, Unit] = (for {
    _ <- Console.printLine("Hello, World!")
    _ <- KafkaProducer.producer(topic).merge(KafkaConsumer.consume(topic)).runDrain.provide(producerLayer, consumerLayer)
  } yield ())


  override def run = myApp
}