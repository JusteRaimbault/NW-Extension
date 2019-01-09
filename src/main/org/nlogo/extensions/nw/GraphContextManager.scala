// (C) Uri Wilensky. https://github.com/NetLogo/NW-Extension

package org.nlogo.extensions.nw

import org.nlogo.agent
import org.nlogo.agent.{AgentSet, Link, World}
import org.nlogo.api

trait GraphContextProvider {
  def getGraphContext(world: api.World): GraphContext
  def getWeightedGraphContext(world: api.World,weightVar: String): GraphContext
  def withTempGraphContext(gc: GraphContext)(f: () => Unit)
}


trait GraphContextManager extends GraphContextProvider {

  private var _graphContext: Option[GraphContext] = None

  override def getGraphContext(world: api.World): GraphContext = {
    val w = world.asInstanceOf[agent.World]
    val oldGraphContext = _graphContext
    _graphContext = _graphContext
      .map(_.verify(w))
      .orElse(Some(new GraphContext(w, w.turtles, w.links)))
    for {
      oldGC <- oldGraphContext
      newGC <- _graphContext
      if oldGC != newGC
    } oldGC.unsubscribe()
    _graphContext.get
  }

  override def getWeightedGraphContext(world: api.World,weightVar: String): GraphContext = {
    val w = world.asInstanceOf[agent.World]
    val oldGraphContext = _graphContext
    _graphContext = _graphContext
      .map(_.verify(w))
      .orElse(Some(new WeightedGraphContext(w, w.turtles, w.links,weightVar)))
    for {
      oldGC <- oldGraphContext
      newGC <- _graphContext
      if oldGC != newGC
    } oldGC.unsubscribe()
    _graphContext.get
  }

  def setGraphContext(gc: GraphContext) {
    _graphContext.foreach(_.unsubscribe())
    _graphContext = Some(gc)
  }

  def withTempGraphContext(gc: GraphContext)(f: () => Unit) {
    val currentContext = _graphContext
    _graphContext = Some(gc)
    f()
    gc.unsubscribe()
    _graphContext = currentContext
  }

  def clearContext() {
    _graphContext.foreach(_.unsubscribe())
    _graphContext = None
  }
}
