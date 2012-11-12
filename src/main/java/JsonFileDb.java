import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class JsonFileDb<C> {

    private final Charset UTF8 = Charset.forName("UTF-8");

    private final Gson gson = new Gson();

    public void writeAsJson(C bean, Writer writer) throws IOException {
        String lineAsJson = gson.toJson(bean);
        writer.write(lineAsJson);
        writer.write("\n");
    }

    public void writeListAsJson(List<C> beanList, Writer writer) throws IOException {
        for (C bean : beanList) {
            writeAsJson(bean, writer);
        }
    }

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
