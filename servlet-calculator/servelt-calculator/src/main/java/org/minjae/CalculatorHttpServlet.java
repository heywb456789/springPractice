package org.minjae;

import org.minjae.calculator.Calculator;
import org.minjae.calculator.PositiveNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * packageName       : org.minjae
 * fileName         : CalculatorHttpServlet
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
public class CalculatorHttpServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(CalculatorServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int operand1 = Integer.parseInt(request.getParameter("operand1"));
                String operator = request.getParameter("operator");
                int operand2 = Integer.parseInt(request.getParameter("operand2"));

                int result = Calculator.calculate(new PositiveNumber(operand1), operator , new PositiveNumber(operand2));

                PrintWriter writer = response.getWriter();
                writer.println(result);
    }
}
