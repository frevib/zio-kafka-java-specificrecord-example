package com.eventloopsoftware


import zio.*
import zio.kafka.consumer.{Consumer, ConsumerSettings}
import zio.kafka.producer.{Producer, ProducerSettings}


object MainApp extends ZIOAppDefault {

  def producerLayer =
    ZLayer.scoped(
      Producer.make(
        settings = ProducerSettings(List("localhost:9092"))
      )
    )

  def consumerLayer =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(List("localhost:9092")).withGroupId("group")
      )
    )

  val myApp: ZIO[Any, Throwable, Unit] = (for {
    _ <- Console.printLine("Hello, World!")
    _ <- KafkaProducer.producer.merge(KafkaConsumer.consumer).runDrain.provide(producerLayer, consumerLayer)
  } yield ())


  override def run = myApp
}