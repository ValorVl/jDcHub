/*
 * ChatControlCmd.java
 *
 * Created on 09 noiembrie 2007, 11:20
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

package ru.sincore.cmd;

import java.util.StringTokenizer;

import ru.sincore.BanWordsList;
import ru.sincore.ClientHandler;
import ru.sincore.Main;

/**
 * An command interface to banned words.
 *
 * @author Pietricica
 */
public class ChatControlCmd
{

    /**
     * Creates a new instance of ChatControlCmd
     */
    public ChatControlCmd(ClientHandler cur_client, String cmd)
    {
        StringTokenizer ST = new StringTokenizer(cmd);
        ST.nextToken();
        if (!(ST.hasMoreTokens()))
        {
            String Help = "\nThe chatcontrol command:\n" +
                          "Usage: chatcontrol list" +
                          "\n          -- brings up a list of chat controls and their unique ID\n" +
                          "\nUsage: chatcontrol add \"regular expression\" flags [modification]" +

                          "\n          -- adds the regular expression with corresponding flags. The expression must be enclosed in quotes.\n" +
                          "The flags are : \n" +
                          " Drop user 1\n" +
                          " Kick user 2\n" +
                          " Don't do anything to client 4\n" +
                          " Hide the matching word from chat 8\n" +
                          " Replace matching word with stars ( **** ) 16\n" +
                          " Modify matching word with given modification 32\n" +
                          " Control private chat as well 64\n" +
                          " Report to operator's chat 128\n" +
                          " Also check searches 256\n" +
                          "As you can see, you must not select all flags, but one of each category.\n" +
                          "Example: Drop user and replace word with stars : Use flag 1+16=17\n" +
                          "Note: flag 64, 128 and 256 are independent of others.\n" +
                          "Note: The modification parameter is only available for flag including 32.\n" +
                          "\nUsage: chatcontrol mod ID/\"regular expression\" flags [modification]." +
                          "\n          -- mods the regular expression already listed given by string or it's unique ID. Parameters are same like on adding.\n" +
                          "\nUsage: chatcontrol del ID/\"regular expression\" " +
                          "\n          -- deletes the regular expression given by itself or by it's unique ID.\n" +
                          "\nUsage: chatcontrol save \"filename\" " +
                          "\n          -- saves the current wordlist into file specified by path. For security reasons, you can write to DSHub installation dir only.\n" +
                          "\nUsage: chatcontrol load \"filename\" " +
                          "\n          -- load the wordlist configuration from the file specified by path. For security reasons, you can read from DSHub installation dir only.";
            cur_client.sendFromBot(Help);
            return;
        }
        String what = ST.nextToken();
        if (!(ST.hasMoreTokens()))
        {
            if (what.equalsIgnoreCase("list"))
            {
                String Help = "\nChat Control List:";
                Help += Main.listaBanate.List();

                cur_client.sendFromBot(Help);
                return;
            }
            else if (what.equalsIgnoreCase("add"))
            {
                String Help =
                        "\nUsage: chatcontrol add \"regular expression\" flags [modification]" +

                        "\n          -- mods the regular expression with corresponding flags. The expression must be enclosed in quotes.\n" +
                        "The flags are : \n" +
                        " Drop user 1\n" +
                        " Kick user 2\n" +
                        " Don't do anything to client 4\n" +
                        " Hide the matching word from chat 8\n" +
                        " Replace matching word with stars ( **** ) 16\n" +
                        " Modify matching word with given modification 32\n" +
                        " Control private chat as well 64\n" +
                        " Report to operator's chat 128\n" +
                        " Also check searches 256\n" +
                        "As you can see, you must not select all flags, but one of each category.\n" +
                        "Example: Drop user and replace word with stars : Use flag 1+16=17\n" +
                        "Note: The modification parameter is only available for flag including 32.\n";

                cur_client.sendFromBot(Help);
                return;
            }
            else if (what.equalsIgnoreCase("mod"))
            {
                String Help =
                        "\nUsage: chatcontrol mod \"regular expression\" flags [modification]" +

                        "\n          -- mods the regular expression already listed given by string or it's unique ID. The expression must be enclosed in quotes.\n" +
                        "The flags are : \n" +
                        " Drop user 1\n" +
                        " Kick user 2\n" +
                        " Don't do anything to client 4\n" +
                        " Hide the matching word from chat 8\n" +
                        " Replace matching word with stars ( **** ) 16\n" +
                        " Modify matching word with given modification 32\n" +
                        " Control private chat as well 64\n" +
                        " Report to operator's chat 128\n" +
                        " Also check searches 256\n" +
                        "As you can see, you must not select all flags, but one of each category.\n" +
                        "Example: Drop user and replace word with stars : Use flag 1+16=17\n" +
                        "Note: The modification parameter is only available for flag including 32.\n";

                cur_client.sendFromBot(Help);
                return;
            }
            else if (what.equalsIgnoreCase("del"))
            {
                String Help = "\nUsage: chatcontrol del ID/\"regular expression\" " +

                              "\n          -- deletes the regular expression given by itself or by it's unique ID.";

                cur_client.sendFromBot(Help);
                return;
            }
            else if (what.equalsIgnoreCase("save"))
            {
                String Help = "\nUsage: chatcontrol save \"filename\" " +
                              "\n          -- saves the current wordlist into file specified by name.";

                cur_client.sendFromBot(Help);
                return;
            }
            else if (what.equalsIgnoreCase("load"))
            {
                String Help = "\nUsage: chatcontrol load \"filename\" " +
                              "\n          -- load the wordlist configuration from the file specified by name.";

                cur_client.sendFromBot(Help);
                return;
            }
            else
            {
                cur_client.sendFromBot("Unkown chatcontrol command.");
                return;
            }
        }
        if (what.equalsIgnoreCase("add"))
        {
            String regex = ST.nextToken();
            if (!regex.startsWith("\""))
            {
                cur_client.sendFromBot("Regular expression must be enclosed in quotes.");
                return;
            }

            String bla = regex;
            while (!bla.endsWith("\""))
            {
                if (!ST.hasMoreTokens())
                {
                    cur_client.sendFromBot("Regular expression must be enclosed in quotes.");
                    return;
                }
                bla = ST.nextToken();
                regex += " " + bla;

            }
            regex = regex.substring(1, regex.length() - 1);
            if (BanWordsList.ver_regex(regex) == false)
            {
                cur_client.sendFromBot("Invalid Regular Expression Pattern.");
                return;
            }


            if (!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("Must specify the flags.");
                return;
            }
            String flags = ST.nextToken();
            int flag = 0;
            try
            {
                flag = Integer.parseInt(flags);
            }
            catch (NumberFormatException nfe)
            {
                cur_client.sendFromBot("Invalid flags.");
                return;
            }

            switch (flag)
            {
                /* 
                         static final long dropped=1;
    static final long kicked=2;
    static final long noAction=4;
    static final long hidden=8;
    static final long replaced=16;
    static final long modified=32;
    static final long allclient=7;
    static final long allword=56;
    public static final long privatechat=64;
    public static final long notify=128;
    public static final long searches=256;*/
                case 9:
                case 10:
                case 17:
                case 12:
                case 18:
                case 20:
                case 9 + 64:
                case 9 + 128:
                case 9 + 256:
                case 9 + 128 + 64 + 256:
                case 9 + 128 + 256:
                case 9 + 256 + 64:
                case 9 + 128 + 64:
                case 10 + 64:
                case 10 + 128:
                case 10 + 256:
                case 10 + 128 + 64 + 256:
                case 10 + 128 + 256:
                case 10 + 256 + 64:
                case 10 + 128 + 64:
                case 12 + 64:
                case 12 + 128:
                case 12 + 256:
                case 12 + 128 + 64 + 256:
                case 12 + 128 + 256:
                case 12 + 256 + 64:
                case 12 + 128 + 64:
                case 17 + 64:
                case 17 + 128:
                case 17 + 256:
                case 17 + 128 + 64 + 256:
                case 17 + 128 + 256:
                case 17 + 256 + 64:
                case 17 + 128 + 64:
                case 18 + 64:
                case 18 + 128:
                case 18 + 256:
                case 18 + 128 + 64 + 256:
                case 18 + 128 + 256:
                case 18 + 256 + 64:
                case 19 + 128 + 64:
                case 20 + 64:
                case 20 + 128:
                case 20 + 256:
                case 20 + 128 + 64 + 256:
                case 20 + 128 + 256:
                case 20 + 256 + 64:
                case 20 + 128 + 64:

                    Main.listaBanate.add(regex, (long) flag, "x");
                    cur_client.sendFromBot("Successfully added.");
                    break;
                case 34:
                case 33:
                case 36:
                case 34 + 64:
                case 33 + 64:
                case 36 + 64:
                case 34 + 128:
                case 33 + 128:
                case 36 + 128:
                case 34 + 256:
                case 33 + 256:
                case 36 + 256:
                case 34 + 64 + 256:
                case 33 + 64 + 256:
                case 36 + 64 + 256:
                case 34 + 128 + 256:
                case 33 + 128 + 256:
                case 36 + 128 + 256:
                case 34 + 128 + 64:
                case 33 + 128 + 64:
                case 36 + 128 + 64:
                case 34 + 128 + 64 + 256:
                case 33 + 128 + 64 + 256:
                case 36 + 128 + 64 + 256:
                {
                    if (!ST.hasMoreTokens())
                    {
                        cur_client.sendFromBot("Must specify replacement string.");
                        return;
                    }
                    String repl = "";
                    while (ST.hasMoreTokens())
                    {
                        repl += ST.nextToken() + " ";
                    }
                    repl = repl.substring(0, repl.length() - 1);
                    Main.listaBanate.add(regex, (long) flag, repl);
                    cur_client.sendFromBot("Successfully added.");
                    break;
                }
                default:
                {
                    cur_client.sendFromBot("Invalid flags.");
                    return;
                }

            }
            if (Main.GUIok)
            {
                Main.GUI.refreshListaBanate();
            }


        }
        else if (what.equalsIgnoreCase("mod"))
        {
            String regex = ST.nextToken();
            int id = -1;
            try
            {
                id = Integer.parseInt(regex);
            }
            catch (NumberFormatException nfe)
            {
                if (!regex.startsWith("\""))
                {
                    cur_client.sendFromBot("Regular expression must be enclosed in quotes.");
                    return;
                }

                String bla = regex;
                while (!bla.endsWith("\""))
                {
                    if (!ST.hasMoreTokens())
                    {
                        cur_client.sendFromBot("Regular expression must be enclosed in quotes.");
                        return;
                    }
                    bla = ST.nextToken();
                    regex += " " + bla;

                }
                regex = regex.substring(1, regex.length() - 1);
            }

            if (Main.listaBanate.size() < id)
            {
                cur_client.sendFromBot("Invalid Regular Expression ID.");
                return;
            }


            if (!ST.hasMoreTokens())
            {
                cur_client.sendFromBot("Must specify the flags.");
                return;
            }
            String flags = ST.nextToken();
            int flag = 0;
            try
            {
                flag = Integer.parseInt(flags);
            }
            catch (NumberFormatException nfe)
            {
                cur_client.sendFromBot("Invalid flags.");
                return;
            }

            switch (flag)
            {
                /* 
                         static final long dropped=1;
    static final long kicked=2;
    static final long noAction=4;
    static final long hidden=8;
    static final long replaced=16;
    static final long modified=32;
    static final long allclient=7;
    static final long allword=56;*/
                case 9:
                case 17:
                case 10:
                case 12:
                case 18:
                case 20:
                case 9 + 64:
                case 9 + 128:
                case 9 + 256:
                case 9 + 128 + 64 + 256:
                case 9 + 128 + 256:
                case 9 + 256 + 64:
                case 9 + 128 + 64:
                case 10 + 64:
                case 10 + 128:
                case 10 + 256:
                case 10 + 128 + 64 + 256:
                case 10 + 128 + 256:
                case 10 + 256 + 64:
                case 10 + 128 + 64:
                case 12 + 64:
                case 12 + 128:
                case 12 + 256:
                case 12 + 128 + 64 + 256:
                case 12 + 128 + 256:
                case 12 + 256 + 64:
                case 12 + 128 + 64:
                case 17 + 64:
                case 17 + 128:
                case 17 + 256:
                case 17 + 128 + 64 + 256:
                case 17 + 128 + 256:
                case 17 + 256 + 64:
                case 17 + 128 + 64:
                case 18 + 64:
                case 18 + 128:
                case 18 + 256:
                case 18 + 128 + 64 + 256:
                case 18 + 128 + 256:
                case 18 + 256 + 64:
                case 18 + 128 + 64:
                case 20 + 64:
                case 20 + 128:
                case 20 + 256:
                case 20 + 128 + 64 + 256:
                case 20 + 128 + 256:
                case 20 + 256 + 64:
                case 20 + 128 + 64:
                    if (id != -1)
                    {
                        Main.listaBanate.modifyPrAt(id, (long) flag, "x");
                    }
                    else if (Main.listaBanate.modifyPr(regex, (long) flag, "x") == false)
                    {
                        cur_client.sendFromBot("Regular expression doesn't exist.");
                        return;
                    }
                    cur_client.sendFromBot("Successfully modified.");
                    break;
                case 34:
                case 33:
                case 36:
                case 34 + 64:
                case 33 + 64:
                case 36 + 64:
                case 34 + 128:
                case 33 + 128:
                case 36 + 128:
                case 34 + 256:
                case 33 + 256:
                case 36 + 256:
                case 34 + 64 + 256:
                case 33 + 64 + 256:
                case 36 + 64 + 256:
                case 34 + 128 + 256:
                case 33 + 128 + 256:
                case 36 + 128 + 256:
                case 34 + 128 + 64:
                case 33 + 128 + 64:
                case 36 + 128 + 64:
                case 34 + 128 + 64 + 256:
                case 33 + 128 + 64 + 256:
                case 36 + 128 + 64 + 256:
                {
                    if (!ST.hasMoreTokens())
                    {
                        cur_client.sendFromBot("Must specify replacement string.");
                        return;
                    }
                    String repl = "";
                    while (ST.hasMoreTokens())
                    {
                        repl += ST.nextToken() + " ";
                    }
                    repl = repl.substring(0, repl.length() - 1);
                    if (id != -1)
                    {
                        Main.listaBanate.modifyPrAt(id, (long) flag, repl);
                    }
                    else
                    {
                        Main.listaBanate.modifyPr(regex, (long) flag, repl);
                    }
                    cur_client.sendFromBot("Successfully modified.");
                    break;
                }
                default:
                {
                    cur_client.sendFromBot("Invalid flags.");
                    return;
                }

            }
            if (Main.GUIok)
            {
                Main.GUI.refreshListaBanate();
            }


        }
        else if (what.equalsIgnoreCase("del"))
        {
            String regex = ST.nextToken();
            int id = -1;
            try
            {
                id = Integer.parseInt(regex);
            }
            catch (NumberFormatException nfe)
            {
                if (!regex.startsWith("\""))
                {
                    cur_client.sendFromBot("Regular expression must be enclosed in quotes.");
                    return;
                }

                String bla = regex;
                while (!bla.endsWith("\""))
                {
                    if (!ST.hasMoreTokens())
                    {
                        cur_client.sendFromBot("Regular expression must be enclosed in quotes.");
                        return;
                    }
                    bla = ST.nextToken();
                    regex += " " + bla;

                }
                regex = regex.substring(1, regex.length() - 1);
            }

            if (Main.listaBanate.size() <= id)
            {
                cur_client.sendFromBot("Invalid Regular Expression ID.");
                return;
            }

            if (id != -1)
            {
                Main.listaBanate.removeElAt(id);
            }
            else if (Main.listaBanate.removeElement(regex) == false)
            {
                cur_client.sendFromBot("Regular expression doesn't exist.");
                return;
            }
            cur_client.sendFromBot("Successfully deleted.");
            if (Main.GUIok)
            {
                Main.GUI.refreshListaBanate();
            }
        }
        else if (what.equalsIgnoreCase("save"))
        {
            String path = ST.nextToken();

            if (!path.startsWith("\""))
            {
                cur_client.sendFromBot("Filename must be enclosed in quotes.");
                return;
            }

            String bla = path;
            while (!bla.endsWith("\""))
            {
                if (!ST.hasMoreTokens())
                {
                    cur_client.sendFromBot("Filename must be enclosed in quotes.");
                    return;
                }
                bla = ST.nextToken();
                path += " " + bla;

            }
            path = path.substring(1, path.length() - 1);

            if (Main.listaBanate.printFile(path) == true)


            {
                cur_client.sendFromBot("Successfully saved.");
            }
            else
            {
                cur_client.sendFromBot("File access error.");
            }

        }
        else if (what.equalsIgnoreCase("load"))
        {
            String path = ST.nextToken();

            if (!path.startsWith("\""))
            {
                cur_client.sendFromBot("Filename must be enclosed in quotes.");
                return;
            }

            String bla = path;
            while (!bla.endsWith("\""))
            {
                if (!ST.hasMoreTokens())
                {
                    cur_client.sendFromBot("Filename must be enclosed in quotes.");
                    return;
                }
                bla = ST.nextToken();
                path += " " + bla;

            }
            path = path.substring(1, path.length() - 1);

            if (Main.listaBanate.loadFile(path) == true)


            {
                cur_client.sendFromBot("Successfully loaded.");
            }
            else
            {
                cur_client.sendFromBot("File access error.");
            }

        }

    }

}
