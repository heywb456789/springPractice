package org.minjae.calculator;

import org.minjae.CalculatorServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * packageName       : org.minjae.calculator
 * fileName         : CalculatorGenericServlet
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
public class CalculatorGenericServlet extends GenericServlet {

    private Logger logger = LoggerFactory.getLogger(CalculatorServlet.class);

        /**
         * Generic Servlet 추살 클래스는 필요한 기능만 오버라이드
         */

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
}
