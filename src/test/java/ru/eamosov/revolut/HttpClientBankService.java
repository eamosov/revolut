package ru.eamosov.revolut;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import ru.eamosov.revolut.api.BankService;
import ru.eamosov.revolut.api.Transaction;
import ru.eamosov.revolut.api.TransferResult;
import ru.eamosov.revolut.model.TransferResultImpl;
import ru.eamosov.revolut.utils.ZonedDateTimeType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * BankService implementation through HTTP REST and JSON serialization
 */
public class HttpClientBankService implements BankService {
    private String host;
    private int port;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeType())
                                               .registerTypeAdapter(Transaction.class, (JsonDeserializer<Transaction>) (jsonElement, type, jsonDeserializationContext) -> jsonDeserializationContext
                                                   .deserialize(jsonElement, SimpleTransactionImpl.class))
                                               .registerTypeAdapter(TransferResult.class, (JsonDeserializer<TransferResult>) (jsonElement, type, jsonDeserializationContext) -> jsonDeserializationContext
                                                   .deserialize(jsonElement, TransferResultImpl.class))
                                               .setPrettyPrinting()
                                               .create();

    public <R> R get(String path, Type rType, NameValuePair... parameters) throws SQLException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            final HttpGet get = new HttpGet(new URI(String.format("http://%s:%d%s?%s", host, port, path, URLEncodedUtils
                .format(Arrays.asList(parameters), "UTF-8"))));
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    return gson.fromJson(new InputStreamReader(response.getEntity().getContent()), rType);
                } else if (response.getStatusLine().getStatusCode() == 500) {
                    throw new SQLException(IOUtils.toString(response.getEntity()
                                                                    .getContent(), Charset.forName("UTF-8")));
                } else {
                    throw new IOException("invalid status code: " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public HttpClientBankService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean createAccount(UUID id, long balance) throws SQLException {
        return get("/create",
                   Boolean.class,
                   new BasicNameValuePair("id", id.toString()),
                   new BasicNameValuePair("balance", Long.toString(balance)));
    }

    @Override
    public List<Transaction> getHistory(UUID id) throws SQLException {
        return get("/history",
                   new TypeToken<List<Transaction>>() {
                   }.getType(),
                   new BasicNameValuePair("id", id.toString()));
    }

    @Override
    public Long getBalance(UUID id) throws SQLException {
        return get("/balance", Long.class, new BasicNameValuePair("id", id.toString()));
    }

    @Override
    public TransferResult transfer(UUID src, UUID dst, long amount) throws SQLException {
        return get("/transfer",
                   TransferResult.class,
                   new BasicNameValuePair("src", src.toString()),
                   new BasicNameValuePair("dst", dst.toString()),
                   new BasicNameValuePair("amount", Long.toString(amount)));
    }

    @Override
    public boolean checkTransactionHash(UUID id) throws SQLException {
        return get("/checkTransactionHash",
                   Boolean.class,
                   new BasicNameValuePair("id", id.toString()));
    }
}
