package com.example.luisp.ed2_lab_01;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Huffman {

    private Huffman() {}

    private static class HuffmanNode {
        char ch;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        HuffmanNode(char ch, int frequency,  HuffmanNode left,  HuffmanNode right) {
            this.ch = ch;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
    }
    private static Integer SizeFile;
    private static String  File;

    public static Integer FileCompress(){
        SizeFile = File.getBytes().length;
        return  SizeFile;

    }

    private static class HuffManComparator implements Comparator <HuffmanNode> {
        @Override
        public int compare(HuffmanNode node1, HuffmanNode node2) {
            return node1.frequency - node2.frequency;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void compress(String sentence) throws FileNotFoundException, IOException {
        if (sentence == null) {
            throw new NullPointerException("Input sentence cannot be null.");
        }
        if (sentence.length() == 0) {
            throw new IllegalArgumentException("The string should atleast have 1 character.");
        }

        final Map<Character, Integer> charFreq = getCharFrequency(sentence);
        final HuffmanNode root = buildTree(charFreq);
        final Map<Character, String> charCode = generateCodes(charFreq.keySet(), root);
        final String encodedMessage = encodeMessage(charCode, sentence);
        serializeTree(root);
        serializeMessage(encodedMessage);

    }

    private static Map<Character, Integer> getCharFrequency(String sentence) {
        final Map<Character, Integer> map = new HashMap<Character, Integer>();

        for (int i = 0; i < sentence.length(); i++) {
            char ch = sentence.charAt(i);
            if (!map.containsKey(ch)) {
                map.put(ch, 1);
            } else {
                int val = map.get(ch);
                map.put(ch, ++val);
            }
        }

        return map;
    }



    private static HuffmanNode buildTree(Map<Character, Integer> map) {
        final Queue<HuffmanNode> nodeQueue = createNodeQueue(map);

        while (nodeQueue.size() > 1) {
            final HuffmanNode node1 = nodeQueue.remove();
            final HuffmanNode node2 = nodeQueue.remove();
            HuffmanNode node = new HuffmanNode('\0', node1.frequency + node2.frequency, node1, node2);
            nodeQueue.add(node);
        }

        // remove it to prevent object leak.
        return nodeQueue.remove();
    }

    private static Queue<HuffmanNode> createNodeQueue(Map<Character, Integer> map) {
        final Queue<HuffmanNode> pq = new PriorityQueue<HuffmanNode>(11, new HuffManComparator());
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue(), null, null));
        }
        return pq;
    }

    private static Map<Character, String> generateCodes(Set<Character> chars, HuffmanNode node) {
        final Map<Character, String> map = new HashMap<Character, String>();
        doGenerateCode(node, map, "");
        return map;
    }


    private static void doGenerateCode(HuffmanNode node, Map<Character, String> map, String s) {
        if (node.left == null && node.right == null) {
            map.put(node.ch, s);
            return;
        }
        doGenerateCode(node.left, map, s + '0');
        doGenerateCode(node.right, map, s + '1' );
    }


    private static String encodeMessage(Map<Character, String> charCode, String sentence) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < sentence.length(); i++) {
            stringBuilder.append(charCode.get(sentence.charAt(i)));
        }
        File = stringBuilder.toString();
        return stringBuilder.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void serializeTree(HuffmanNode node) throws FileNotFoundException, IOException {
        final BitSet bitSet = new BitSet();

        File nuevaCarpeta;
        nuevaCarpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , "/MisCompresiones");
        if (!nuevaCarpeta.exists()){

              nuevaCarpeta.mkdirs();
        }

        File f;
        File j;
        f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones","tree.huff");
        j = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones","Char.huff");
        try (ObjectOutputStream oosTree = new ObjectOutputStream(new FileOutputStream(f))) {
            try (ObjectOutputStream oosChar = new ObjectOutputStream(new FileOutputStream(j))) {
                IntObject o = new IntObject();
                preOrder(node, oosChar, bitSet, o);
                bitSet.set(o.bitPosition, true);
                oosTree.writeObject(bitSet);
            }
        }
    }

    private static class IntObject {
        int bitPosition;
    }


    private static void preOrder(HuffmanNode node, ObjectOutputStream oosChar, BitSet bitSet, IntObject intObject) throws IOException {
        if (node.left == null && node.right == null) {
            bitSet.set(intObject.bitPosition++, false);
            oosChar.writeChar(node.ch);
            return;
        }
        bitSet.set(intObject.bitPosition++, true);
        preOrder(node.left, oosChar, bitSet, intObject);

        bitSet.set(intObject.bitPosition++, true);
        preOrder(node.right, oosChar, bitSet, intObject);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void serializeMessage(String message) throws IOException {
        final BitSet bitSet = getBitSet(message);
        File f;
        f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones", "encodedMessage.huff");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))){

            oos.writeObject(bitSet);
        }
    }

    private static BitSet getBitSet(String message) {
        final BitSet bitSet = new BitSet();
        int i = 0;
        for (i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '0') {
                bitSet.set(i, false);
            } else {
                bitSet.set(i, true);
            }
        }
        bitSet.set(i, true); // bit para saber el tamaño
        return bitSet;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String expand() throws FileNotFoundException, ClassNotFoundException, IOException {
        final HuffmanNode root = deserializeTree();
        return decodeMessage(root);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static HuffmanNode deserializeTree() throws FileNotFoundException, IOException, ClassNotFoundException {

        File f;
        File j;
        f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones", "tree.txt");
        j = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones", "Char.txt");
        try (ObjectInputStream oisBranch = new ObjectInputStream(new FileInputStream(f))) {
            try (ObjectInputStream oisChar = new ObjectInputStream(new FileInputStream(j))) {
                final BitSet bitSet = (BitSet) oisBranch.readObject();
                return preOrder(bitSet, oisChar, new IntObject());
            }
        }
    }


    private static HuffmanNode preOrder(BitSet bitSet, ObjectInputStream oisChar, IntObject o) throws IOException {

        final HuffmanNode node = new HuffmanNode('\0', 0, null, null);


        if (!bitSet.get(o.bitPosition)) {
            o.bitPosition++;
            node.ch = oisChar.readChar();
            return node;
        }

        o.bitPosition = o.bitPosition + 1;
        node.left = preOrder(bitSet, oisChar, o);

        o.bitPosition = o.bitPosition + 1;
        node.right = preOrder(bitSet, oisChar, o);

        return node;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String decodeMessage(HuffmanNode node) throws FileNotFoundException, IOException, ClassNotFoundException {
        File f;
        f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/MisCompresiones", "encodedMessage.txt");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            final BitSet bitSet = (BitSet) ois.readObject();
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < (bitSet.length() - 1);) {
                HuffmanNode temp = node;

                while (temp.left != null) {
                    if (!bitSet.get(i)) {
                        temp = temp.left;
                    } else {
                        temp = temp.right;
                    }
                    i = i + 1;
                }
                stringBuilder.append(temp.ch);
            }
            return stringBuilder.toString();
        }
    }


}
