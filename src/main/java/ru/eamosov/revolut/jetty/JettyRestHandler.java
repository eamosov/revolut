package ru.eamosov.revolut.jetty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import ru.eamosov.revolut.rest.AbstractRestController;
import ru.eamosov.revolut.utils.ZonedDateTimeType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fluder on 27/05/2018.
 */
public class JettyRestHandler extends AbstractHandler {
    private final Map<String, AbstractRestController> controllers = new HashMap<>();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeType())
                                               .setPrettyPrinting()
                                               .create();


    public JettyRestHandler() {
        
    }

    public void registerController(AbstractRestController controller) {
        controllers.put(controller.getPath(), controller);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        AbstractRestController controller = controllers.get(target);
        if (controller != null) {
            response.setCharacterEncoding("UTF-8");

            try {
                Object ret = controller.handle(target, baseRequest);
                response.setStatus(200);
                response.setContentType("application/json");
                response.getOutputStream().write(gson.toJson(ret).getBytes("UTF-8"));
            } catch (Exception e) {
                response.setStatus(500);
                response.setContentType("text/html");
                response.getOutputStream().println(e.getMessage());
            }
            baseRequest.setHandled(true);
        }
    }
}
