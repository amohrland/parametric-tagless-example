package com.example

import cats.effect._

abstract class KafkaAlgebras[I,O] {
  abstract class Input[F[_]: Sync] {
    def read: F[I]
    def commit: F[Unit]
  }

  abstract class Output[F[_]: Sync] {
    def write(o: O): F[Unit]
    def commit: F[Unit]
  }

}

