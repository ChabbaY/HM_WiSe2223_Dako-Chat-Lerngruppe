package edu.hm.dako.api.servlet;

import edu.hm.dako.api.store.DataBaseController;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * default REST endpoint
 *
 * @author Linus Englert
 */
public class HelloWorldServlet extends HttpServlet {
    /**
     * message to be displayed as title
     */
    private String msg;

    @Override
    public void init() {
        msg = "Hello World!";
        DataBaseController controller = DataBaseController.getInstance();
        controller.init();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<h1>" + msg + "</h1>");
        out.println("<p>" + "S\u00FC\u00FC!" + "</p>");
    }
}