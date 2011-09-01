package ru.sincore;

import org.apache.log4j.Logger;

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
    }


    /**
     * @param args Entry point, start program this
     */
    public static void main(String[] args)
    {
        new Main();
    }
}
