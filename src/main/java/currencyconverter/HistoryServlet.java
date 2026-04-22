package currencyconverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/history")
public class HistoryServlet extends HttpServlet {
    private static final String LOG_PATH = System.getProperty("user.home") + "/java_logs/operations.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        File file = new File(LOG_PATH);
        List<ExchangeOperation> history = new ArrayList<>();

        if (file.exists()) {
            history = mapper.readValue(file, new TypeReference<List<ExchangeOperation>>() {});
        }

        out.print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(history));
        out.flush();
    }
}