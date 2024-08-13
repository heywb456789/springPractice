package org.minjae;

import org.minjae.calculator.Calculator;
import org.minjae.calculator.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * packageName       : org.minjae
 * fileName         : CalculatorServlet
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
@WebServlet("/calculate")
public class CalculatorServlet implements Servlet {

    private Logger logger = LoggerFactory.getLogger(CalculatorServlet.class);

    private ServletConfig config;

    /**
     * Life Cycle 관련 init ,service , destroy
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.info("init");
        this.config = servletConfig;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        logger.info("service");
        int operand1 = Integer.parseInt(request.getParameter("operand1"));
        String operator = request.getParameter("operator");
        int operand2 = Integer.parseInt(request.getParameter("operand2"));

        int result = Calculator.calculate(new PositiveNumber(operand1), operator , new PositiveNumber(operand2));

        PrintWriter writer = response.getWriter();
        writer.println(result);
    }

    @Override
    public void destroy() {
        logger.info("destroy");
        //resource Release
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }



    @Override
    public String getServletInfo() {
        return "";
    }


}
