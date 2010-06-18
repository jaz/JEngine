package org.jengine.browser;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QBrowser
{
	//private static final String CONNECTION_FACTORY = "ConnectionFactory";
	private static final String CONNECTION_FACTORY = "ConnectionFactory";
	private static final String PROVIDER_URL       = System.getProperty("jboss.bind.address");

        private static Context ctx;
        private static QueueConnectionFactory factory;
        private static QueueConnection connection;
        private static QueueSession session;
        private static QueueBrowser qBrowser;

	private static String defaultQueue = "queue/HL7QueueInLog";

        public QBrowser()
        {
        }

        private static Context getInitialContext() throws NamingException
	{
	        Properties env = new Properties();
	        env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		env.put("Context.PROVIDER_URL", PROVIDER_URL);
	        env.put("java.naming.provider.url.pkgs", "org.jnp.interfaces");
	        return new InitialContext(env);
	}

        public static void init(String q)
        {
                try
                {
                        System.out.println("Initialize : " + q);
			ctx = getInitialContext();
			factory = (QueueConnectionFactory)ctx.lookup(CONNECTION_FACTORY);
			connection = factory.createQueueConnection();
			session = (QueueSession)connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	                qBrowser = (QueueBrowser)session.createBrowser((Queue)ctx.lookup(q));
        	}
                catch (NamingException ne)
                {
                        System.out.println("init() NamingException : " + ne.getMessage());
			ne.printStackTrace();
                }
                catch (JMSException je)
                {
                        System.out.println("init() JMSException : " + je.getMessage());
                }
        }

        public static void main(String argv[])
        {
            if (argv.length > 0)
                init("queue/" + argv[0]);
/*
            else
                init(defaultQueue);
*/
            run();
        }

        private static void run()
        {
	        try
	        {
                        java.util.Enumeration e = qBrowser.getEnumeration();
                        for (int i=0 ; e.hasMoreElements() ; i++)
                        {
	                        TextMessage tMsg = (TextMessage)e.nextElement();
                                System.out.println("Q MSG : " + i);
                                System.out.println(tMsg.getText());
                                System.out.println("END Q MSG");
                        }
                        qBrowser.close();

	        }
	        catch(JMSException je)
	        {
                        System.out.println("main() JMSException : " + je.getMessage());
	        }
        }

}
