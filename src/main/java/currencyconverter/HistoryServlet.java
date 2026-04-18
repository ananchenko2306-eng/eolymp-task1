package currencyconverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet("/api/history")
public class HistoryServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final List<ExchangeOperation> historyStorage = new CopyOnWriteArrayList<>();

    public static void addOperation(ExchangeOperation operation) {
        historyStorage.add(operation);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        mapper.writeValue(out, historyStorage);
    }
}