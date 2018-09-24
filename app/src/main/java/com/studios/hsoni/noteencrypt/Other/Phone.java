package com.studios.hsoni.noteencrypt.Other;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyChain;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.studios.hsoni.noteencrypt.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyStoreBuilderParameters;
import javax.security.auth.x500.X500Principal;

//Holds all of the encrypted stuff stored by the user
public class Phone{
    private Context context;
    SharedPreferences sharedPref;

    KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    public Phone(Context Context) throws KeyStoreException {
        context = Context;
        sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.titleOfDocument), Context.MODE_PRIVATE);

    }


    public ArrayList<NoteEntry> loadData() {
        Map<String, String> storedStuff = (Map<String, String>) sharedPref.getAll();
        ArrayList storedNotes = new ArrayList<>();
        for(Map.Entry<String, String> entry: storedStuff.entrySet() ){
            if(storedNotes == null){
                return null;
            }
            storedNotes.add(new NoteEntry(entry.getKey(),  null,entry.getValue()));
        }
        return storedNotes;
    }
    public void updateData(ArrayList<NoteEntry> noteEntries){
       SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.titleOfDocument), Context.MODE_PRIVATE);
        Map<String, String> storedStuff = (Map<String, String>) sharedPref.getAll();
        for(Map.Entry<String, String> entry: storedStuff.entrySet() ){
            NoteEntry note = new NoteEntry(entry.getKey(),  null,entry.getValue());
          if(!noteEntries.contains(note)){
              noteEntries.add(note);
          }
        }
    }

    public void storeNote(String Content, String Password) throws DigestException {
        try {
            NoteEntry entry = encryptMessage(Content,Password);
            String hashPassword = this.hashPassword(entry.getEntryPassword());
            Log.i("Password Hashed", hashPassword);
            sharedPref.edit().putString(entry.getEntryData(), hashPassword).apply();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    public void deleteNote(NoteEntry noteEntry){
        sharedPref.edit().remove(noteEntry.getEntryData()).apply();
    }
    public void nukeNotes(){
        sharedPref.edit().clear().apply();
    }

    public NoteEntry encryptMessage(String Message, String Password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 5);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(Password)
                .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            generator.initialize(spec);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        assert generator != null;
        KeyPair keyPair = generator.generateKeyPair();
        keyPair.getPublic();

        Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        input.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(
                outputStream, input);
        cipherOutputStream.write(Message.getBytes("UTF-8"));
        cipherOutputStream.close();
        byte [] vals = outputStream.toByteArray();
        return new NoteEntry(Base64.encodeToString(vals, Base64.DEFAULT), vals, Password);
    }

    public String decryptyt(String Password, String answer) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(Password, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(answer, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }
            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            return finalText;
        } catch (Exception e) {
            MDToast.makeText(context, "Exception " + e.getMessage() + " occured", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
            e.printStackTrace();
            return "An Error Occured";
        }
    }
    private String hashPassword(String hashPassword) throws NoSuchAlgorithmException, DigestException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        try {
            return Base64.encodeToString(md.digest(hashPassword.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            MDToast.makeText(context, "An error occurred",MDToast.LENGTH_LONG, MDToast.TYPE_ERROR ).show();
            return "null";

        }

    }
    public boolean isCorrectPassword(String Password, String hashedPassword)  {
        String hashed = null;
        try {
            hashed = this.hashPassword(Password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (DigestException e) {
            e.printStackTrace();
        }
        if (hashed != null) {
            return hashed.equals(hashedPassword);
        }
        return false;
    }

}
