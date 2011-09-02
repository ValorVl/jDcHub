/*
 * BannedWord.java
 *
 * Created on 30 octombrie 2007, 15:13
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
 * @author naccio
 */
public class BannedWord
{
    String cuvant;
    private long   proprietati;
    private String replacement;
    /**
     * cuvant        -- banned word
     * proprietati   -- flags for propreties
     * 0 - drop
     * 1 - kick
     * 2 - no action
     * 3 - hide
     * 4 - replace with *
     * 5 - modify
     * replacement   -- the replacement for the banned word (ifcase)
     */

    public static final long dropped     = 1;
    public static final long kicked      = 2;
    public static final long noAction    = 4;
    public static final long hidden      = 8;
    public static final long replaced    = 16;
    public static final long modified    = 32;
    public static final long privatechat = 64;
    public static final long notify      = 128;
    public static final long searches    = 256;
    public static final long allclient   = 7;
    public static final long allword     = 56;


    /**
     * Creates a new instance of BannedWord
     */


    public BannedWord(String ccuvant, long prop, String repl)
    {
        cuvant = new String(ccuvant);
        proprietati = prop;

        replacement = new String(repl);
    }


    public void setFlags(long prop, String repl)
    {
        proprietati = prop;
        replacement = new String(repl);
    }


    public void setFlags(long prop)
    {
        proprietati = prop;

    }


    public long getFlags()
    {
        return proprietati;
    }


    public String getWord()
    {
        return cuvant;
    }


    public String getReplacement()
    {
        return replacement;
    }


    public void setWord(String s)
    {
        cuvant = new String(s);
    }


    public void setPrivate(boolean x)
    {
        if (x)
        {
            proprietati = proprietati | privatechat;
        }
        else
        {
            proprietati = proprietati &
                          dropped +
                          kicked +
                          noAction +
                          hidden +
                          replaced +
                          modified +
                          notify +
                          searches;
        }
    }


    public void setNotify(boolean x)
    {
        if (x)
        {
            proprietati = proprietati | notify;
        }
        else
        {
            proprietati = proprietati &
                          dropped +
                          kicked +
                          noAction +
                          hidden +
                          replaced +
                          modified +
                          privatechat +
                          searches;
        }
    }


    public void setSearches(boolean x)
    {
        if (x)
        {
            proprietati = proprietati | searches;
        }
        else
        {
            proprietati = proprietati &
                          dropped +
                          kicked +
                          noAction +
                          hidden +
                          replaced +
                          modified +
                          privatechat +
                          notify;
        }
    }
}
