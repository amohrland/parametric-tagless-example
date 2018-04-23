package com.example

import cats.Show
import cats.data.Kleisli
import cats.effect.{Effect, IO}

object Impls {
  def outputDebugger[I,O](algebras: KafkaAlgebras[I,O]): algebras.Output[IO] = new algebras.Output[IO] {
    override def write(o: O): IO[Unit] = IO(println(s"Output.write($o)"))
    override def commit: IO[Unit] = IO(println("Output.commit()"))
  }

  def inputDebugger[I: Show,O](algebras: KafkaAlgebras[I,O]): algebras.Input[Kleisli[IO,I,?]] = new algebras.Input[Kleisli[IO,I,?]] {
    override def read: Kleisli[IO,I,I] = Kleisli { i => IO { println(s"Input.read() = $i"); i } }
    override def commit: Kleisli[IO,I,Unit] = Kleisli { i => IO { println(s"Input.commit() = $i") } }
  }
}
