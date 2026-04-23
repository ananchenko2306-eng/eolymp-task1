package currencyconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService service = new ExchangeService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        try {
            ExchangeResponse res = service.performExchange(from, to, amount);
            mapper.writeValue(response.getWriter(), res);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ExchangeResponse errorRes = new ExchangeResponse("error", e.getMessage());
            mapper.writeValue(response.getWriter(), errorRes);

            service.saveToLog(new ExchangeOperation(from, to, amount, e.getMessage()));
        }
    }
}