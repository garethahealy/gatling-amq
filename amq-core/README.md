amq-core
=================
The project works by 'JMSReplyTo'. If the code base pushes onto another queue as per below, then we need to change it so the message bounces back.

#Original 
<route id="bridgeTest">
    <from uri="activemq:inbound"/>
    <log message="Body == ${body}"/>
    <to uri="activemq:outbound"/>
</route>

#Change 
<route id="jmsReplyToTest">
    <from uri="activemq:inbound"/>
    <log message="Body == ${body}"/>
</route>
