package ro.nom.vmt.spreadsheet_importer;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import com.squareup.javapoet.*;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.nom.vmt.spreadsheet_importer.annotation.*;
import ro.nom.vmt.spreadsheet_importer.importing.RowContext;
import ro.nom.vmt.spreadsheet_importer.interfaces.BaseImportMapper;
import ro.nom.vmt.spreadsheet_importer.interfaces.Validatable;
import ro.nom.vmt.spreadsheet_importer.problems.InstantiationProblem;
import ro.nom.vmt.spreadsheet_importer.problems.ValueFormatEnumProblem;
import ro.nom.vmt.spreadsheet_importer.problems.ValueFormatProblem;
import ro.nom.vmt.spreadsheet_importer.util.ColumnOptions;
import ro.nom.vmt.spreadsheet_importer.util.ColumnUtil;
import ro.nom.vmt.spreadsheet_importer.util.Pair;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.squareup.javapoet.ParameterizedTypeName.get;


public class ImportableClass {

    private static final String OPTIONS_SUFFIX = "Options";
    private static final String ENTRY_SUFFIX = "Entry";
    private static final String PROCESSORS_SUFFIX = "Processors";
    private static final String STRING_SUFFIX = "String";

    private static final Set<Class<?>> mainClasses = new HashSet<>(Arrays.asList(String.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, LocalDateTime.class, LocalDate.class, LocalTime.class));
    private static final Set<String> mainClassesString = mainClasses.stream().map(Class::getSimpleName).collect(Collectors.toSet());
    private final String classPackage;
    private final String classSimpleName;
    private final ClassName className;
    private final ProcessingEnvironment environment;
    private final Types typeUtils;
    private final Filer filer;
    private final Messager messager;
    private final TypeElement classElement;
    private final Set<VariableElement> variableElements = new HashSet<>();
    private final Set<VariableElement> injectedElements = new HashSet<>();
    private final Map<String, List<? extends TypeMirror>> processFunctionClassesByVariable = new HashMap<>();
    boolean hasHeader;
    boolean isNamed;
    int[] sheetIndexes;
    String[] sheetNames;

