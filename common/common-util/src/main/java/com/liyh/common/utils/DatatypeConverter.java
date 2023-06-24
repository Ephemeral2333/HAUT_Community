package com.liyh.common.utils;

public class DatatypeConverter {
     public static String printBase64Binary(byte[] input) {
         return javax.xml.bind.DatatypeConverter.printBase64Binary(input);
     }

     public static byte[] parseBase64Binary(String input) {
         return javax.xml.bind.DatatypeConverter.parseBase64Binary(input);
     }
}
