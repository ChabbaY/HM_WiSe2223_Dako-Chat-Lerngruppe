package edu.hm.dako.api.servlet;

import edu.hm.dako.api.data.PDU;
import edu.hm.dako.api.store.DataBaseController;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;

/**
 * REST endpoints to store and load PDUs of the audit log server
 *
 * @author Linus Englert
 */
public class PDUServlet extends HttpServlet {
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
     * GET localhost:8080/api/pdus or localhost:8080/api/pdus?id=3<br/>
     * parameter: id<br/>
     * no id or id=0 is a list-request, a specific id >= 1 is a single-item-request<br/>
     * response can be http200 (ok) with value or http404 (not found), http400 (bad request) if id not a number
     *
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Headers.setHeaders(response);
        int id = evaluateId(request, response);

        if (id == 0) {//get all (id starts from 1)
            PDU[] pdus = controller.selectAllPDU();

            response.getOutputStream().println(JSONObject.wrap(pdus).toString());
        } else {//get one
            PDU pdu = controller.selectPDU(id);

            if (pdu == null) {//404
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.getOutputStream().println(JSONObject.wrap(pdu).toString());
        }
    }

    /**
     * POST localhost:8080/api/pdus<br/>
     * parameter: pduType, username, clientThread, serverThread, auditTime, content<br/>
     * response can be http200 (ok), http400 (bad request) if a parameter is too long, http500 (server error)
     *
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Headers.setHeaders(response);
        String[] input = getPDU(request, response);
        if (input == null) return;

        if (!controller.insertPDU(new PDU(0, input[0], input[1], input[2], input[3], input[4], input[5]))) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUT localhost:8080/api/pdus<br/>
     * parameter: id, pduType, username, clientThread, serverThread, auditTime, content<br/>
     * response can be http200 (ok), http400 (bad request) if a parameter is too long or id not a number,
     * http404 (not found) if id doesn't exist, http500 (server error)
     *
     * @param request the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     */
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Headers.setHeaders(response);
        int id = evaluateId(request, response);

        String[] input = getPDU(request, response);
        if (input == null) return;

        PDU pdu = controller.selectPDU(id);

        if (pdu != null) {
            if (!controller.updatePDU(id, new PDU(0, input[0], input[1], input[2], input[3], input[4], input[5]))) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * DELETE localhost:8080/api/pdus<br/>
     * parameter: id<br/>
     * response can be http200 (ok), http400 (bad request) if id not a number, http500 (server error)
     *
     * @param request the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Headers.setHeaders(response);
        int id = evaluateId(request, response);

        if (!controller.deletePDU(id)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * checks if id is number and returns it, or bad request if parsing went wrong
     *
     * @param request object that contains the request the client made of the servlet
     * @param response object that contains the response the servlet returns to the client
     * @return parsed id
     */
    private int evaluateId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = 0;
        try {
            String idInput = request.getParameter("id");
            if (idInput != null) id = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            response.getOutputStream().println("wrong id format: integer expected");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return id;
    }

    /**
     * puts all pdu parameters in an array, bad request if wrong parameters
     *
     * @param request object that contains the request the client made of the servlet
     * @param response object that contains the response the servlet returns to the client
     * @return parsed strings in array
     */
    private String[] getPDU(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pduType = request.getParameter("pduType");
        String username = request.getParameter("username");
        String clientThread = request.getParameter("clientThread");
        String serverThread = request.getParameter("serverThread");
        String auditTime = request.getParameter("auditTime");
        String content = request.getParameter("content");

        if ((pduType == null) || (username == null) || (clientThread == null) || (serverThread == null) ||
                (auditTime == null) || (content == null) ||
                (pduType.length() > 100) || (username.length() > 100) || (clientThread.length() > 100) ||
                (serverThread.length() > 100) || (auditTime.length() > 100) || (content.length() > 100)) {
            response.getOutputStream().println("max parameter size of 100 chars exceeded ");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        return new String[] {pduType, username, clientThread, serverThread, auditTime, content};
    }
}