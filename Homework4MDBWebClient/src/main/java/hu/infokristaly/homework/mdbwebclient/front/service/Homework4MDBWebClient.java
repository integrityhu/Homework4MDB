package hu.infokristaly.homework.mdbwebclient.front.service;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author pzoli
 *
 */
@Stateless
public class Homework4MDBWebClient {

    // @Resource(lookup = "java:/ConnectionFactory")
    @Resource(lookup = "java:/JmsXA")
    private ConnectionFactory connectionFactory;
 
    @Resource(lookup = "java:jboss/exported/jms/queue/test")
    private Queue testQueue;
 
    @Inject
    private Logger log;
 
    public String sendMessage(String msg) {
        Connection connection = null;
        Session session = null;
        try {
            log.info("sending message");
 
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer publisher = session.createProducer(testQueue);
 
            final TextMessage message = session.createTextMessage(msg);
            publisher.send(message);
 
            log.info("message sent");
        } catch (final JMSException exc) {
            log.severe(exc.getMessage());
        } finally {
            try {
                if (null != session) {
                    session.close();
                }
            } catch (JMSException exc) {
                log.warning(exc.getMessage());
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException exc) {
                log.warning(exc.getMessage());
            }
        }
        return "test";
    }
    
}
