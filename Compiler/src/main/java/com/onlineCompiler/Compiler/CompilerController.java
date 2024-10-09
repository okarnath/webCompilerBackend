package com.onlineCompiler.Compiler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RequestMapping("/api")
public class CompilerController  {

    @PostMapping("/compile")
    public ResponseEntity<String> complieCode(@RequestBody CodeRequest request) {

        String code = request.getCode();
        String language = request.getLanguage();
        String result ="";
        try {
           if (language.toLowerCase().equals("java")){
               result = compileJava(code);
               return ResponseEntity.ok(result);
           }
           else if (language.toLowerCase().equals("python")) {
               result = compilePython(code);
               return ResponseEntity.ok(result);
           }
           else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in the responseEntity code");

        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: "+ e.getMessage());
        }
    }


    public String compileJava(String code) throws Exception {
      ProcessBuilder processBuilder;
      String fileName = "Main";
      String output = "";

      //Delete the old class file
        delteOldClassFile(fileName);
      //create a writeToFile Function which takes code as a paramereter and the filename.
      writeToFile(code, fileName+ ".java");

      //Compile the java code
        processBuilder = new ProcessBuilder("javac", fileName+ ".java");
        Process newCompileProcess = processBuilder.start();
        newCompileProcess.waitFor();

      // Run the java code
       processBuilder  = new ProcessBuilder("java", fileName);
        Process processForExecution = processBuilder.start();

        String error = getProcessOutput(processForExecution.getErrorStream());

        if (!error.isEmpty()) {
            return "Runtime Error: \n" + error;
        }

      // Capture the process output
      output = getProcessOutput(processForExecution.getInputStream());
      return output;
    }


    public String compilePython(String code) throws IOException, InterruptedException {
        ProcessBuilder processBuilder;
        String fileName = "test.py";
        String output;
        String error;

        writeToFile(code, fileName);
        processBuilder = new ProcessBuilder("python", fileName);
        System.out.println("The name of the file to b executed: "+ fileName);
        Process runProcess = processBuilder.start();
        int exitCode = runProcess.waitFor();
        System.out.println("Process exited with code: " + exitCode);

        output = getProcessOutput(runProcess.getInputStream());
        error = getProcessOutput(runProcess.getErrorStream());

        System.out.println("Error Output: \n"+ error);
        if (!error.isEmpty()){
            return "Runtime Error: \n"+ error;
        }
        System.out.println("Output of the python file: \n"+ output);
        return output;
    }


    // getProcessOutput (reads the output after the processExcute is done)
    public String getProcessOutput(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null){
            output.append(line).append("\n");
            System.out.println("The lines are being readed");
        }
        return output.toString();
    }

    //Writer Function
    public void writeToFile(String code, String fileName){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.write(code);
            System.out.println("Code Written to" + fileName +" : "+ code );
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // To delete the olfClass in java

    public void delteOldClassFile(String fileName){
        String classFileName = fileName+".class";

        File classFile = new File(classFileName);

        if (classFile.exists()){
            if (classFile.delete()){
                System.out.println("Old class file" + classFileName + "deleted successfully");
            }else {
                System.out.println("Failed to delete the old class file");
            }
        }else {
            System.out.println("No old class file to delete");
        }
    }
}
