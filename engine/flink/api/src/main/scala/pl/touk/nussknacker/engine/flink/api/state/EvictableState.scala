package pl.touk.nussknacker.engine.flink.api.state

import java.lang

import org.apache.flink.api.common.state.{State, ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeutils.base.StringSerializer
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.streaming.api.operators._
import org.apache.flink.util.Collector
import pl.touk.nussknacker.engine.api.util.MultiMap

/*
  Constraints
  - only EventTime
  - keys are strings
 */
@Deprecated
abstract class EvictableState[In, Out] extends AbstractStreamOperator[Out]
    with OneInputStreamOperator[In, Out] with Triggerable[String, String] {

  var internalTimerService : InternalTimerService[String] = _

  var lastEventTimeForKey : ValueState[java.lang.Long] = _

  override def open() = {
    super.open()
    lastEventTimeForKey = getRuntimeContext.getState[java.lang.Long](new ValueStateDescriptor[java.lang.Long]("timers", classOf[java.lang.Long]))
    internalTimerService = getInternalTimerService("evictable-timers", new StringSerializer, this)
  }

  def getState: State

  override def onProcessingTime(timer: InternalTimer[String, String]) = {
    //FIXME: or maybe there should be an error??
  }

  override def onEventTime(timer: InternalTimer[String, String]) = {
    setCurrentKey(timer.getKey)
    val noNewerEventsArrived = lastEventTimeForKey.value() == timer.getTimestamp
    if (noNewerEventsArrived) {
      getState.clear()
      lastEventTimeForKey.update(null)
    }
  }


  protected final def setEvictionTimeForCurrentKey(time: Long) = {
    val key = getCurrentKey.toString
    //we don't delete former timer, because it's inefficient
    internalTimerService.registerEventTimeTimer(key, time)
    lastEventTimeForKey.update(time)
  }

}

abstract class EvictableStateFunction[In, Out, StateType] extends KeyedProcessFunction[String, In, Out] {

  protected var lastEventTimeForKey : ValueState[java.lang.Long] = _

  protected var state: ValueState[StateType] = _

  protected def stateDescriptor: ValueStateDescriptor[StateType]

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    lastEventTimeForKey = getRuntimeContext.getState[java.lang.Long](new ValueStateDescriptor[java.lang.Long]("timers", classOf[java.lang.Long]))
    state = getRuntimeContext.getState(stateDescriptor)
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, In, Out]#OnTimerContext, out: Collector[Out]): Unit = {
    val noNewerEventsArrived = lastEventTimeForKey.value() == timestamp
    if (noNewerEventsArrived) {
      state.clear()
      lastEventTimeForKey.update(null)
    }
  }

  protected def moveEvictionTime(offset: Long, ctx: KeyedProcessFunction[String, In, Out]#Context) : Unit= {
    val time = ctx.timestamp() + offset
    //we don't delete former timer, because it's inefficient
    ctx.timerService().registerEventTimeTimer(time)
    lastEventTimeForKey.update(time)
  }
}


abstract class TimestampedEvictableStateFunction[In, Out, StateType] extends EvictableStateFunction[In, Out, MultiMap[Long, StateType]] {

  override protected def moveEvictionTime(offset: Long, ctx: KeyedProcessFunction[String, In, Out]#Context): Unit = {
    super.moveEvictionTime(offset, ctx)
    state.update( stateValue.from(ctx.timestamp() - offset))
  }

  protected def stateValue: MultiMap[Long, StateType] = {
    Option(state.value()).getOrElse(MultiMap[Long, StateType](Ordering.Long))
  }

}

abstract class LatelyEvictableStateFunction[In, Out, StateType] extends KeyedProcessFunction[String, In, Out] {

  protected var latestEvictionTimeForKey : ValueState[java.lang.Long] = _

  protected var state: ValueState[StateType] = _

  protected def stateDescriptor: ValueStateDescriptor[StateType]

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    latestEvictionTimeForKey = getRuntimeContext.getState[java.lang.Long](new ValueStateDescriptor[java.lang.Long]("timers", classOf[java.lang.Long]))
    state = getRuntimeContext.getState(stateDescriptor)
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, In, Out]#OnTimerContext, out: Collector[Out]): Unit = {
    val latestEvictionTimeValue = latestEvictionTimeForKey.value()
    val noLaterEventsArrived = latestEvictionTimeValue == timestamp
    if (noLaterEventsArrived) {
      state.clear()
      latestEvictionTimeForKey.update(null)
    } else if (latestEvictionTimeValue != null) {
      ctx.timerService().registerEventTimeTimer(latestEvictionTimeValue)
    }
  }

  protected def moveEvictionTime(offset: Long, ctx: KeyedProcessFunction[String, In, Out]#Context) : Unit= {
    val time = ctx.timestamp() + offset
    val latestEvictionTimeValue = latestEvictionTimeForKey.value()
    val maxEvictionTime = if (latestEvictionTimeValue == null || time > latestEvictionTimeValue) {
      time
    } else {
      latestEvictionTimeValue.longValue()
    }
    if (latestEvictionTimeValue == null) {
      ctx.timerService().registerEventTimeTimer(maxEvictionTime)
    }
    latestEvictionTimeForKey.update(maxEvictionTime)
  }

}