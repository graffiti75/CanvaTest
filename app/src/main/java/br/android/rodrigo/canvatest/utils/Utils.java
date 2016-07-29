package br.android.rodrigo.canvatest.utils;

/**
 * Utils.java.
 *
 * @author Rodrigo Cericatto
 * @since Jul 24, 2016
 */
public class Utils {

    //--------------------------------------------------
    // String Methods
    //--------------------------------------------------

    public static Boolean isEmpty(String text) {
        Boolean result = true;
        Boolean isNull = (text == null);
        if (!isNull) {
            Boolean isZeroLength = (text.length() <= 0);
            Boolean isEmpty = (text.equals(""));
            Boolean contentOfTextIsLiteralNull = (text.equals("null"));
            result = isNull || isZeroLength || isEmpty || contentOfTextIsLiteralNull;
        }
        return result;
    }
}