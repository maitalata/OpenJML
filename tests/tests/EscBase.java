package tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import junit.framework.AssertionFailedError;

import org.jmlspecs.openjml.JmlSpecs;

import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;


public abstract class EscBase extends JmlTestCase {

    static String z = java.io.File.pathSeparator;
    static String testspecpath1 = "$A"+z+"$B";
    static String testspecpath;
    int expectedExit = 0;
    int expectedErrors = 0;

    protected void setUp() throws Exception {
        testspecpath = testspecpath1;
        collector = new FilteredDiagnosticCollector<JavaFileObject>(true);
        super.setUp();
        options.put("-specs",   testspecpath);
        options.put("-esc",   "");
        main.register(context);
        specs = JmlSpecs.instance(context);
        Log.instance(context).multipleErrors = true;
        expectedExit = 0;
        expectedErrors = 0;
        print = false;
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        specs = null;
    }

//    public void helpFailure(String failureMessage, String s, Object ... list) {
//        noExtraPrinting = true;
//        boolean failed = false;
//        try {
//            helpTC(s,list);
//        } catch (AssertionFailedError a) {
//            failed = true;
//            assertEquals("Failure report wrong",failureMessage,a.getMessage());
//        }
//        if (!failed) fail("Test Harness failed to report an error");
//    }

//    public void helpTC(String s, Object ... list) {
//        helpTCX(null,s,list);
//    }
//
//    public void helpTCF(String filename,String s, Object ... list) {
//        helpTCX(filename,s,list);
//    }
    
    public void helpTCX(String classname, String s, Object... list) {
        try {
            expectedErrors = list.length/2;
            String filename = classname.replace(".","/")+".java";
            JavaFileObject f = new TestJavaFileObject(filename,s);
            
            Log.instance(context).useSource(f);
            List<JavaFileObject> files = List.of(f);
            int ex = main.compile(new String[]{}, context, files, null);
            
            if (print || collector.getDiagnostics().size()!=expectedErrors) printErrors();
            assertEquals("Errors seen",expectedErrors,collector.getDiagnostics().size());
            for (int i=0; i<expectedErrors; i++) {
                assertEquals("Error " + i, list[2*i].toString(), collector.getDiagnostics().get(i).toString());
                assertEquals("Error " + i, ((Integer)list[2*i+1]).intValue(), collector.getDiagnostics().get(i).getColumnNumber());
            }
            if (ex != expectedExit) fail("Compile ended with exit code " + ex);
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
            fail("Exception thrown while processing test: " + e);
        } catch (AssertionFailedError e) {
            if (!print && !noExtraPrinting) printErrors();
            throw e;
        }
    }

    /** Used to add a pseudo file to the file system. Note that for testing, a 
     * typical filename given here might be #B/A.java, where #B denotes a 
     * mock directory on the specification path
     * @param filename the name of the file, including leading directory components 
     * @param content the String constituting the content of the pseudo-file
     */
    protected void addMockFile(/*@ non_null */ String filename, /*@ non_null */String content) {
        try {
            addMockFile(filename,new TestJavaFileObject(new URI("file:///" + filename),content));
        } catch (Exception e) {
            fail("Exception in creating a URI: " + e);
        }
    }

    /** Used to add a pseudo file to the file system. Note that for testing, a 
     * typical filename given here might be #B/A.java, where #B denotes a 
     * mock directory on the specification path
     * @param filename the name of the file, including leading directory components 
     * @param file the JavaFileObject to be associated with this name
     */
    protected void addMockFile(String filename, JavaFileObject file) {
        specs.addMockFile(filename,file);
    }


}