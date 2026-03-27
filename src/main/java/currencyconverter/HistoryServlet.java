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

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    // Шлях до того самого файлу логів
    private static final String LOG_PATH = System.getProperty("user.home") + "/java_logs/operations.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Історія операцій</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f9; }");
        out.println("table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        out.println("th, td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; }");
        out.println("th { background-color: #007bff; color: white; }");
        out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
        out.println("</style></head><body>");

        out.println("<h2> Історія останніх обмінів</h2>");

        File file = new File(LOG_PATH);
        List<ExchangeOperation> history = new ArrayList<>();

        if (file.exists()) {
            // Читаємо список операцій з JSON-файлу
            history = mapper.readValue(file, new TypeReference<List<ExchangeOperation>>() {});
        }

        if (history.isEmpty()) {
            out.println("<p>Операцій ще не було. Зробіть свій перший обмін!</p>");
        } else {
            out.println("<table>");
            out.println("<tr><th>Час</th><th>Віддано</th><th>Отримано</th><th>Статус</th></tr>");

            // Виводимо список у зворотному порядку (найсвіжіші зверху)
            for (int i = history.size() - 1; i >= 0; i--) {
                ExchangeOperation op = history.get(i);
                out.println("<tr>");
                out.println("<td>" + op.getTime() + "</td>");
                out.println("<td>" + String.format("%.2f", op.getAmountGiven()) + " " + op.getFromCurrency() + "</td>");
                out.println("<td>" + String.format("%.2f", op.getAmountReceived()) + " " + op.getToCurrency() + "</td>");
                out.println("<td>" + op.getError() + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }

        out.println("<br><a href='index.html'>⬅ Повернутися до конвертації</a>");
        out.println("</body></html>");
    }
}