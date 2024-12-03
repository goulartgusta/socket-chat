package br.com.almaviva.socketchat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigPropertiesChat {
    private static Properties properties = new Properties();

    static void carregarConfigs(){
        try (FileInputStream arquivoConfig = new FileInputStream("resources/config.properties")) {
            properties.load(arquivoConfig);
        } catch (IOException e) {
            System.err.println("Erro ao carregar config.properties: " + e.getMessage());
        }
    }

    public static String getIP(String ip) {
        return properties.getProperty(ip);
    }

    public static int getPorta(String porta) {
        return Integer.parseInt(properties.getProperty(porta));
    }
}
