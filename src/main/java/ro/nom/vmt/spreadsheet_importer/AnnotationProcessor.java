package ro.nom.vmt.spreadsheet_importer;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import com.google.auto.service.AutoService;
import ro.nom.vmt.spreadsheet_importer.annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@SupportedAnnotationTypes("ro.nom.vmt.spreadsheet_importer.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Started processing annotations");
        try {

            Map<Element, ImportableClass> importableClassMap = new HashMap<>();

            //find importable classes
            Set<? extends Element> annotatedImportables = environment.getElementsAnnotatedWith(Importable.class);
            for (Element element : annotatedImportables) {
                if (element.getKind().equals(ElementKind.CLASS)) {
                    importableClassMap.put(element, new ImportableClass(processingEnv, (TypeElement) element));
                }
            }


            //find fields for those classes
            addImportableFields(environment, importableClassMap);

            //find fields for those classes
            addInjectableFields(environment, importableClassMap);


            //generate sources
            for (ImportableClass importableClass : importableClassMap.values()) {
                generateMapperClass(importableClass);
            }


            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Finished processing annotations");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error prcessing annotations:" + e.getMessage() + "/n" + sw.toString());

        }
        return false;
    }

    private void generateMapperClass(ImportableClass importableClass) {
        try {
            importableClass.generateSources();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error generating the source code for:" + importableClass.getOriginalName());
        }
    }

    private void addInjectableFields(RoundEnvironment environment, Map<Element, ImportableClass> importableClassMap) {
        Set<? extends Element> generatedFields = environment.getElementsAnnotatedWith(Inject.class);
        for (Element element : generatedFields) {
            if (element.getKind().equals(ElementKind.FIELD)) {
                ImportableClass importableClass = importableClassMap.get(element.getEnclosingElement());
                if (importableClass == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Inject can only be used on classes annotated with @Importable");
                } else {
                    importableClass.addInjected((VariableElement) element);
                }
            }
        }
    }

    private void addImportableFields(RoundEnvironment environment, Map<Element, ImportableClass> importableClassMap) {
        Set<? extends Element> importableFields = environment.getElementsAnnotatedWith(Import.class);
        for (Element element : importableFields) {
            if (element.getKind().equals(ElementKind.FIELD)) {
                boolean namedAnnotationPresent = element.getAnnotation(Named.class) != null;
                boolean ordinalAnnotationPresent = element.getAnnotation(Ordinal.class) != null;
                ImportableClass importableClass = importableClassMap.get(element.getEnclosingElement());

                if (!namedAnnotationPresent && !ordinalAnnotationPresent) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Import can only be used alongside @NamedImport or @OrdinalImport");
                }
                if (namedAnnotationPresent && ordinalAnnotationPresent) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@NamedImport and @OrdinalImport cannot be used together");
                }
                if (element.getEnclosingElement().getAnnotation(Importable.class).isNamed()) {
                    if (!namedAnnotationPresent) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "If @Importable is named then all @Import fields must also be annotated with @NamedImport");
                    }
                } else {
                    if (!ordinalAnnotationPresent) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "If @Importable is not named then all @Import fields must also be annotated with @OrdinalImport");
                    }
                }
                if (importableClass == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Import can only be used on classes annotated with @Importable");
                } else {
                    importableClass.addField((VariableElement) element);
                }

            }
        }
    }


}
