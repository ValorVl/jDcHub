/*
 * CertManager.java
 *
 * Created on 15 septembrie 2008, 20:40
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

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import ru.sincore.ConfigLoader;
import ru.sincore.Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


public class CertManager
{
    public static final Logger log = Logger.getLogger(CertManager.class);

    static char[] passwd = { 'd', 's', 'h', 'u', 'b' };

    static X509V3CertificateGenerator v1CertGen = new X509V3CertificateGenerator();
    static X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
    private Certificate[] chain;
    //private KeyManager km;

    PublicKey  pubkey;
    PrivateKey privkey;

    private KeyStore ks;


    /**
     * we generate the CA's certificate
     */
    public static Certificate createMasterCert(
            PublicKey pubKey,
            PrivateKey privKey)
            throws Exception
    {
        //
        // signers name 
        //
        String issuer = "C=AU, O=" + ConfigLoader.HUB_NAME + ", OU=" + "DSHub primary certificate";


        //
        // subjects name - the same as we are self signed.
        //
        String subject = "C=AU, O=" + ConfigLoader.HUB_NAME + ", OU=" + "DSHub primary certificate";

        //
        // create the certificate - version 1
        //

        v1CertGen.setSerialNumber(BigInteger.valueOf(1546235));
        v1CertGen.setIssuerDN(new X509Principal(issuer));
        v1CertGen.setNotBefore(new Date(System.currentTimeMillis()));
        v1CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)));
        v1CertGen.setSubjectDN(new X509Principal(subject));
        v1CertGen.setPublicKey(pubKey);
        v1CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

        X509Certificate cert = v1CertGen.generate(privKey);

        cert.checkValidity(new Date());

        cert.verify(pubKey);

        // PKCS12BagAttributeCarrier   bagAttr = (PKCS12BagAttributeCarrier)cert;

        //
        // this is actually optional - but if you want to have control
        // over setting the friendly name this is the way to do it...
        //
        //  bagAttr.setBagAttribute(
        //     PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
        //     new DERBMPString("DSHub Primary Certificate"));

        return cert;
    }


    /**
     * we generate an intermediate certificate signed by our CA
     */
    public static Certificate createIntermediateCert(
            PublicKey pubKey,
            PrivateKey caPrivKey,
            X509Certificate caCert)
            throws Exception
    {
        //
        // subject name table.
        //
        Hashtable attrs = new Hashtable();
        Vector order = new Vector();

        attrs.put(X509Principal.C, "AU");
        attrs.put(X509Principal.O, ConfigLoader.HUB_NAME);
        attrs.put(X509Principal.OU, "DSHub intermediate certificate");
        attrs.put(X509Principal.EmailAddress, "pietry@death-squad.ro");

        order.addElement(X509Principal.C);
        order.addElement(X509Principal.O);
        order.addElement(X509Principal.OU);
        order.addElement(X509Principal.EmailAddress);

        //
        // create the certificate - version 3
        //
        v3CertGen.reset();

        v3CertGen.setSerialNumber(BigInteger.valueOf(2));
        v3CertGen.setIssuerDN(PrincipalUtil.getSubjectX509Principal(caCert));
        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)));
        v3CertGen.setSubjectDN(new X509Principal(order, attrs));
        v3CertGen.setPublicKey(pubKey);
        v3CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

        //
        // extensions
        //
        v3CertGen.addExtension(
                X509Extensions.SubjectKeyIdentifier,
                false,
                new SubjectKeyIdentifierStructure(pubKey));

        v3CertGen.addExtension(
                X509Extensions.AuthorityKeyIdentifier,
                false,
                new AuthorityKeyIdentifierStructure(caCert));

        v3CertGen.addExtension(
                X509Extensions.BasicConstraints,
                true,
                new BasicConstraints(0));

        X509Certificate cert = v3CertGen.generateX509Certificate(caPrivKey);

        cert.checkValidity(new Date());

        cert.verify(caCert.getPublicKey());

        PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) cert;

        //
        // this is actually optional - but if you want to have control
        // over setting the friendly name this is the way to do it...
        //
        bagAttr.setBagAttribute(
                PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
                new DERBMPString("DSHub Intermediate Certificate"));

        return cert;
    }


    /**
     * we generate a certificate signed by our CA's intermediate certficate
     */
    public static Certificate createCert(
            PublicKey pubKey,
            PrivateKey caPrivKey,
            PublicKey caPubKey)
            throws Exception
    {
        //
        // signers name table.
        //
        Hashtable sAttrs = new Hashtable();
        Vector sOrder = new Vector();

        sAttrs.put(X509Principal.C, "AU");
        sAttrs.put(X509Principal.O, ConfigLoader.HUB_NAME);
        sAttrs.put(X509Principal.OU, "DSHub User Certificate");
        sAttrs.put(X509Principal.EmailAddress, "pietry@death-squad.ro"); //TK owner's email

        sOrder.addElement(X509Principal.C);
        sOrder.addElement(X509Principal.O);
        sOrder.addElement(X509Principal.OU);
        sOrder.addElement(X509Principal.EmailAddress);

        //
        // subjects name table.
        //
        Hashtable attrs = new Hashtable();
        Vector order = new Vector();

        attrs.put(X509Principal.C, "AU");
        attrs.put(X509Principal.O, ConfigLoader.HUB_NAME);
        attrs.put(X509Principal.L, "Direct Connect");
        attrs.put(X509Principal.CN, "mynick"); //TK user's nick
        attrs.put(X509Principal.EmailAddress, "pietry@death-squad.ro");

        order.addElement(X509Principal.C);
        order.addElement(X509Principal.O);
        order.addElement(X509Principal.L);
        order.addElement(X509Principal.CN);
        order.addElement(X509Principal.EmailAddress);

        //
        // create the certificate - version 3
        //
        v3CertGen.reset();

        v3CertGen.setSerialNumber(BigInteger.valueOf(3));
        v3CertGen.setIssuerDN(new X509Principal(sOrder, sAttrs));
        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)));
        v3CertGen.setSubjectDN(new X509Principal(order, attrs));
        v3CertGen.setPublicKey(pubKey);
        v3CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

        //
        // add the extensions
        //
        v3CertGen.addExtension(
                X509Extensions.SubjectKeyIdentifier,
                false,
                new SubjectKeyIdentifierStructure(pubKey));

        v3CertGen.addExtension(
                X509Extensions.AuthorityKeyIdentifier,
                false,
                new AuthorityKeyIdentifierStructure(caPubKey));

        X509Certificate cert = v3CertGen.generateX509Certificate(caPrivKey);

        cert.checkValidity(new Date());

        cert.verify(caPubKey);

        PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) cert;

        //
        // this is also optional - in the sense that if you leave this
        // out the keystore will add it automatically, note though that
        // for the browser to recognise the associated private key this
        // you should at least use the pkcs_9_localKeyId OID and set it
        // to the same as you do for the private key's localKeyId.
        //
        bagAttr.setBagAttribute(
                PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
                new DERBMPString("User's key"));
        bagAttr.setBagAttribute(
                PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
                new SubjectKeyIdentifierStructure(pubKey));

        return cert;
    }


    public boolean storeCerts()
    {

        KeyStore store = genKeyStore();


        FileOutputStream fOut = null;
        try
        {
            fOut = new FileOutputStream(Main.myPath + "key.crt");
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        try
        {
            store.store(fOut, passwd);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public KeyStore loadKeyStore()
    {
        KeyStore store = null;
        try
        {
            store = KeyStore.getInstance("JKS");
        }
        catch (KeyStoreException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            //return null;
        }
        java.io.FileInputStream fis = null;
        try
        {
            fis = new java.io.FileInputStream(Main.myPath + "key.crt");
            store.load(fis, passwd);
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            //return null;
        }
        catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return null;
        }
        catch (CertificateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return null;
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return null;
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return store;
    }


    public KeyStore genKeyStore()
    {
        //
        // store the key and the certificate chain
        //
        KeyStore store = null;
        try
        {
            store = KeyStore.getInstance("JKS");//"PKCS12", "BC");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.debug(e);
        }

        try
        {
            store.load(null, null);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.debug(e);
        }

        //
        // if you haven't set the friendly name and local key id above
        // the name below will be the name of the key
        //
        try
        {
            store.setKeyEntry("User's Key", privkey, passwd, chain);
        }
        catch (KeyStoreException e)
        {
            // TODO Auto-generated catch block
            log.debug(e.getMessage());
        }

        return store;
    }


    public KeyStore getKeyStore()
    {
        return ks;
    }


    public boolean recreateKeysCerts()
    {
        KeyManager km = new KeyManager();
        pubkey = km.getPublicKey();
        privkey = km.getPrivateKey();


        chain = new Certificate[1];

        try
        {
            log.info("Creating hub certificate...");
            chain[0] = createMasterCert(pubkey, privkey);
            //createIntermediateCert(km.getPublicKey(), km.getPrivateKey(), (X509Certificate)x);
            //   chain[0] = createCert(km.getPublicKey(), km.getPrivateKey(), km.getPublicKey());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.debug(e);
            return false;
        }

        //
        // add the friendly name for the private key
        //
        //  PKCS12BagAttributeCarrier   bagAttr = (PKCS12BagAttributeCarrier)km.getPrivateKey();

        //
        // this is also optional - in the sense that if you leave this
        // out the keystore will add it automatically, note though that
        // for the browser to recognise which certificate the private key
        // is associated with you should at least use the pkcs_9_localKeyId
        // OID and set it to the same as you do for the private key's
        // corresponding certificate.
        //
        /*bagAttr.setBagAttribute(
                  PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
                  new DERBMPString("User's Key"));
              try {
                  bagAttr.setBagAttribute(
                      PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
                      new SubjectKeyIdentifierStructure(km.getPublicKey()));
              } catch (CertificateParsingException e1) {
                  e1.printStackTrace();
              }
        */
        storeCerts();

        //this.ks=ks;
        ks = genKeyStore();
        return (ks != null);
    }


    public CertManager()
    {
        Security.addProvider(new BouncyCastleProvider());
        //this.km=km;
        ks = loadKeyStore();

        if (ks == null)
        {
            // recreateKeysCerts();
        }
        else
        {
            //this.ks=genKeyStore();
        }
    }
}




