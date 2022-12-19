package edu.hm.dako.api.servlet;

import edu.hm.dako.api.store.DataBaseController;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;

/**
 * REST endpoints with more detailed information (statistics) about pdu types
 *
 * @author Linus Englert
 */
public class PDUTypeServlet extends HttpServlet {
    /**
     * controller of database actions
     */
    DataBaseController controller;

    @Override
    public void init() {
        controller = DataBaseController.getInstance();
        controller.init();
    }

    /**
     * GET localhost:8080/api/pdus/types<br/>
     *
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Headers.setHeaders(response);
        int[] typeCount = controller.selectPDUTypeCount();
        response.getOutputStream().println(new JSONObject()
                .put("undefined", typeCount[0])
                .put("login", typeCount[1])
                .put("logout", typeCount[2])
                .put("chat", typeCount[3])
                .put("finish", typeCount[4]).toString());
    }
}