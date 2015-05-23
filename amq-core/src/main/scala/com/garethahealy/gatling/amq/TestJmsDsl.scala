package com.garethahealy.gatling.amq

import javax.jms._

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.jms.Predef._

import org.apache.activemq.jndi.ActiveMQInitialContextFactory

class TestJmsDsl extends Simulation {

    val jmsConfig = jms
        .connectionFactoryName("ConnectionFactory")
        .url("tcp://localhost:61616")
        .credentials("admin", "admin")
        .contextFactory(classOf[ActiveMQInitialContextFactory].getName)
        .listenerCount(1)
        .usePersistentDeliveryMode

    val scn = scenario("JMS DSL test").repeat(1) {
        exec(jms("req reply testing").reqreply
            .queue("inbound")
            .replyQueue("outbound")
            .textMessage("hello from gatling jms dsl")
            .check(simpleCheck(checkBodyTextCorrect))
        )
    }

    setUp(scn.inject(rampUsersPerSec(10) to 1000 during (2 minutes)))
        .protocols(jmsConfig)

    def checkBodyTextCorrect(m: Message) = {
        // this assumes that the service just does an "uppercase" transform on the text
        m match {
            case tm: TextMessage => true
            case _ => false
        }
    }
}
