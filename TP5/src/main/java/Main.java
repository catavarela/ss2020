import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Track track = new Track();
        writeFile(track.run(), "output.xyz", false);
    }

    public static void writeFile(List<String> output, String fileName, boolean append) {
        Iterator<String> it = output.iterator();

        try {
            FileWriter writer = new FileWriter(fileName, append);

            while (it.hasNext())
                writer.write(it.next() + '\n');

            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo " + fileName + '.');
            e.printStackTrace();
        }
    }

}