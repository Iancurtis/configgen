package configgen.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;

public class TabPrintStream implements Closeable {
    public final PrintStream ps;

    public TabPrintStream(PrintStream ps) {
        this.ps = ps;
    }

    public void println() {
        ps.println();
    }

    public void println(String str) {
        ps.println(str);
    }

    public void println1(String str) {
        ps.println("    " + str);
    }

    public void println2(String str) {
        ps.println("        " + str);
    }

    public void println3(String str) {
        ps.println("            " + str);
    }

    public void println4(String str) {
        ps.println("                " + str);
    }

    public void println5(String str) {
        ps.println("                    " + str);
    }

    public void println6(String str) {
        ps.println("                        " + str);
    }

    public void println7(String str) {
        ps.println("                            " + str);
    }

    @Override
    public void close() throws IOException {
        ps.close();
    }
}
