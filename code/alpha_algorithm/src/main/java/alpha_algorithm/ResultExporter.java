package alpha_algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultExporter {
    public static void exportToJson(ArrayList<Fragment> fragments, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(fileName), fragments);
    }
}
