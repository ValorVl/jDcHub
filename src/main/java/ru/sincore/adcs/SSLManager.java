/*
 * SSLManager.java
 *
 * Created on 15 septembrie 2008, 20:57
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


package ru.sincore.adcs;

import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.mina.filter.ssl.SslFilter;

public class SSLManager
{
    private SslFilter sslfilter = null;
    private CertManager cm;


    public SslFilter getSSLFilter()
    {
        return sslfilter;
    }


    public CertManager getCertManager()
    {
        return cm;
    }


    public SSLManager(CertManager cm)
    {
        this.cm = cm;
        try
        {
            KeyStore keystore = cm.getKeyStore();
            if (keystore == null)
            {
                return;//couldn;t find any keys so running normal adc mode
            }
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keystore, CertManager.passwd);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keystore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(),
                            trustManagerFactory.getTrustManagers(),
                            null);

            sslfilter = new SslFilter(sslContext);
        }
        catch (KeyStoreException e)
        {

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
    }
}
