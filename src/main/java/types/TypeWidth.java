/**
 * Helper class that returns the width of each primitive type in bytes
 * for array element positioning**/

package types;

import org.objectweb.asm.Type;

public class TypeWidth {


    public static int getTypeWidth(Type elementType){
        if(elementType == Type.BOOLEAN_TYPE)
            return 1;
        if(elementType == Type.CHAR_TYPE)
            return 1;
        if(elementType == Type.INT_TYPE)
            return 4;
        if(elementType == Type.FLOAT_TYPE)
            return 4;

        return 0;
    }


}
