/**
 * 
 */
package hu.infokristaly.homework.mdbwebclient.front.controller;

import hu.infokristaly.homework.mdbwebclient.front.service.Homework4MDBWebClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author pzoli
 *
 */
@Named
@RequestScoped
public class MessageController {

    @Inject
    private Homework4MDBWebClient client;
    
    public void sendMessage(String msg){
        client.sendMessage(msg);
        System.out.println("msg sent");
    }
}
