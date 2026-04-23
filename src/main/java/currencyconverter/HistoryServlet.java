package currencyconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/history")
public class HistoryServlet extends HttpServlet {
    private final ExchangeService service = new ExchangeService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(response.getWriter(), service.getHistory());
        } catch (Exception e) {
            response.getWriter().print("[]");
        }
    }
}