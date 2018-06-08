package com.zstok.infraestrutura.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static void criarToast(Context context, String texto){
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
    public static boolean verificaExpressaoRegularEmail(String email) {

        if (!email.isEmpty()) {
            String excecoes = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
            Pattern pattern = Pattern.compile(excecoes);
            Matcher matcher = pattern.matcher(email);

            return matcher.matches();//se igual a true tem alguma expressão irregular.
        }
        return false;
    }
    public static void mascaraTelefone(EditText editText){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraTelefone(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraCpf(EditText editText){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraCpf(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraCnpj(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN/NNNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraDataNascimento(EditText editText){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraDataNascimento(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static String removerMascara(String str){
        return str.replaceAll("\\D", "");
    }
    //Convertendo string para bitmap
    //By: http://androidtrainningcenter.blogspot.com.br/2012/03/how-to-convert-string-to-bitmap-and.html
    public static Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    //Convertendo bitMap para string
    //By: http://androidtrainningcenter.blogspot.com.br/2012/03/how-to-convert-string-to-bitmap-and.html
    public static String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress (Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b = baos.toByteArray ();
        return Base64.encodeToString (b, Base64.DEFAULT);
    }
}
