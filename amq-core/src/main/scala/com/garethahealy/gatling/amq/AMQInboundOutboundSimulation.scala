package com.garethahealy.gatling.amq

import javax.jms._

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.jms.Predef._

import org.apache.activemq.jndi.ActiveMQInitialContextFactory

class AMQInboundOutboundSimulation extends Simulation {

    val jmsConfig = jms
        .connectionFactoryName("ConnectionFactory")
        .url("tcp://localhost:61616")
        .credentials("admin", "admin")
        .contextFactory(classOf[ActiveMQInitialContextFactory].getName)
        .listenerCount(20)
        .usePersistentDeliveryMode

    val scn = scenario("Inbound 2minutes of 5 messages").repeat(1) {
        exec(jms("Inbound Data Request").reqreply
            .queue("inbound")
            .replyQueue("outbound")
            .textMessage("<inboundRequest/>")
            .check(simpleCheck(checkBodyTextCorrect))
        )
    }

    setUp(scn.inject(rampUsersPerSec(1) to 5 during (2 minutes)))
        .protocols(jmsConfig)

    def checkBodyTextCorrect(m: Message) = {
        m match {
            case `m` => true
            case _ => false
        }
    }
}
