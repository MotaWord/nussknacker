package com.motaword.ipm.engine

import com.motaword.ipm.engine.translator.controller.TooFrequentTranslation
import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api._
import pl.touk.nussknacker.engine.api.exception.{EspExceptionHandler, ExceptionHandlerFactory}
import pl.touk.nussknacker.engine.api.process._
import pl.touk.nussknacker.engine.api.signal.ProcessSignalSender

class IPMProcessConfigCreator extends ProcessConfigCreator {

  override def customStreamTransformers(config: Config): Map[String, WithCategories[CustomStreamTransformer]] = {
    Map(
      "TooFrequentTranslation" -> WithCategories(new TooFrequentTranslation, "Translator")
    )
  }

  override def services(config: Config): Map[String, WithCategories[Service]] =
    Map.empty

  override def sourceFactories(config: Config): Map[String, WithCategories[SourceFactory[_]]] =
    Map.empty

  override def sinkFactories(config: Config): Map[String, WithCategories[SinkFactory]] =
    Map.empty

  override def listeners(config: Config): Seq[ProcessListener] =
    Nil

  //TODO: this does not work for Flink procsses -> as it is doesn't define restart strategy...
  override def exceptionHandlerFactory(config: Config): ExceptionHandlerFactory =
    ExceptionHandlerFactory.noParams(_ => EspExceptionHandler.empty)

  override def expressionConfig(config: Config) = ExpressionConfig(Map.empty, List.empty)

  override def buildInfo(): Map[String, String] = {
    val engineBuildInfo = pl.touk.nussknacker.engine.version.BuildInfo.toMap.map { case (k, v) => s"engine-$k" -> v.toString }
    engineBuildInfo ++ Map(
      "process-version" -> "0.1"
    )
  }

  override def signals(config: Config): Map[String, WithCategories[ProcessSignalSender]] =
    Map.empty


}
