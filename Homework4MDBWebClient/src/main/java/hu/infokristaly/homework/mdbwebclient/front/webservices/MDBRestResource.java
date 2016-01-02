package hu.infokristaly.homework.mdbwebclient.front.webservices;

import hu.infokristaly.homework.mdbwebclient.front.service.Homework4MDBWebClient;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/rs/MyRESTMessage")
@Stateless
public class MDBRestResource {

    private final static String msg = "Jó napot!";
    
    @GET()
    @Produces("text/plain")
    public String sayHello() {
        try{  
            InitialContext ic = new InitialContext();  
            Object service = ic.lookup("java:/global/Homework4MDBWebClient/Homework4MDBWebClient");
            if ((service != null) && (service instanceof Homework4MDBWebClient)) {
                System.out.println("service:" + service);
                ((Homework4MDBWebClient)service).sendMessage(msg);
            } else {
                System.out.println("service is null");
            }
          } catch(Throwable t){
              System.out.println("service lookup failed");
          }  
        return msg;
    }
}
