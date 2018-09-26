package com.example.luisp.ed2_lab_01;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.*;

public class LZW {
    private static String File_Input = null;
    private static double MAX_TABLE_SIZE; //Max Table size is based on the bit length input.
    private static String LZWfilename;

    public static void Encode_string(String input_string, double Bit_Length) throws IOException {



        MAX_TABLE_SIZE = Math.pow(2, Bit_Length);

        double table_Size =  255;

        Map<String, Integer> TABLE = new HashMap<String, Integer>();

        for (int i = 0; i < 255 ; i++)
            TABLE.put("" + (char) i, i);

        String initString = "";

        List<Integer> encoded_values = new ArrayList<Integer>();

        for (char symbol : input_string.toCharArray()) {
            String Str_Symbol = initString + symbol;
            if (TABLE.containsKey(Str_Symbol))
                initString = Str_Symbol;
            else {
                encoded_values.add(TABLE.get(initString));

                if(table_Size < MAX_TABLE_SIZE)
                    TABLE.put(Str_Symbol, (int) table_Size++);
                initString = "" + symbol;
            }
        }

        if (!initString.equals(""))
            encoded_values.add(TABLE.get(initString));

        CreateLZWfile(encoded_values);

    }


/*
@param encoded_values , This hold the encoded text.
@throws IOException
*/

    private static void CreateLZWfile(List<Integer> encoded_values) throws IOException {

        BufferedWriter out = null;

        File F = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones","codelzw.txt");

        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(F),"UTF_16BE")); //The Charset UTF-16BE is used to write as 16-bit compressed file

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {

           for (int i = 1 ; i < encoded_values.size();i++){
                out.write(encoded_values.get(i));
           }
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.flush();
        out.close();
    }

    public static void Decode_String(String file_Input2, double bit_Length) throws IOException {


        MAX_TABLE_SIZE = Math.pow(2, bit_Length);


        List<Integer> get_compress_values = new ArrayList<Integer>();
        int table_Size = 255;


        BufferedReader br = null;
        InputStream inputStream  = new FileInputStream(file_Input2);
        Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-16BE"); // The Charset UTF-16BE is used to read the 16-bit compressed file.

        br = new BufferedReader(inputStreamReader);

        double value=0;

        // reads to the end of the stream
        while((value = br.read()) != -1)
        {
            get_compress_values.add((int) value);
        }

        br.close();

        Map<Integer, String> TABLE = new HashMap<Integer, String>();
        for (int i = 0; i < 255; i++)
            TABLE.put(i, "" + (char) i);

        String Encode_values = "" + (char) (int) get_compress_values.remove(0);

        StringBuffer decoded_values = new StringBuffer(Encode_values);

        String get_value_from_table = null;
        for (int check_key : get_compress_values) {

            if (TABLE.containsKey(check_key))
                get_value_from_table = TABLE.get(check_key);
            else if (check_key == table_Size)
                get_value_from_table = Encode_values + Encode_values.charAt(0);

            decoded_values.append(get_value_from_table);

            if(table_Size < MAX_TABLE_SIZE )
                TABLE.put(table_Size++, Encode_values + get_value_from_table.charAt(0));

            Encode_values = get_value_from_table;
        }

        Create_decoded_file(decoded_values.toString());



    }

/*
@param String , This hold the decoded text.
@throws IOException
*/

    private static void Create_decoded_file(String decoded_values) throws IOException {


        File F = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones","decodedlzw.txt");

        FileWriter writer = new FileWriter(F, true);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);


        try {

            bufferedWriter.write(decoded_values);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        bufferedWriter.flush();

        bufferedWriter.close();
    }
}
