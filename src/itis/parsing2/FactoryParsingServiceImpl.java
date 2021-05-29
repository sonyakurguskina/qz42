package itis.parsing2;

import itis.parsing2.annotations.Concatenate;
import itis.parsing2.annotations.NotBlank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FactoryParsingServiceImpl implements FactoryParsingService {

    @Override
    public void parseFactoryData(String factoryDataDirectoryPath) throws FactoryParsingException, ClassNotFoundException, IOException {
        //write your code here
        Class<?> classFactory = Class.forName(Factory.class.getName());

        List<String> namesFields = new ArrayList<>();
        List<List<String[]>> annotationsOfFields = new ArrayList<>();

        for (Field field : classFactory.getDeclaredFields()) {
            namesFields.add(field.getName());

            List<String[]> annotationList = new ArrayList<>();
            if (field.isAnnotationPresent(Concatenate.class)) {

                String[] arr = field.getAnnotation(Concatenate.class).fieldNames();
                int length = arr.length;
                String[] array = new String[length + 2];
                array[0] = field.getAnnotation(Concatenate.class).annotationType().getName();
                array[1] = field.getAnnotation(Concatenate.class).delimiter();
                for (int i = 2; i < length + 2; i++) {
                    array[i] = arr[i-2];
                }
                annotationList.add(array);
            }

            if (field.isAnnotationPresent(NotBlank.class)) {
                String[] array = new String[1];
                array[0] = field.getAnnotation(NotBlank.class).annotationType().getName();
                annotationList.add(array);
            }
            annotationsOfFields.add(annotationList);
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(factoryDataDirectoryPath));
        String сurrentLine;
        HashMap<String, String> lines = new HashMap<>();

        while ((сurrentLine = bufferedReader.readLine()) != null) {
            if (!сurrentLine.contains("---")) {
                String[] splittedDataLine = сurrentLine.split(":");
                splittedDataLine[0] = splittedDataLine[0].replaceAll("\"", "");
                splittedDataLine[1] = splittedDataLine[1].replaceAll("\"", "");
                lines.put(splittedDataLine[0], splittedDataLine[1]);
            }
        }

        HashMap<String, String> parsedFactoryData = new HashMap<>();

        for (int i = 0; i < namesFields.size(); i++) {
            if (lines.containsKey(namesFields.get(i)) || namesFields.get(i)=="organizationChiefFullName") {
                List<String[]> currentAnnOfField = annotationsOfFields.get(i);

                if (currentAnnOfField.size() > 0){
                    for (int j = 0; j < currentAnnOfField.size(); j++) {
                        if (currentAnnOfField.get(j)[0].contains("NotBlank")) {
                            if (lines.get(namesFields.get(i)) == "") {
                                parsedFactoryData.put(namesFields.get(i), "null");
                            } else
                                parsedFactoryData.put(namesFields.get(i), lines.get(namesFields.get(i)));
                        }

                        else if (currentAnnOfField.get(j)[0].contains("Concatenate")) {
                            String[] arr = currentAnnOfField.get(j);
                            String del = arr[1];
                            String fullName = "";
                            for (int k = 1; k < arr.length; k++) {
                                if (k != 1){
                                    if (lines.containsKey(arr[k])){
                                        fullName += del + lines.get(arr[k]);
                                        parsedFactoryData.put("organizationChiefFullName", fullName);
                                    }
                                }
                            }
                        }
//                    }
                    }
                }
                else
                    parsedFactoryData.put(namesFields.get(i), lines.get(namesFields.get(i)));
            }
        }
        System.out.println(parsedFactoryData);
    }
}
