/*
 * KeyManager.java
 *
 * Created on 15 septembrie 2008, 20:33
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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.log4j.Logger;

/**
 * @author Eugen Hristev
 */
public class KeyManager
{
    public static final Logger log = Logger.getLogger(KeyManager.class);

    private PrivateKey privateKey;
    private PublicKey  publicKey;


    public boolean genKeys()
    {
        String keyAlgorithm = "RSA";
        int numBits = 2048;
        log.info("Generating new key/value pair using " +
                keyAlgorithm +
                " algorithm....");
        // Get the public/private key pair
        KeyPairGenerator keyGen = null;
        try
        {
            keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.debug(e);
            return false;
        }

        keyGen.initialize(numBits);
        KeyPair keyPair = keyGen.genKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        return true;
    }


    public PrivateKey getPrivateKey()
    {
        return privateKey;
    }


    public PublicKey getPublicKey()
    {
        return publicKey;
    }


    public KeyManager()
    {
        privateKey = null;
        publicKey = null;
        genKeys();
    }
}
