import java.util.Properties;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;

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
public class Homework4MDBClient {

    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "java:/jms/queue/test";
    private static final String DEFAULT_USERNAME = "quickstartUser";
    private static final String DEFAULT_PASSWORD = "quickstart!1Password";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";

    private static final String PORT = "8080";
    private static final String SERVER = "dell-wifi";
    private static final String PKG_PREFIX = "org.jboss.ejb.client.naming";
    
    private static final Logger log = Logger.getGlobal();

    private static Context initContext() throws NamingException {
        final Properties env = new Properties();
        
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put("jboss.naming.client.ejb.context", true);
        env.put(Context.PROVIDER_URL, "http-remoting://" + SERVER + ":" + PORT);
        env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
        env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));        
        //env.put(Context.URL_PKG_PREFIXES, PKG_PREFIX);
        env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
        env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");
        env.put("endpoint.name", "client-endpoint");
        
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

    private static Destination createDestination(Context context) throws NamingException {
        String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
        log.info("Attempting to acquire destination \"" + destinationString + "\"");
        Destination destination = (Destination) context.lookup(destinationString);
        log.info("Found destination \"" + destinationString + "\" in JNDI");
        return destination;
    }

    @SuppressWarnings("static-access")
    public static Options getOprions() {
        Options options = new Options();
        options.addOption(OptionBuilder.isRequired(true).withArgName("message").withLongOpt("message").hasArg(true).withDescription("message to send").create());
        return options;
    }

    public static void sendMessage(String msg) {
        MessageProducer producer = null;
        Destination destination = null;

        TextMessage message = null;
        Context context = null;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;

        try {

            context = initContext();
            connectionFactory = createFactory(context);
            destination = createDestination(context);

            // Create the JMS connection, session, producer, and consumer
            connection = connectionFactory.createConnection(System.getProperty("username", DEFAULT_USERNAME), System.getProperty("password", DEFAULT_PASSWORD));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            producer = session.createProducer(destination);

            connection.start();

            log.info("Sending messages with content: " + msg);

            // Send the specified number of messages
            message = session.createTextMessage(msg);
            producer.send(message);

        } catch (final JMSException exc) {
            log.severe(exc.getMessage());
            exc.printStackTrace();
        } catch (NamingException exc) {
            log.severe(exc.getExplanation());
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
        Options options = getOprions();
        try {
            CommandLine commandLine = null;
            Parser parser = new GnuParser();

            commandLine = parser.parse(options, args);
            String msg = (String) commandLine.getOptionValue("message");
            sendMessage(msg);
        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar Homework4MDBClient.jar", options);
        }
    }

}
