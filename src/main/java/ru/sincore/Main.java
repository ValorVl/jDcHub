package ru.sincore;

import org.apache.log4j.Logger;
import ru.sincore.db.dao.StubDAO;

public class Main extends Thread
{

    private static final Logger log = Logger.getLogger(Main.class);


    public Main()
    {
        this.start();
    }


    public void run()
    {
        setName("core-main");
        setPriority(NORM_PRIORITY);
		ConfigLoader.init();

		StubDAO st = new StubDAO();

		System.out.println(ConfigLoader.DB_ENGINE);

		st.del(248L);



    }


    /**
     * @param args Entry point, start program this
     */
    public static void main(String[] args)
    {
        new Main();
    }
}
