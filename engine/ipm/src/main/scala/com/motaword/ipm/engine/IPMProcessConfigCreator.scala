package com.motaword.ipm.engine

import com.motaword.ipm.engine.translator.controller.TooFrequentTranslation
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.api._
import pl.touk.nussknacker.engine.api.exception.{EspExceptionHandler, EspExceptionInfo, ExceptionHandlerFactory}
import pl.touk.nussknacker.engine.api.process._
import pl.touk.nussknacker.engine.api.signal.ProcessSignalSender

class IPMProcessConfigCreator extends ProcessConfigCreator with LazyLogging {

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

  override def exceptionHandlerFactory(config: Config): ExceptionHandlerFactory = ExceptionHandlerFactory.noParams(md => new EspExceptionHandler {
    override def handle(exceptionInfo: EspExceptionInfo[_ <: Throwable]): Unit = logger.error("Error", exceptionInfo)
  })

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
