import ast.ASTNode;
import ast.ASTVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * This code is part of the lab exercises for the Compilers course at Harokopio
 * University of Athens, Dept. of Informatics and Telematics.
 */
public class Compiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.info("Usage : java Compiler [ --encoding <name> ] <inputfile(s)>");
        } else {
            int firstFilePos = 0;
            String encodingName = "UTF-8";
            if (args[0].equals("--encoding")) {
                firstFilePos = 2;
                encodingName = args[1];
                try {
                    java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid?
                } catch (Exception e) {
                    LOGGER.error("Invalid encoding '" + encodingName + "'");
                    return;
                }
            }
            for (int i = firstFilePos; i < args.length; i++) {
                Lexer scanner = null;
                try {
                    java.io.FileInputStream stream = new java.io.FileInputStream(args[i]);
                    LOGGER.info("Scanning file " + args[i]);
                    java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
                    scanner = new Lexer(reader);

                    // parse
                    parser p = new parser(scanner);
                    ASTNode rootUnit = (ASTNode) p.parse().value;
                    LOGGER.info("Constructed AST");

                    // keep global instance of program
                    Registry.getInstance().setRoot(rootUnit);


                    // build symbol table
                    ASTVisitor symTableVisitor = new SymTableBuilderASTVisitor();
                    rootUnit.accept(symTableVisitor);
                    LOGGER.info("Constructed symbol tables");

                    //build local variables index
                    LOGGER.info("Building local variables index");
                    rootUnit.accept(new LocalIndexBuilderASTVisitor());

                    // collect symbols
                    ASTVisitor collectSymbolsVisitor = new CollectSymbolsASTVisitor();
                    rootUnit.accept(collectSymbolsVisitor);
                    LOGGER.info("Semantic pass 1 done (symbols collected)");

                    // calculate types
                    ASTVisitor calculateTypesVisitor = new CalculateTypesASTVisitor();
                    rootUnit.accept(calculateTypesVisitor);
                    LOGGER.info("Semantic pass 2 done (calculated types)");


                    // print rootUnit
                    /*LOGGER.info("Input:");
                    ASTVisitor printVisitor = new PrintASTVisitor();
                    rootUnit.accept(printVisitor);*/


                    // convert to java bytecode
                    LOGGER.info("Bytecode:");
                    BytecodeGeneratorASTVisitor bytecodeVisitor = new BytecodeGeneratorASTVisitor();
                    rootUnit.accept(bytecodeVisitor);
                    ClassNode cn = bytecodeVisitor.getClassNode();
                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
                    TraceClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));

                    cn.accept(cv);

                    // get code
                    byte[] code = cw.toByteArray();


                    // update to file
                    //LOGGER.info("Writing class to file Program.class");
                    FileOutputStream fos = new FileOutputStream("Program.class");
                    fos.write(code);
                    fos.close();
                    LOGGER.info("Compilation done");

                    // instantiate class
                    //LOGGER.info("Loading class Program.class");

                    bytecodeVisitor.cl.register("Program", code);
                    Class<?> customLanguageClass = bytecodeVisitor.cl.loadClass("Program");
                    /*ReloadingClassLoader rcl = new ReloadingClassLoader(ClassLoader.getSystemClassLoader());
                    rcl.register("Program", code);
                    Class<?> customLanguageClass = rcl.loadClass("Program");*/

                    // run main method
                    Method meth = customLanguageClass.getMethod("main");
                    //String[] params = null;
                    LOGGER.info("Executing");
                    meth.invoke(null);


                    /*LOGGER.info("3-address code:");
                    IntermediateCodeASTVisitor threeAddressVisitor = new IntermediateCodeASTVisitor();
                    rootUnit.accept(threeAddressVisitor);
                    String intermediateCode = threeAddressVisitor.getProgram().emit();
                    //System.out.println("--instructions--");
                    System.out.println(intermediateCode);*/


                } catch (java.io.FileNotFoundException e) {
                    LOGGER.error("File not found : \"" + args[i] + "\"");
                } catch (java.io.IOException e) {
                    LOGGER.error("IO error scanning file \"" + args[i] + "\"");
                    LOGGER.error(e.toString());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
    }

}
