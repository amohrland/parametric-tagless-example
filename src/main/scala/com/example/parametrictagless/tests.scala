package com.example

import cats.Show
import cats.data.Kleisli
import cats.effect._
import cats.implicits._

object tests {

  def outputProg[I:Show](algebras: KafkaAlgebras[I,String]): IO[Unit] = {
    val out = Impls.outputDebugger(algebras)
    import out._

    for {
      _ <- write("string is output type")
      _ <- commit
    } yield ()
  }

  def inputProg[I:Show](algebras: KafkaAlgebras[I,String]): Kleisli[IO, I, String] = {
    val in = Impls.inputDebugger(algebras)
    import in._

    for {
      i <- read
      _ <- commit
    } yield i.show

  }

  def useBoth[I:Show](algebras: KafkaAlgebras[I,String]): Kleisli[IO, I, String] =
    for {
      i <- inputProg(algebras)
      _ <- Kleisli.liftF(outputProg(algebras))
    } yield i

  def mixAndMatch[I:Show](algebras: KafkaAlgebras[I,String]): Kleisli[IO, I, String] = {
    val in = Impls.inputDebugger(algebras)
    import in.{commit => commitInput, _}
    val out = Impls.outputDebugger(algebras)
    import out.{commit => commitOutput, _}

    for {
      i <- read
      _ <- commitInput
      _ <- Kleisli.liftF(write("testo"))
      _ <- Kleisli.liftF(commitOutput)
    } yield i.show
  }

  def allTogether[I:Show](algebras: KafkaAlgebras[I,String]): Kleisli[IO, I, String] =
    for {
      i <- useBoth(algebras)
      _ <- mixAndMatch(algebras)
    } yield i.show
}