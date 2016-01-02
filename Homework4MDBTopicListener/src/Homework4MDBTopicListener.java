import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * 
 */

/**
 * @author pzoli
 *         https://github.com/jboss-developer/jboss-eap-quickstarts/blob/jdf
 *         -2.1.2
 *         .Final/helloworld-jms/src/main/java/org/jboss/as/quickstarts/jms
 *         /HelloWorldJMSClient.java
 * 
 */
public class Homework4MDBTopicListener implements MessageListener {

    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_TOPIC = "java:/jms/topic/testTopic";//java:jboss/exported/
    private static final String DEFAULT_USERNAME = "quickstartUser";
    private static final String DEFAULT_PASSWORD = "quickstart!1Password";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://dell-wifi:8080"; //remote://localhost:4447 JBoss AS

    private static final Logger log = Logger.getGlobal();

    private static Context initContext() throws NamingException {
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
        env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
        env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
        Context ctx = new InitialContext(env);
        return ctx;
    }

    private static ConnectionFactory createFactory(Context context) throws NamingException {
        // Perform the JNDI lookups
        String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
        log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
        log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");
        return connectionFactory;
    }

    private static Topic createTopic(Context context) throws NamingException {
        String topicString = System.getProperty("topic", DEFAULT_TOPIC);
        log.info("Attempting to acquire topic \"" + topicString + "\"");
        Topic topic = (Topic) context.lookup(topicString);
        log.info("Found destination \"" + topicString + "\" in JNDI");
        return topic;
    }

    @SuppressWarnings("static-access")
    public static Options getOprions() {
        Options options = new Options();
        options.addOption(OptionBuilder.isRequired(false).withArgName("message").withLongOpt("message").hasArg(true).withDescription("message to send").create());
        return options;
    }

    private void receiveMessage() {
        Context context = null;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;

        MessageConsumer consumer = null;

        try {

            context = initContext();
            connectionFactory = createFactory(context);
            Topic topic = createTopic(context);

            connection = connectionFactory.createConnection(System.getProperty("username", DEFAULT_USERNAME), System.getProperty("password", DEFAULT_PASSWORD));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            consumer = session.createConsumer(topic);
            consumer.setMessageListener(this);
            connection.start();
            
            Scanner keyIn = new Scanner(System.in);
            System.out.print("JMS Server listening. Type a Key + CR to exit\n");
            keyIn.next();
        } catch (final JMSException exc) {
            log.info(exc.getMessage());
        } catch (NamingException exc) {
            log.info(exc.getMessage());
        } finally {
            try {
                if (null != session) {
                    session.close();
                }
            } catch (JMSException exc) {
                log.severe(exc.getMessage());
            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException exc) {
                log.severe(exc.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Homework4MDBTopicListener client = new Homework4MDBTopicListener();
        client.receiveMessage();
    }

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                log.info("Received message to topic: "+((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
