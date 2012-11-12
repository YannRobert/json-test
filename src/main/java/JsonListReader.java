import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class JsonListReader<C> {

    private final Charset UTF8 = Charset.forName("UTF-8");

    private final Gson gson = new Gson();

    public List<C> fromJsonList(InputStream inputStream, Class<C> clazz) throws IOException {
        return this.fromJsonList(inputStream, clazz, null);
    }

    public List<C> fromJsonList(InputStream inputStream, Class<C> clazz, Filter filter) throws IOException {
        List<C> result = new ArrayList<C>();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            C readBean = gson.fromJson(line, clazz);
            if (filter != null) {
                if (filter.accept(readBean)) {
                 result.add(readBean);
                }
            } else {
                result.add(readBean);
            }
            line = bufferedReader.readLine();
        }
        return result;
    }

}