    public ImportableClass(ProcessingEnvironment processingEnvironment, TypeElement classElement) {
        this.environment = processingEnvironment;
        this.typeUtils = environment.getTypeUtils();
        this.filer = processingEnvironment.getFiler();
        this.messager = processingEnvironment.getMessager();
        this.classElement = classElement;

        classPackage = environment.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
        classSimpleName = classElement.getSimpleName().toString();

        className = ClassName.get(classPackage, classSimpleName);

        hasHeader = classElement.getAnnotation(Importable.class).hasHeader();
        isNamed = classElement.getAnnotation(Importable.class).isNamed();
        sheetNames = classElement.getAnnotation(Importable.class).sheetNames();
        sheetIndexes = classElement.getAnnotation(Importable.class).sheetIndexes();


        if (isNamed && !hasHeader) {
            messager.printMessage(Diagnostic.Kind.ERROR, "For an import based on names you need to use a header!");
        }

        if ((sheetIndexes.length > 1 || (sheetIndexes.length == 1 && sheetIndexes[0] != 0)) && sheetNames.length != 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "You cannot pass both sheetIndexes and sheetNames to a class!");
        }
        if (sheetIndexes.length == 0 && sheetNames.length == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "You must pass values to either sheetIndexes or sheetNames!");
        }
        if (sheetNames.length > 1) {
            this.sheetIndexes = new int[0];
        }

    }

    private static List<? extends TypeMirror> getPreProcess(Element element) {
        try {
            //not the cleanest solution but you can't directly get the class
            //hack described here: https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            element.getAnnotation(Import.class).preProcess();
            return new ArrayList<>();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
    }

    public void generateSources() throws IOException {
        JavaFileObject builderFile = filer.createSourceFile(classPackage + "." + classSimpleName + "ImportMapper", this.classElement);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {


            Set<MethodSpec> methods = new HashSet<>();

            methods.add(generateMapMethod());
            methods.addAll(generateGetterMethods());

            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(classSimpleName + "ImportMapper")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(get(ClassName.get(BaseImportMapper.class), TypeName.get(classElement.asType())))
                    .addField(Logger.class, "log", Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                    .addField(boolean.class, "hasHeader", Modifier.PRIVATE)
                    .addField(boolean.class, "isNamed", Modifier.PRIVATE)

                    .addField(get(Map.class, String.class, String.class), "columnNames")
                    .addField(get(Set.class, String.class), "sheetNames")
                    .addField(get(Map.class, String.class, Integer.class), "columnPositions")
                    .addField(get(Set.class, Integer.class), "sheetPositions")
                    .addField(get(ClassName.get(Map.class), ClassName.get(String.class), get(Pair.class, Integer.class, Integer.class)), "columnRequiredAppearances");

            typeSpecBuilder.addStaticBlock(CodeBlock.builder()
                    .addStatement("log = $T.getLogger($T.class)", LoggerFactory.class, classElement)
                    .build());

            typeSpecBuilder = addProcessorFields(typeSpecBuilder);

            typeSpecBuilder
                    .addInitializerBlock(getInitializerBlock())
                    .addInitializerBlock(getProcessorInitializerBlock())
                    .addMethods(methods);

            JavaFile mapperFile = JavaFile.builder(classPackage, typeSpecBuilder.build()).build();

            mapperFile.writeTo(out);
        }
    }

    private TypeSpec.Builder addProcessorFields(TypeSpec.Builder builder) {

        for (Map.Entry<String, List<? extends TypeMirror>> entry : processFunctionClassesByVariable.entrySet()) {
            builder.addField(get(ClassName.get(List.class), get(UnaryOperator.class, String.class)), entry.getKey().concat(PROCESSORS_SUFFIX));

        }

        return builder;

    }

    private CodeBlock getProcessorInitializerBlock() {

        CodeBlock.Builder builder = CodeBlock.builder();

        for (Map.Entry<String, List<? extends TypeMirror>> entry : processFunctionClassesByVariable.entrySet()) {
            builder.addStatement("$L = new $T<>()", entry.getKey().concat(PROCESSORS_SUFFIX), ArrayList.class);
            for (TypeMirror mirror : entry.getValue()) {
                builder.addStatement("$L.add(new $T().processFunction())", entry.getKey().concat(PROCESSORS_SUFFIX), mirror);
            }
        }
        return builder.build();
    }

    private CodeBlock getInitializerBlock() {
        CodeBlock mapInitializingCode;
        if (isNamed) {
            mapInitializingCode = getCodeBlockNamed();
        } else {
            mapInitializingCode = getCodeBlockOrdinal();
        }

        String joinedSheetNames = sheetNames.length == 0 ? "" : "\"" + String.join("\",\"", sheetNames) + "\"";
        return CodeBlock.builder()
                .addStatement("hasHeader = $L", hasHeader)
                .addStatement("isNamed = $L", isNamed)
                .addStatement("sheetPositions = new $T<>($T.asList(new $T[]{$L}))", HashSet.class, Arrays.class, Integer.class, Arrays.toString(sheetIndexes).replaceAll("[\\[\\]]", ""))
                .addStatement("sheetNames = new $T<>($T.asList(new $T[]{$L}))", HashSet.class, Arrays.class, String.class, joinedSheetNames)
                .add(mapInitializingCode)
                .build();
    }

    private CodeBlock getCodeBlockOrdinal() {
        CodeBlock mapInitializingCode;
        CodeBlock.Builder mapInitializingCodeBuilder = CodeBlock.builder();
        mapInitializingCodeBuilder.addStatement("columnPositions = new $L()", get(HashMap.class, String.class, Integer.class));
        for (VariableElement element : variableElements) {
            mapInitializingCodeBuilder.addStatement("columnPositions.put($S,$L)", element.getSimpleName().toString(), element.getAnnotation(Ordinal.class).value());
        }

        mapInitializingCodeBuilder.addStatement("columnRequiredAppearances=new $L()", get(HashMap.class));
        for (VariableElement element : variableElements) {
            mapInitializingCodeBuilder.addStatement("columnRequiredAppearances.put($S, new $T(1, 1))", element.getSimpleName().toString(), Pair.class);
        }

        mapInitializingCode = mapInitializingCodeBuilder.build();
        return mapInitializingCode;
    }

    private CodeBlock getCodeBlockNamed() {
        CodeBlock mapInitializingCode;
        CodeBlock.Builder mapInitializingCodeBuilder = CodeBlock.builder();
        mapInitializingCodeBuilder.addStatement("columnNames = new $L()", get(HashMap.class, String.class, String.class));

        for (VariableElement element : variableElements) {
            String name = element.getAnnotation(Named.class).value();
            if (name.isEmpty()) {
                name = element.getSimpleName().toString();
            }
            mapInitializingCodeBuilder.addStatement("columnNames.put( $S, $S)", element.getSimpleName().toString(), name);
        }


        mapInitializingCodeBuilder.addStatement("columnRequiredAppearances = new $L<>()", get(HashMap.class));
        for (VariableElement element : variableElements) {
            int minimumMatches = element.getAnnotation(Named.class).minimumMatches();
            int maximumMatches = element.getAnnotation(Named.class).maximumMatches();
            if (maximumMatches < minimumMatches) {
                messager.printMessage(Diagnostic.Kind.ERROR, "MaximumMatches cannot be greater than minimumMatches! (column:" + element.getSimpleName().toString() + ")");
            }
            if (maximumMatches == 0) {
                messager.printMessage(Diagnostic.Kind.ERROR, "MaximumMatches cannot be 0! (column:" + element.getSimpleName().toString() + ")");
            }

            mapInitializingCodeBuilder.addStatement("columnRequiredAppearances.put($S, new $T($L, $L))", element.getSimpleName().toString(), Pair.class, minimumMatches, maximumMatches);

        }


        mapInitializingCode = mapInitializingCodeBuilder.build();
        return mapInitializingCode;
    }

    private Set<MethodSpec> generateGetterMethods() {
        Set<MethodSpec> methodSpecs = new HashSet<>();

        methodSpecs.add(MethodSpec.methodBuilder("hasHeader")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(boolean.class)
                .addStatement("return hasHeader")
                .build());
        methodSpecs.add(MethodSpec.methodBuilder("isNamed")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(boolean.class)
                .addStatement("return isNamed")
                .build());
        methodSpecs.add(MethodSpec.methodBuilder("getColumnNames")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(get(Map.class, String.class, String.class))
                .addStatement("return columnNames")
                .build());
        methodSpecs.add(MethodSpec.methodBuilder("getColumnRequiredAppearances")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(get(ClassName.get(Map.class), ClassName.get(String.class), get(Pair.class, Integer.class, Integer.class)))
                .addStatement("return columnRequiredAppearances")
                .build());
        methodSpecs.add(MethodSpec.methodBuilder("getColumnPositions")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(get(Map.class, String.class, Integer.class))
                .addStatement("return columnPositions")
                .build());
        methodSpecs.add(MethodSpec.methodBuilder("getSheetPositions")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(get(Set.class, Integer.class))
                .addStatement("return sheetPositions")
                .build());
        methodSpecs.add(MethodSpec.methodBuilder("getSheetNames")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(get(Set.class, String.class))
                .addStatement("return sheetNames")
                .build());


        return methodSpecs;
    }

    public void addField(VariableElement variableElement) {
        if (variableValid(variableElement)) {

            List<? extends TypeMirror> typeMirrors = getPreProcess(variableElement);
            if (typeMirrors != null && !typeMirrors.isEmpty()) {
                processFunctionClassesByVariable.put(variableElement.getSimpleName().toString(), typeMirrors);
            }


            variableElements.add(variableElement);
        }

    }

    public void addInjected(VariableElement variableElement) {
        if (variableValid(variableElement)) {
            injectedElements.add(variableElement);
        }
    }

    protected boolean variableValid(VariableElement variableElement) {
        String fieldName = variableElement.getSimpleName().toString();
        String expectedSetterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        if (hasSetter(variableElement, expectedSetterName)
        ) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Field: " + fieldName + " needs a setter named: " + expectedSetterName + " taking a single parameter of the same type as itself!");
        } else {
            if (classElement.asType().getKind().isPrimitive()) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Field: " + fieldName + " cannot be a primitive!");
            }
            return true;
        }
        return false;
    }

    public String getOriginalName() {
        return classElement.getQualifiedName().toString();
    }

    private MethodSpec generateMapMethod() {


        return MethodSpec.methodBuilder("map")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(className)
                .addParameter(get(ClassName.get(RowContext.class), get(classElement.asType())), "ctx")
                .addParameter(Row.class, "row")
                .addParameter(get(ClassName.get(Map.class), ClassName.get(String.class), get(LinkedHashMap.class, String.class, Integer.class)), "columnPositions")
                .addStatement("$T result = new $T()", className, className)
                .addCode(generateMapSettersForInjectedData())
                .addCode(generateMapSetters())
                .addCode(generateValidationCheck())
                .addStatement("return result")
                .build();
    }

    private CodeBlock generateMapSettersForInjectedData() {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

        for (VariableElement element : injectedElements) {
            Inject.InjectType type = element.getAnnotation(Inject.class).value();

            switch (type) {
                case SHEET_NAME:
                    codeBlockBuilder.add(setSheetName(element));
                    break;
                case SHEET_INDEX:
                    codeBlockBuilder.add(setSheetIndex(element));
                    break;
                case ROW_NUMBER:
                    codeBlockBuilder.add(setRowNumber(element));
                    break;
                case IMPORT_INDEX:
                    codeBlockBuilder.add(setImportIndex(element));
                    break;
                case UNMATCHED_COLUMNS:
                    CodeBlock options = getOptions(element.getSimpleName().toString(), false, true, true, null, "");
                    codeBlockBuilder.add(setUnmatchedColumns(element, options));

                    break;
                default:
                    continue;
            }


        }


        return codeBlockBuilder.build();
    }

    private CodeBlock setSheetName(VariableElement element) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        String fieldName = element.getSimpleName().toString();
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        codeBlockBuilder.addStatement("result.$L(row.getSheet().getSheetName())", setterName);

        return codeBlockBuilder.build();
    }

    private CodeBlock setSheetIndex(VariableElement element) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        String fieldName = element.getSimpleName().toString();
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        codeBlockBuilder.addStatement("result.$L(row.getSheet().getWorkbook().getSheetIndex(row.getSheet()))", setterName);

        return codeBlockBuilder.build();
    }

    private CodeBlock setRowNumber(VariableElement element) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        String fieldName = element.getSimpleName().toString();
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        codeBlockBuilder.addStatement("result.$L(row.getRowNum())", setterName);

        return codeBlockBuilder.build();
    }

    private CodeBlock setImportIndex(VariableElement element) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        String fieldName = element.getSimpleName().toString();
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        codeBlockBuilder.addStatement("result.$L(ctx.getImportIndex())", setterName);

        return codeBlockBuilder.build();
    }

    private CodeBlock setUnmatchedColumns(VariableElement element, CodeBlock options) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

        String variableName = element.getSimpleName().toString();
        String setterName = "set" + variableName.substring(0, 1).toUpperCase() + variableName.substring(1);

        TypeMirror elementType = element.asType();

        Symbol.TypeSymbol keyType = ((Type.ClassType) elementType).getTypeArguments().get(0).tsym;
        Symbol.TypeSymbol valueType = ((Type.ClassType) elementType).getTypeArguments().get(1).tsym;

        if (!keyType.getSimpleName().toString().equals(STRING_SUFFIX) || !valueType.getSimpleName().toString().equals(STRING_SUFFIX)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "This field must be a map having both the key and the value Strings! (column:" + variableName + ")");
        }
        if (mainClassesString.contains(valueType.getSimpleName().toString())) {
            String innerGetterName = "get".concat(valueType.getSimpleName().toString());
            codeBlockBuilder
                    .addStatement("$T<$T, $T> $L = new $T<>()", Map.class, String.class, String.class, variableName + "Map", HashMap.class)
                    .beginControlFlow("for($T<$T, $T> $L:columnPositions.getOrDefault(null, new $T<>()).entrySet())", Map.Entry.class, String.class, Integer.class, variableName + ENTRY_SUFFIX, LinkedHashMap.class)
                    .addStatement(options)
                    .addStatement("$L.put($L.getKey(), $T.$L(ctx, row, $L.getValue(), $L))", variableName + "Map", variableName + ENTRY_SUFFIX, ColumnUtil.class, innerGetterName, variableName + ENTRY_SUFFIX, variableName + OPTIONS_SUFFIX)
                    .endControlFlow()
                    .addStatement("result.$L($L)", setterName, variableName + "Map");

        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "At this time, Lists can only be of basic types (" + String.join(", ", mainClassesString) + ")!");
        }


        return codeBlockBuilder.build();
    }

    private CodeBlock generateMapSetters() {

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

        ELEMENTS:
        for (VariableElement element : variableElements) {
            String variableName = element.getSimpleName().toString();
            String fieldName = variableName;
            String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            TypeMirror elementType = element.asType();
            boolean isRequired = element.getAnnotation(Import.class).required();
            boolean trim = element.getAnnotation(Import.class).trim();
            boolean formulaAllowed = element.getAnnotation(Import.class).formulaAllowed();
            String matches = element.getAnnotation(Import.class).matches();


            Symbol.TypeSymbol container = ((Type) elementType).tsym;
            String getterName = "get".concat(container.getSimpleName().toString());

            String preProcessors = processFunctionClassesByVariable.get(variableName) != null ? variableName + PROCESSORS_SUFFIX : null;

            CodeBlock options = getOptions(variableName, isRequired, trim, formulaAllowed, preProcessors, matches);


            for (Class<?> clazz : mainClasses) { //we create the code for the simple types
                if (typeUtils.isSameType(elementType, environment.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType())) {
                    validateNotMultipleAppearence(element);
                    codeBlockBuilder.add(setSimpleField(variableName, setterName, getterName, options));
                    continue ELEMENTS;
                }
            }

            if (container.getQualifiedName().toString().equals(List.class.getCanonicalName())) {
                codeBlockBuilder.add(setListField(elementType, variableName, setterName, options));
                continue;
            }

            if (container.getQualifiedName().toString().equals(Map.class.getCanonicalName())) {
                codeBlockBuilder.add(setMapField(elementType, variableName, setterName, options));
                continue;
            }


            if (((TypeElement) container).getKind().equals(ElementKind.ENUM)) {
                validateNotMultipleAppearence(element);
                codeBlockBuilder.add(setEnumField(variableName, setterName, element, options));
                continue;
            }

            validateNotMultipleAppearence(element);
            codeBlockBuilder.add(setCustomClassField(element, variableName, setterName, (TypeElement) container, options));

        }


        return codeBlockBuilder.build();
    }

    private void validateNotMultipleAppearence(Element element) {
        if (element.getAnnotation(Named.class) != null && element.getAnnotation(Named.class).maximumMatches() > 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Allowing multiple appearances for a column but expecting to import to a non collection would cause unpredictable behaviour and as such is not allowed! (column: " + element.getSimpleName() + ")");
        }
    }

    private CodeBlock generateValidationCheck() {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

        if (typeUtils.isAssignable(classElement.asType(), environment.getElementUtils().getTypeElement(Validatable.class.getCanonicalName()).asType())) {
            codeBlockBuilder.addStatement("ctx.addValidationProblems(result.validate(row.getSheet().getSheetName(), row.getRowNum()))");
        }


        return codeBlockBuilder.build();
    }

    private CodeBlock getOptions(String variableName, boolean isRequired, boolean trim, boolean formulaAllowed, String processFunctionNames, String matches) {
        CodeBlock.Builder options = CodeBlock.builder();
        options.add("$T $L = $T.builder()", ColumnOptions.class, variableName + OPTIONS_SUFFIX, ColumnOptions.class);
        options.add(".trim($L)", trim);
        options.add(".formulaAllowed($L)", formulaAllowed);
        options.add(".isRequired($L)", isRequired);
        options.add(".matches($S)", matches);
        options.add(".columnName($L.getKey())", variableName + ENTRY_SUFFIX);
        if (processFunctionNames != null) {
            options.add(".preProcess($L)", processFunctionNames);

        }

        options.add(".build()");
        return options.build();
    }

    private CodeBlock setCustomClassField(VariableElement element, String variableName, String setterName, TypeElement container, CodeBlock options) {
        boolean constructorIsPresent = getCustomClassConstructor(container).isPresent();

        if (constructorIsPresent) {
            String tempStringVariableName = variableName.concat(STRING_SUFFIX);
            return CodeBlock.builder()
                    .beginControlFlow("if(columnPositions.containsKey($S))", variableName)
                    .addStatement("$T $L=null", String.class, tempStringVariableName)
                    .addStatement("$T<$T, $T> $L=columnPositions.get($S).entrySet().iterator().next()", Map.Entry.class, String.class, Integer.class, variableName + ENTRY_SUFFIX, variableName)
                    .addStatement(options)
                    .addStatement("$L = $T.getString(ctx, row, $L.getValue(), $L)", tempStringVariableName, ColumnUtil.class, variableName + ENTRY_SUFFIX, variableName + OPTIONS_SUFFIX)
                    .beginControlFlow("if ($L != null)", tempStringVariableName)
                    .beginControlFlow("try")
                    .addStatement("result.$L(new $T($L))", setterName, element.asType(), tempStringVariableName)
                    .nextControlFlow("catch ($T ex)", InstantiationProblem.class)
                    .addStatement("ctx.addValidationProblems(ex.getValidationProblems());")
                    .nextControlFlow("catch ($T ex)", Exception.class)
                    .addStatement("log.error(ex.getMessage(), ex)")
                    .addStatement("ctx.addValidationProblem(new $T(row.getSheet().getSheetName(), row.getRowNum(), $L.getValue(), $L.getKey(), $L));", ValueFormatProblem.class, variableName + ENTRY_SUFFIX, variableName + ENTRY_SUFFIX, tempStringVariableName)
                    .endControlFlow()
                    .endControlFlow()
                    .endControlFlow()
                    .build();
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "Objects that are not supported by default (" + String.join(", ", mainClassesString) + " and Enums) must have a public constructor accepting a String parameter corresponding to the value found in the column! (column:" + variableName + ")");
            return CodeBlock.builder().build();
        }
    }

    private Optional<? extends Element> getCustomClassConstructor(TypeElement container) {
        return container.getEnclosedElements()//we want to make sure it has a public constructor taking in a String
                .stream()
                .filter(el -> el.getKind().equals(ElementKind.CONSTRUCTOR)) //we are only interested in the constructors
                .filter(el -> el.getModifiers().contains(Modifier.PUBLIC)) //it must be public
                .filter(el -> ((ExecutableElement) el).getParameters().size() == 1) //it must have a single parameter
                .filter(el -> typeUtils.isSameType(((ExecutableElement) el).getParameters().get(0).asType(), environment.getElementUtils().getTypeElement(String.class.getCanonicalName()).asType())) //the parameter must be a String
                .findFirst();
    }

    private CodeBlock setMapField(TypeMirror elementType, String variableName, String setterName, CodeBlock options) {

        Symbol.TypeSymbol keyType = ((Type.ClassType) elementType).getTypeArguments().get(0).tsym;
        Symbol.TypeSymbol valueType = ((Type.ClassType) elementType).getTypeArguments().get(1).tsym;

        if (!keyType.getSimpleName().toString().equals(STRING_SUFFIX) && !keyType.getSimpleName().toString().equals("Integer")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Map fields must have the value type as String in order to insert the column name or as Integer to insert the column index! (column:" + variableName + ")");
        }
        if (mainClassesString.contains(valueType.getSimpleName().toString())) {
            String innerGetterName = "get".concat(valueType.getSimpleName().toString());
            String entryGetter = keyType.getSimpleName().toString().equals(STRING_SUFFIX) ? "getKey" : "getValue";

            return CodeBlock.builder()
                    .addStatement("$T<$T, $T> $L = new $T<>()", Map.class, keyType.asType(), valueType.asType(), variableName + "Map", HashMap.class)
                    .beginControlFlow("for($T<$T, $T> $L : columnPositions.getOrDefault($S, new $T<>()).entrySet())", Map.Entry.class, String.class, Integer.class, variableName + ENTRY_SUFFIX, variableName, LinkedHashMap.class)
                    .addStatement(options)
                    .addStatement("$L.put($L.$L(), $T.$L(ctx, row, $L.getValue(), $L))", variableName + "Map", variableName + ENTRY_SUFFIX, entryGetter, ColumnUtil.class, innerGetterName, variableName + ENTRY_SUFFIX, variableName + OPTIONS_SUFFIX)
                    .endControlFlow()
                    .addStatement("result.$L($L)", setterName, variableName + "Map")
                    .build();


        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "At this time, Lists can only be of basic types (" + String.join(",", mainClassesString) + ")!");
            return CodeBlock.builder().build();
        }

    }

    private CodeBlock setListField(TypeMirror elementType, String variableName, String setterName, CodeBlock options) {
        Symbol.TypeSymbol innerType = ((Type.ClassType) elementType).getTypeArguments().get(0).tsym;
        if (mainClassesString.contains(innerType.getSimpleName().toString())) {
            String innerGetterName = "get".concat(innerType.getSimpleName().toString());
            return CodeBlock.builder()
                    .addStatement("$T<$T> $L = new $T<>()", List.class, innerType.asType(), variableName + "List", ArrayList.class)
                    .beginControlFlow("for($T<$T, $T> $L:columnPositions.getOrDefault($S, new $T<>()).entrySet())", Map.Entry.class, String.class, Integer.class, variableName + ENTRY_SUFFIX, variableName, LinkedHashMap.class)
                    .addStatement(options)
                    .addStatement("$L.add($T.$L(ctx, row, $L.getValue(), $L))", variableName + "List", ColumnUtil.class, innerGetterName, variableName + ENTRY_SUFFIX, variableName + OPTIONS_SUFFIX)
                    .endControlFlow()
                    .addStatement("result.$L($L)", setterName, variableName + "List")
                    .build();

        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "At this time, Lists can only be of basic types (" + String.join(",", mainClassesString) + ")!");
            return CodeBlock.builder().build();
        }

    }

    private CodeBlock setSimpleField(String variableName, String setterName, String getterName, CodeBlock options) {
        return CodeBlock.builder()
                .beginControlFlow("if(columnPositions.containsKey($S))", variableName)
                .addStatement("$T<$T, $T> $L=columnPositions.get($S).entrySet().iterator().next()", Map.Entry.class, String.class, Integer.class, variableName + ENTRY_SUFFIX, variableName)
                .addStatement(options)
                .addStatement("result.$L($T.$L(ctx, row, $L.getValue(), $L))", setterName, ColumnUtil.class, getterName, variableName + ENTRY_SUFFIX, variableName + OPTIONS_SUFFIX)
                .endControlFlow()
                .build();
    }

    private CodeBlock setEnumField(String variableName, String setterName, VariableElement element, CodeBlock options) {

        return CodeBlock.builder()
                .beginControlFlow("if(columnPositions.containsKey($S))", variableName)
                .addStatement("$T<$T, $T> $L=columnPositions.get($S).entrySet().iterator().next()", Map.Entry.class, String.class, Integer.class, variableName + ENTRY_SUFFIX, variableName)
                .addStatement(options)
                .addStatement("$T stringEnum=$T.getString(ctx, row, $L.getValue(), $L)", String.class, ColumnUtil.class, variableName + ENTRY_SUFFIX, variableName + OPTIONS_SUFFIX)
                .beginControlFlow("if(stringEnum!=null)")
                .beginControlFlow("try")
                .addStatement("result.$L($T.valueOf(stringEnum))", setterName, element.asType())
                .nextControlFlow("catch ($T e)", IllegalArgumentException.class)
                .addStatement("ctx.addValidationProblem(new $T(row.getSheet().getSheetName(), row.getRowNum(), $L.getValue(), $L.getKey(), stringEnum, $T.class));", ValueFormatEnumProblem.class, variableName + ENTRY_SUFFIX, variableName + ENTRY_SUFFIX, element.asType())
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .build();


    }

    private boolean hasSetter(VariableElement variableElement, String expectedSetterName) {
        return classElement.getEnclosedElements()
                .stream()
                .filter(element -> element.getKind().equals(ElementKind.METHOD))
                .filter(element -> element.getSimpleName().toString().equals(expectedSetterName))
                .noneMatch(element -> ((ExecutableElement) element).getParameters().size() == 1
                        && ((ExecutableElement) element).getParameters().get(0).asType().toString().equals(variableElement.asType().toString()));
    }

}
