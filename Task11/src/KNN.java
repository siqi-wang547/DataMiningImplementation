import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class KNN {
    
    public static void main(String[] args) throws IOException {
        File inFile = new File("trainProdSelection.arff");
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"));
        
        for (String l = in.readLine(); l != null; l = in.readLine()) {
            if (!l.startsWith("@") && l.length() > 0) {
                System.out.println(l);
            }
        }
    }

}
