package teplykh.logical.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teplykh.logical.ioValues.interfaces.IOElement;
import teplykh.repository.ExpressionRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@Component
public class HashMapLoaderSaver {

    @Value("${collection.file.path}")
    private String savePath;


    public void writeHashMap(HashMap<String, IOElement> hashMap, String fileName) throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(fileName);
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(hashMap);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public HashMap<String, IOElement> readHashMap(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream
                = new FileInputStream(fileName);
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);
        HashMap<String, IOElement> hashMap = (HashMap<String, IOElement>) objectInputStream.readObject();
        objectInputStream.close();
        return hashMap;
    }

    public void createNewFile(String postfix){
        try {
            Path newFilePath = Paths.get(savePath + "_" + postfix);
            Files.createFile(newFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
