package br.ufpe.cin.mergers.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.CharStreams;

public final class CSDiffRunner {
    private static final String CSDiffScriptPath = "/dependencies/csdiff.sh";

    public static void run(Path left, Path base, Path right, Path CSDiffOutput, Path diff3Output) throws IOException, InterruptedException {
        Path script = createScriptFile();
        
        String[] command = buildCommand(script, left, base, right, CSDiffOutput, diff3Output);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        Files.delete(script);
    }

    private static Path createScriptFile() throws IOException {
        Path file = Files.createTempFile("csdiff", ".sh");
        String content = CharStreams.toString(new InputStreamReader(CSDiffRunner.class.getResourceAsStream(CSDiffScriptPath)));
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static String[] buildCommand(Path script, Path left, Path base, Path right, Path CSDiffOutput, Path diff3Output) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().trim().startsWith("windows");

        String[] command = new String[7];
        command[0] = isWindows ? "" : "sh";
        command[1] = script.toAbsolutePath().toString();
        command[2] = left.toAbsolutePath().toString();
        command[3] = base.toAbsolutePath().toString();
        command[4] = right.toAbsolutePath().toString();
        command[5] = CSDiffOutput.toAbsolutePath().toString();
        command[6] = diff3Output.toAbsolutePath().toString();

        return command;
    }
}
