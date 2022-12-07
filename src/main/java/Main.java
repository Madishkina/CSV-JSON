import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;




public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // Задача 1:CSV-JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};//массив строчек columnMapping, содержащий информацию о предназначении колонок в CVS файле
        String fileName = "data.csv";//определим имя для считываемого CSV файла
        List<Employee> list = parseCSV(columnMapping, fileName);//Далее получите список сотрудников, вызвав метод parseCSV()
        String json = listToJson(list);//Полученный список преобразуйте в строчку в формате JSON. Сделайте это с помощью метода listToJson()

        //Задача 2: XML - JSON парсер

        List<Employee> list2 = parseXML("data2.json");
        String json2 = listToJson(list2);
        String jsonFilename2 = "data2.json";
        writeString(json, jsonFilename2);


    }

    private static void writeString(String json, String jsonFilename) {

            try (FileWriter file = new FileWriter(jsonFilename)) {
                file.write(json);
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private static List<Employee> parseXML(String s) throws ParserConfigurationException, IOException, SAXException {//Для получения списка сотрудников из XML документа используйте метод parseXML()

        List<String> elements = new ArrayList<>();
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();//с использованием DocumentBuilderFactory (2)
        DocumentBuilder builder = factory.newDocumentBuilder();//и DocumentBuilder через метод parse() (3)
        Document doc = builder.parse(new File("data.xml"));//необходимо получить экземпляр класса Document (1)
        Node root = doc.getDocumentElement();//получите из объекта Document корневой узел Node с помощью метода getDocumentElement()
        NodeList nodeList = root.getChildNodes();//из корневого узла извлеките список узлов NodeList с помощью метода getChildNodes()
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                NodeList nodeList1 = node.getChildNodes();
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node node_ = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == node_.getNodeType()) {
                        elements.add(node_.getTextContent());
                    }
                }
                list.add(new Employee(
                        Long.parseLong(elements.get(0)),
                        elements.get(1),
                        elements.get(2),
                        elements.get(3),
                        Integer.parseInt(elements.get(4))));
                elements.clear();
            }
        }
        return list;
    }


    private static <T> String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();//вам понадобятся объекты типа GsonBuilder
        Gson gson = builder.setPrettyPrinting().create();//и Gson
        Type listType = new TypeToken<List<T>>() {
        }.getType();//для преобразования списка объектов в JSON, требуется определить тип этого спика
        String json = gson.toJson(list, listType);//Получить JSON из экземпляра класса Gson можно с помощтю метода toJson(), передав в качестве аргументов список сотрудников и тип списка
        try (FileWriter file = new FileWriter("data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gson.toJson(list, listType);
    }


    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {// Задача 1:CSV-JSON
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) { //Данную операцию производите в блоке try-catch с ресурсами
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();//вам потребуется объект класса ColumnPositionMappingStrategy

            strategy.setType(Employee.class);//используя объект стратегии, укажите тип setType()
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");//тип колонок setColumnMapping()
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)// Далее создайте экземпляр CsvToBean с использованием билдера CsvToBeanBuilder
                    .withMappingStrategy(strategy).//При постройке CsvToBean используйте ранее созданный объект стратегии ColumnPositionMappingStrategy
                            build();
            List<Employee> list = csv.parse();//Созданный экземпляр объекта CsvToBean имеет метод parse(), который вернет список сотрудников.
            list.forEach(System.out::println);// Выполним операцию над записью
            return list;

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }
}
