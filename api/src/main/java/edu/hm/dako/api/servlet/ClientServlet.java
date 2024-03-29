package edu.hm.dako.api.servlet;

import edu.hm.dako.api.data.PDU;
import edu.hm.dako.api.store.DataBaseController;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;

/**
 * REST endpoints with more detailed information (statistics) about a client
 *
 * @author Linus Englert
 */
public class ClientServlet extends HttpServlet {
    /**
     * controller for database actions
     */
    DataBaseController controller;

    @Override
    public void init() {
        controller = DataBaseController.getInstance();
        controller.init();
    }

    /**
     * GET localhost:8080/api/pdus/clients<br/>
     * params: username
     *
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Headers.setHeaders(response);
        String username = request.getParameter("username");
        int chatMessages = controller.selectClientChatMessagesCount(username);

        PDU[] pdus = controller.selectPDU(username);

        String lastLogin = "";
        for (PDU pdu : pdus) {
            if (pdu.getPduType().equals("Login")) {
                lastLogin = pdu.getAuditTime();
                break;
            }
        }

        String lastLogout = "";
        for (PDU pdu : pdus) {
            if (pdu.getPduType().equals("Logout")) {
                lastLogout = pdu.getAuditTime();
                break;
            }
        }

        response.getOutputStream().println(new JSONObject()
                .put("username", username)
                .put("chatMessages", chatMessages)
                .put("lastLogin", lastLogin)
                .put("lastLogout", lastLogout)
                .put("pdus", pdus).toString());
    }
}