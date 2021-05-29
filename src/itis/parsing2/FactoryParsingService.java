package itis.parsing2;

import java.io.IOException;

interface FactoryParsingService {

    void parseFactoryData(String factoryDataDirectoryPath) throws FactoryParsingException, ClassNotFoundException, IOException;

}
