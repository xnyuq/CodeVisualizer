package org.example.View;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.utils.SourceRoot;
import org.example.PlantUMLParser.Controller.PlantUMLVisitor;
import org.example.PlantUMLParser.Controller.SequenceGenerator;
import org.example.PlantUMLParser.Controller.XsdConverterVisitor;
import org.example.PlantUMLParser.Controller.XsdToJavaConverter;
import org.example.PlantUMLParser.Model.PlantUMLDiag;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.PlantUMLParser.Controller.PlantUMLHelper.renderPlantUMLAsPNG;

public class MainGUI extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton javaToXsdBtn;
    private JButton xsdToJavaBtn;
    private JTextField xsdToJavaSrcTxt;
    private JTextField javaToXsdSrcTxt;
    private JTextField classSrcPathTxt;
    private JTextField entryTxt;
    private JButton classGenBtn;
    private JTextField seqSrcPathTxt;
    private JButton seqGenBtn;
    private JLabel statusTxt;
    private JTextField textField1;

    public MainGUI() {
        add(panel1);
//        textField1.setForeground(Color.GRAY);
//        textField1.setText("Enter source folder path");
//        textField1.addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                textField1.setText("");
//                textField1.setForeground(Color.BLACK);
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//                if (textField1.getText().isEmpty()) {
//                    textField1.setForeground(Color.GRAY);
//                    textField1.setText("Enter source folder path");
//                }
//
//            }
//        });
        setTitle("Java Visualizer");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        classGenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourcePath = classSrcPathTxt.getText();
                String outputPath = "D:\\output\\";
                try {
                    generateClassDiagram(sourcePath, outputPath);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        seqGenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String sourcePath = seqSrcPathTxt.getText();
                String outputPath = "D:\\output\\";

                Path pathToSource = Paths.get(sourcePath);
                SourceRoot sourceRoot = new SourceRoot(pathToSource);
                try {
                    sourceRoot.tryToParse();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

                PlantUMLDiag plantUML = new PlantUMLDiag();

                Map<String, Path> classPathMap = new HashMap<>();
                // Maintain a list of project's class names for later use
                for (CompilationUnit cu : compilationUnits) {
                    String className1 = cu.getPrimaryTypeName().get();
                    plantUML.addClassName(className1);
                    Path filePath = cu.getStorage().get().getPath();
                    classPathMap.put(className1, filePath);
                }
                String entry = entryTxt.getText();
                String className = entry.substring(0, entry.indexOf("."));
                String methodName = entry.substring(entry.indexOf(".") + 1);

                SequenceGenerator sequenceGenerator = new SequenceGenerator(sourcePath, classPathMap, className, methodName, "D:\\output\\seq.txt");
                try {
                    sequenceGenerator.generate();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("File generated at " + sequenceGenerator.getOutputPath());
                statusTxt.setText("File generated at " + sequenceGenerator.getOutputPath());

            }
        });
        javaToXsdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String srcPath = javaToXsdSrcTxt.getText();

                String outputFolder = "D:\\output\\xsdGenerated";
                Path pathToSource = Paths.get(srcPath);
                SourceRoot sourceRoot = new SourceRoot(pathToSource);
                try {
                    sourceRoot.tryToParse();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

                PlantUMLDiag plantUML = new PlantUMLDiag();

                Map<String, Path> classPathMap = new HashMap<>();
                // Maintain a list of project's class names for later use
                for (CompilationUnit cu : compilationUnits) {
                    String className1 = cu.getPrimaryTypeName().get();
                    plantUML.addClassName(className1);
                    Path filePath = cu.getStorage().get().getPath();
                    classPathMap.put(className1, filePath);
                }

                for (CompilationUnit cu : compilationUnits) {
                    String className1 = cu.getPrimaryTypeName().get();
                    XsdConverterVisitor xsdVisitor = new XsdConverterVisitor(outputFolder + className1 + ".xsd");
                    statusTxt.setText("Parsing " + className1 + "...");
                    xsdVisitor.visit(cu, plantUML.classNames);
                    xsdVisitor.closeWriter();
                }
                statusTxt.setText("XSD files generated at " + outputFolder);
            }
        });
        xsdToJavaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceFolder = xsdToJavaSrcTxt.getText();
                String outputFolder = "D:\\output\\sourceGenerated";
                File folder = new File(sourceFolder);

                // Check if the source folder exists
                if (!folder.exists() || !folder.isDirectory()) {
                    System.err.println("Source folder doesn't exist or is not a directory.");
                    return;
                }

                // Get a list of all XSD files in the source folder
                File[] xsdFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xsd"));

                if (xsdFiles == null || xsdFiles.length == 0) {
                    System.err.println("No XSD files found in the source folder.");
                    return;
                }

                // Process each XSD file
                for (File xsdFile : xsdFiles) {
                    processXsdFile(xsdFile.getAbsolutePath(), outputFolder);
                }

            }
        });
    }

    public static void main(String[] args) {
        new MainGUI();
    }

    private void generateClassDiagram(String sourcePath, String outputPath) throws IOException {
        Path pathToSource = Paths.get(sourcePath);
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

        PlantUMLVisitor umlVisitor = new PlantUMLVisitor();
        PlantUMLDiag plantUML = new PlantUMLDiag();

        Map<String, Path> classPathMap = new HashMap<>();
        // Maintain a list of project's class names for later use
        for (CompilationUnit cu : compilationUnits) {
            String className1 = cu.getPrimaryTypeName().get();
            plantUML.addClassName(className1);
            Path filePath = cu.getStorage().get().getPath();
            classPathMap.put(className1, filePath);
        }

        // Parse the source code and generate PlantUML code
        for (CompilationUnit cu : compilationUnits) {
            umlVisitor.visit(cu, plantUML);
        }

        // Generate PlantUML code
        String plantUMLCode = plantUML.toString();
//        System.out.println(plantUMLCode);

        // Generate PlantUML diagram
        renderPlantUMLAsPNG(plantUMLCode);
    }

    private static void processXsdFile(String xsdFilePath, String outputFolder) {
        // Your existing code to convert a single XSD file
        XsdToJavaConverter xsdToJavaConverter = new XsdToJavaConverter();
        ClassOrInterfaceDeclaration classOrInterface = xsdToJavaConverter.generateClassFromXml(xsdFilePath);
        System.out.println(classOrInterface.toString());

        // Save the generated class source code to a file in the output folder
        saveSourceToFile(classOrInterface, outputFolder);
    }

    private static void saveSourceToFile(ClassOrInterfaceDeclaration classOrInterface, String outputFolder) {
        // Create the output folder if it doesn't exist
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Construct the output file path based on the class name
        String className = classOrInterface.getNameAsString();
        String outputPath = outputFolder + className + ".java";

        // Write the class source code to the output file
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(classOrInterface.toString());
            System.out.println("Generated source code saved to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error saving generated source code to file: " + e.getMessage());
        }
    }
}
