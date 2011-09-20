package ru.sincore;

import ru.sincore.util.AdcUtils;

public class Nick
{

    public static boolean validateNick(String nick)
    {
        if (!nick.matches(ConfigLoader.NICK_CHAR))
		     return false;

		 if (AdcUtils.isIP(nick))
        {
            return false;
        }
        if (AdcUtils.isCID(nick))
        {
            return false;
        }

        int index = Main.listaBanate.isOK(nick);
        if (index != -1)
        {
            return false;
        }
        return true;
    }

}
