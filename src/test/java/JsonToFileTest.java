import com.google.gson.Gson;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;


@RunWith(JUnit4.class)
public class JsonToFileTest {

    public static class Bean {

        String name;
        String value;

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final Gson gson = new Gson();

    @Test
    public void shouldReadOneElement() throws IOException {
        JsonFileDb jsonFileDb = new JsonFileDb<Bean>();
        StringWriter writer = new StringWriter();
        Bean b = new Bean();
        b.name = "foo\nhello";
        b.value = "bar";
        jsonFileDb.writeAsJson(b, writer);
        writer.close();

        logger.info("bean = " + b);
        logger.info("file content : " + writer.getBuffer());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.getBuffer().toString().getBytes());
        Bean readBean = gson.fromJson(new InputStreamReader(inputStream), Bean.class);

        assertEquals(b, readBean);
    }

    @Test
    public void shouldReadTwoElement() throws IOException {
        JsonFileDb jsonFileDb = new JsonFileDb<Bean>();

        StringWriter writer = new StringWriter();
        Bean b = new Bean();
        b.name = "foo\nhello";
        b.value = "bar";
        jsonFileDb.writeAsJson(b, writer);
        jsonFileDb.writeAsJson(b, writer);
        writer.close();

        logger.info("bean = " + b);
        logger.info("file content : " + writer.getBuffer());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.getBuffer().toString().getBytes());

        List<Bean> list = jsonFileDb.fromJsonList(inputStream, Bean.class);

        for (Bean readBean : list) {
            assertEquals(b, readBean);
        }
        int lineCount = list.size();
        assertEquals(2, lineCount);
    }


    @Test
    public void shouldFilterElement() throws IOException {

        Bean b1 = new Bean();
        {
            b1.name = "John";
            b1.value = "bar";
            logger.info("bean = " + b1);
        }
        Bean b2 = new Bean();
        {
            b2.name = "Jim";
            b2.value = "bar";
            logger.info("bean = " + b2);
        }

        List<Bean> beanList = new ArrayList<Bean>();
        beanList.add(b1);
        beanList.add(b2);

        JsonFileDb jsonFileDb = new JsonFileDb<Bean>();

        StringWriter writer = new StringWriter();
        jsonFileDb.writeListAsJson(beanList, writer);
        writer.close();

        logger.info("file content : " + writer.getBuffer());

        Filter<Bean> filterAcceptJimName = new Filter<Bean>() {

            public boolean accept(Bean object) {
                return "Jim".equals(object.name);
            }
        };

        Filter<Bean> filterAcceptBarValue = new Filter<Bean>() {

            public boolean accept(Bean object) {
                return "bar".equals(object.value);
            }
        };


        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.getBuffer().toString().getBytes());
            List<Bean> list = jsonFileDb.fromJsonList(inputStream, Bean.class, filterAcceptJimName);
            assertEquals(1, list.size());
            for (Bean readBean : list) {
                assertEquals(b2, readBean);
            }
        }
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.getBuffer().toString().getBytes());
            List<Bean> list = jsonFileDb.fromJsonList(inputStream, Bean.class, filterAcceptBarValue);
            assertEquals(2, list.size());
        }


    }

}
