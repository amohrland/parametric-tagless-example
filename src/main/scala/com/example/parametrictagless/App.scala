package com.example

import cats.implicits._
import cats.effect._
import fs2._

object App extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] = {

    val intDependency = 10
    val intStringAlgebras = new KafkaAlgebras[Int,String] {}
    val progWithIntDep: IO[String] = tests.allTogether(intStringAlgebras).run(intDependency)

    val stringDependency = "string_dep"
    val stringStringAlgebras = new KafkaAlgebras[String,String] {}
    val progWithStringDep: IO[String] = tests.allTogether(stringStringAlgebras).run(stringDependency)

    for {
      _ <- Stream.eval(progWithIntDep)
      _ <- Stream.eval(progWithStringDep)
    } yield StreamApp.ExitCode.fromInt(0)
  }
}

