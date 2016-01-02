/**
 * 
 */
package hu.infokristaly.homework.mdbserver.back.mdbs;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * @author pzoli
 * EAP/bin add-user.bat (add [quickstartUser] with [quickstart!1Password] and [guest] group)
 * http://stackoverflow.com/questions/17936440/accessing-httpsession-from-httpservletrequest-in-a-web-socket-serverendpoint
 * 
 */
@MessageDriven(name = "TestMessageConsumer", activationConfig = { @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:jboss/exported/jms/queue/test"), @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "AUTO_ACKNOWLEDGE") })
public class TestMessageConsumer implements MessageListener {
    @Inject
    private Logger log;
    
    @Resource(lookup = "java:jboss/exported/jms/topic/testTopic")
    private Topic testTopic;

    @Resource(lookup = "java:/JmsXA")
    private ConnectionFactory topicConnectionFactory;

    private Connection connection = null;
    private Session session = null;
    private MessageProducer producer = null;
    
    public TestMessageConsumer() {
        
    }
    
    @PostConstruct
    public void init() {
        log.info("TestMessageConsumer created");
        try {

            connection = topicConnectionFactory.createConnection();
            
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(testTopic);
             
            connection.start();
            
        } catch (JMSException exc) {
            log.warning(exc.getMessage());
        }
    }

    @Override
    public void onMessage(final Message msg) {
        if (msg instanceof TextMessage) {
            final TextMessage tmp = (TextMessage) msg;
            try {
                log.info(String.format("received textmessage: %s", tmp.getText()));

                if (session == null) {
                    init();
                }
                TextMessage message = session.createTextMessage(((TextMessage) msg).getText());
                producer.send(message);
                
            } catch (JMSException exc) {
                log.warning(exc.getMessage());
            }
        } else {
            log.warning("got unknown messagetype");
        }
    }
}
