package ru.sincore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Клас загрузцик и инициализатор конфигурационных параметров. Параметры 
 * представлены ввиде статических констант.
 * 
 * @author valor
 *	
 */
public class ConfigLoader {
	
	private static  String CFG_DIR = "";
		
	public ConfigLoader(String cfg) {

		CFG_DIR = cfg;
	}
	
	public static void init(){
		
		Properties prop = new Properties();
		File confFile   = new File(CFG_DIR);

		try {
			FileReader fr = new FileReader(confFile);
			prop.load(fr);
			
			//TODO More configuration parameters
			
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
