/*
 * BanWordsList.java
 *
 * Created on 30 octombrie 2007, 15:12
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package dshub;

/**
 * A list of banned words.
 *
 * @author naccio
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

public class BanWordsList
{
    Vector<BannedWord> bannedWords;


    /**
     * Creates a new instance of BanWordsList
     */
    public BanWordsList()
    {
        bannedWords = new Vector<BannedWord>();
    }


    public int size()
    {
        return bannedWords.size();
    }


    /**///searches word by name
    ///returns its index*/
    public int searchEl(String s)
    {

        for (int i = 0; i < bannedWords.size(); i++)
        {
            BannedWord cuv = (BannedWord) bannedWords.elementAt(i);
            if (s.equals(cuv.cuvant))
            {
                return i;
            }
        }
        ;
        return -1;
    }


    /**
     * modifies the name of the word at index
     */
    public void modifyStrAt(int index, String s)
    {

        BannedWord cuv = (BannedWord) bannedWords.elementAt(index);
        cuv.setWord(s);
    }


    /**
     * ///replaces word name with another
     */
    public void modifyStr(String s, String news)
    {

        int n = searchEl(s);
        if (n == -1)
        {
            System.out.println("The word you want to modify is not in the list");
            return;
        }
        modifyStrAt(n, news);
    }


    /**
     * ///modifies propreties at index
     */
    public void modifyPrAt(int index, long prop, String repl)
    {

        BannedWord cuv = (BannedWord) bannedWords.elementAt(index);
        cuv.setFlags(prop, repl);
    }


    /**
     * ///modifies propreties by name
     */
    public boolean modifyPr(String s, long prop, String repl)
    {

        int n = searchEl(s);
        if (n == -1)
        {
            //System.out.println("The word you want to modify is not in the list");
            return false;
        }
        modifyPrAt(n, prop, repl);
        return true;
    }


    /**
     * ///modifies client propreties for multiple selection
     */
    public void modifyMultiClientPrAt(int[] list, long prop)
    {

        prop = prop & BannedWord.allclient;
        //   System.out.println("%%%%%");
        //   System.out.println("prop:");
        //  System.out.println(prop);
        long curpr;
        for (int i = 0; i < list.length; i++)
        {
            BannedWord cuv = (BannedWord) bannedWords.elementAt(list[i]);
            // System.out.print(list[i]+" ; ");
            curpr = cuv.getFlags();
            String repl = cuv.getReplacement();
            //   System.out.println("curpr:");
            //   System.out.println(curpr);
            curpr = curpr & BannedWord.allword;
            //  System.out.println(curpr);
            curpr = curpr | prop;
            // System.out.println(curpr);
            cuv.setFlags(curpr, repl);
        }
        // System.out.println("&&&&&");
    }


    public void modifyMultiPrAt(int[] list, long prop)
    {


        for (int i = 0; i < list.length; i++)
        {
            BannedWord cuv = (BannedWord) bannedWords.elementAt(list[i]);


            cuv.setFlags(prop);
        }
        // System.out.println("&&&&&");
    }


    /**
     * ///modifies word propreties for multiple selection
     */
    public void modifyMultiWordPrAt(int[] list, long prop, String repl)
    {

        prop = prop & BannedWord.allword;
        long curpr;
        for (int i = 0; i < list.length; i++)
        {
            BannedWord cuv = (BannedWord) bannedWords.elementAt(list[i]);
            curpr = cuv.getFlags();
            curpr = curpr & BannedWord.allclient;
            curpr = curpr | prop;
            cuv.setFlags(curpr, repl);
        }
    }


    public static boolean ver_regex(String s)
    {
        try
        {
            "".matches(s);
        }
        catch (PatternSyntaxException e)
        {
            return false;
        }
        return true;
    }


    /**
     * ///adds an element at the begining of the list
     */
    public boolean add(String s, long proprietati, String replacement)
    {

        if (!ver_regex(s))
        {
            System.out.println("Error: " + s + " is not a valid regex");
            return false;
        }
        int x = searchEl(s);
        if (x == -1)
        {
            BannedWord altCuvant = new BannedWord(s, proprietati, replacement);
            try
            {
                bannedWords.insertElementAt(altCuvant, 0);
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
        }
        else
        {
            BannedWord cuv = (BannedWord) bannedWords.elementAt(x);
            cuv.setFlags(proprietati, replacement);
        }
        return true;
    }


    /**
     * ///adds an element at the end of the list
     */
    public void append(String s, long proprietati, String replacement)
    {
        if (!ver_regex(s))
        {
            System.out.println("Error: " + s + " is not a valid regex");
            return;
        }
        int x = searchEl(s);
        if (x == -1)
        {
            BannedWord altCuvant = new BannedWord(s, proprietati, replacement);
            try
            {
                bannedWords.add(altCuvant);
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
            }
        }
        else
        {
            BannedWord cuv = (BannedWord) bannedWords.elementAt(x);
            cuv.setFlags(proprietati, replacement);
        }
    }


    /**
     * //cleans the list
     */
    public void clean()
    {

        bannedWords.removeAllElements();
    }


    /**
     * /// prints to file
     */
    public boolean printFile(String path)
    {

        FileWriter fo;
        try
        {
            fo = new FileWriter(path);
        }
        catch (Exception e)
        {

            return false;
        }
        try
        {
            fo.write(toString());
            fo.close();
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }


    /**
     * ///loads list from file
     */
    public boolean loadFile(String path)
    {

        FileReader fi;
        File f;
        try
        {
            fi = new FileReader(path);
            f = new File(path);
        }
        catch (FileNotFoundException e)
        {
            //System.out.println("Invalid File");
            //System.out.println(e.toString());
            printFile(path);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        if (!f.isFile())
        {
            //System.out.println("The path must be a file");
            return false;
        }

        char[] buff = new char[(int) f.length() + 1];
        //System.out.println(f.length());
        int n;
        try
        {
            n = fi.read(buff);
        }
        catch (FileNotFoundException e)
        {
            //System.out.println("File not readable");
            //System.out.println(e.toString());
            printFile(path);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        String buffer = new String(buff);

        StringTokenizer st = new StringTokenizer(buffer, ";\n\r");
        try
        {
            while (st.countTokens() >= 3)
            {
                //System.out.println("am intrat");
                String cuv;
                String pr;
                long prop;
                String repl;
                cuv = st.nextToken();
                cuv = cuv.substring(1);
                //System.out.println(cuv);
                pr = st.nextToken();
                pr = pr.substring(1);
                prop = Long.parseLong(pr);
                //System.out.println(prop);
                repl = st.nextToken();
                repl = repl.substring(1);
                //System.out.println(repl);
                append(cuv, prop, repl);
            }
        }
        catch (Exception e)
        {
            //cc
            //System.out.println("Invalid File Format");
            //System.out.println(e.toString());
            printFile(path);
            return true;
        }
        return true;
    }


    /**
     * ///removes word at index
     */
    public boolean removeElAt(int index)
    {

        try
        {
            bannedWords.removeElementAt(index);
            return true;
        }
        catch (Exception e)
        {
            // System.out.println(e.toString());
            return false;
        }
    }


    /**
     * ///removes word by name
     */
    public boolean removeElement(String s)
    {

        int i;
        BannedWord cuv;
        try
        {
            for (i = 0; i < bannedWords.size(); i++)
            {
                cuv = (BannedWord) bannedWords.elementAt(i);
                if (s.equals(cuv.getWord()))
                {
                    bannedWords.removeElementAt(i);
                    i--;

                    return true;
                }
            }
        }
        catch (Exception e)
        {
            return false;
            // System.out.println(e.toString());
        }
        return false;
    }


    /**
     * ///removes multiple words given by names
     */
    public void removeElements(String[] list)
    {

        int i;
        for (i = 0; i < list.length; i++)
        {
            removeElement(list[i]);
        }
    }


    /**
     * ///removes multiple words given by indexes
     */
    public void removeElementsAt(int[] list)
    {

        int i;
        java.util.Arrays.sort(list);
        for (i = list.length - 1; i >= 0; i--)
        {
            removeElAt(list[i]);
        }
    }


    /**
     * ///returns the name of the word at index
     */
    public String elementAt(int index)
    {

        BannedWord cuv;
        cuv = (BannedWord) bannedWords.elementAt(index);
        return cuv.getWord();
    }


    /**
     * ///returns flags at index
     * static final long dropped=1;
     * static final long kicked=2;
     * static final long noAction=4;
     * static final long hidden=8;
     * static final long replaced=16;
     * static final long modified=32;
     * static final long allclient=7;
     * static final long allword=56;
     */
    public long getPrAt(int index)
    {

        BannedWord cuv;
        cuv = (BannedWord) bannedWords.elementAt(index);
        return cuv.getFlags();
    }


    /**
     * unkown
     */
    public long getPr(String s)
    {
        int n = searchEl(s);
        if (n == -1)
        {
            // System.out.println("The word is not in the list");
            return 0;
        }
        return getPrAt(n);
    }


    /**
     * ///returns replacement at index
     */
    public String getReplAt(int index)
    {

        BannedWord cuv;
        cuv = (BannedWord) bannedWords.elementAt(index);
        return cuv.getReplacement();
    }


    /**
     * ///returns replacement of the word
     */
    public String getRepl(String s)
    {

        int n = searchEl(s);
        if (n == -1)
        {
            System.out.println("The word is not in the list");
            return "";
        }
        return getReplAt(n);
    }


    /**
     * ///generates a String version of the vector
     */
    public String toString()
    {

        String v = "";
        int i;
        for (i = 0; i < bannedWords.size(); i++)
        {
            try
            {
                v += ":" + ((BannedWord) bannedWords.elementAt(i)).getWord() + ";:" +
                     ((BannedWord) bannedWords.elementAt(i)).getFlags() + ";:" +
                     ((BannedWord) bannedWords.elementAt(i)).getReplacement() + "\n";
            }
            catch (Exception e)
            {
                //System.out.println(e.toString());
            }
        }
        return v;
    }


    public String List()
    {

        String v = "\n";
        int i;
        for (i = 0; i < bannedWords.size(); i++)
        {

            v += "\"" +
                 ((BannedWord) bannedWords.elementAt(i)).getWord() +
                 "\"  flags " +
                 ((BannedWord) bannedWords.elementAt(i)).getFlags();
            if ((((BannedWord) bannedWords.elementAt(i)).getFlags() & BannedWord.modified) != 0)
            {
                v += "  replacement " + ((BannedWord) bannedWords.elementAt(i)).getReplacement();
            }
            v += "    ID " + i + "\n";


        }
        return v.substring(0, v.length() - 1);
    }


    /**
     * -1 if string passes all checks
     * index otherwise
     */
    public int isOK(String str)
    {
        for (int i = 0; i < size(); i++)
        {
            if (str.matches(".*" + ((BannedWord) (bannedWords.elementAt(i))).cuvant + ".*"))
            {
                return i;
            }
        }
        return -1;
    }

}
