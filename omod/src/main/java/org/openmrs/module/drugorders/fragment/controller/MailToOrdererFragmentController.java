/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.drugorders.fragment.controller;

import java.text.ParseException;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.drugorders.api.drugordersService;
import org.openmrs.module.drugorders.drugorders;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author harini-parthasarathy
 */
public class MailToOrdererFragmentController {
    
    protected UserContext userContext;
    
    public void controller(FragmentModel model, @RequestParam("patientId") Patient patient,
                            @RequestParam(value = "pharmaGroupAction", required = false) String groupAction,
                            @RequestParam(value = "groupComments", required = false) String groupComments,
                            @RequestParam(value = "groupCheckBox", required=false) long[] groupCheckBox,
                            @RequestParam(value = "ordererName", required = false) String ordererName,
                            @RequestParam(value = "orderNumber", required = false) String orderNumber,
                            @RequestParam(value = "orderName", required = false) String orderName) throws ParseException{
       
        model.addAttribute("groupAction", groupAction);
        model.addAttribute("ordererName", ordererName);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("orderName", orderName);
        model.addAttribute("patientID", patient.getPatientId());
        model.addAttribute("patientName", patient.getGivenName()+" "+patient.getFamilyName());
        
        String sender = "";
        String recipient = "";        
        String orderList = "";    
        String drugNames = "";
        String orderDetails = "";
        
        /*
          If the check-boxes corresponding to one or more orders are checked to be put on hold or discarded,
          fetch the details of the selected orders to be updated in the mail fragment.
        */
        for(int i=0;i<groupCheckBox.length;i++){   
            // Retrieve the ID of each order that is saved in the check-box corresponding to the order
            int orderID = Integer.parseInt(Long.toString(groupCheckBox[i]));
            // Store the string of order IDs.
            orderList = orderList.concat(Long.toString(groupCheckBox[i])+" ");
            // Retrieve the corresponding order record.
            Order order = Context.getOrderService().getOrder(orderID);
            // Retrieve the corresponding drug order record.
            drugorders drugorder = Context.getService(drugordersService.class).getDrugOrderByOrderID(orderID);
            // Store the string of the name of the drugs.
            drugNames = drugNames.concat(drugorder.getDrugName().getDisplayString().toUpperCase()+";");
            // Store the details of the selected orders.
            orderDetails = orderDetails.concat("Order ID: "+Integer.toString(drugorder.getOrderId())+"%0ADrug: "+drugorder.getDrugName().getDisplayString().toUpperCase()+"%0AStart Date: "+drugorder.getStartDate().toString()+"%0A%0A");
            // Set recipient of the mail to be the orderer.
            if(recipient.equals(""))
                recipient = order.getOrderer().getName();
        }
        
        // Sender of the email is the given user (Pharmacist)
        userContext = Context.getUserContext();
        if(sender.equals(""))
            sender = userContext.getAuthenticatedUser().getGivenName() + " " + userContext.getAuthenticatedUser().getFamilyName();
        
        model.addAttribute("sender", sender);
        model.addAttribute("recipient", recipient);
        model.addAttribute("orderList", orderList);
        model.addAttribute("drugNames", drugNames);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("groupComments", groupComments);
    }
}
