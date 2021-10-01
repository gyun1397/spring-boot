package com.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.scheduling.annotation.Async;
import com.common.anotation.CodeGenLimitter;
import com.common.anotation.CodeGenLimitter.Limitter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeGenerator {
    /*
     * package 경로 기본값
     * 접두사 / 접미사
     */
    private static final String prefixPath    = "classpath*:/";
    private static final String suffixPath    = "/**/*.class";
    private static String       defaultIdType = "Long";
    // 포맷 외 기본 equal 검색 설정
    private static String[]     equalsCase    = {};
    // 포맷 외 기본 like 검색 설정
    private static String[]     likeCase      = {};
    // 포맷 외 기본 startWith 검색 설정
    private static String[]     startWithCase = {};
    // 포맷 외 기본 in 검색 설정
    private static String[]     inCase        = {};

    public static void codeGenerator() {
        try {
            StackTraceElement[] a = new Throwable().getStackTrace();
            String className = "";
            int i = 0;
            do {
                className = a[i++].getClassName();
            } while (CodeGenerator.class.getName().equals(className));
            System.out.println(className);
            Class<?> targetClass = Class.forName(className);
            codeGenerator(targetClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public static void codeGenerator(Class<?> targetClass) {
        try {
            Limitter limitter = Limitter.NONE;
            if (targetClass.getDeclaredAnnotation(CodeGenLimitter.class) != null) {
                limitter = targetClass.getDeclaredAnnotation(CodeGenLimitter.class).value();
            }
            String packagePath = targetClass.getPackage().toString();
            String classPath = targetClass.getName();
            String className = targetClass.getSimpleName();
            String entityName = firstLowerCase(className);
            Set<Field> fields = getFields(targetClass);
            String idType = "";
            for (Field field : fields) {
                if ("id".equals(field.getName())) {
                    idType = field.getType().getSimpleName();
                    break;
                }
            }
            if (idType == null || idType.isEmpty()) {
                System.out.println(targetClass.getSimpleName() + " is Not Entitiy Class");
                return;
            } else if (idType.equals("Object")) {
                idType = defaultIdType;
            }
            switch (limitter) {
                case NONE:
                case CONTROLLER:
                    createDirecoty(packagePath);
                    Class<?> supperClass = superClassCheck(targetClass);
                    if ("BasicEntity".equals(supperClass.getSimpleName())) {
                        createSystemController(packagePath, classPath, className, idType);
                    } else {
                        createRestController(packagePath, classPath, className, idType);
                    }
                case SERVICE:
                    createDirecoty(packagePath);
                    createService(packagePath, classPath, className, idType);
                    createServiceImpl(packagePath, classPath, className, entityName, idType);
                case REPOSITORY:
                    createRepository(packagePath, classPath, className, idType);
                    createRepositoryImpl(packagePath, classPath, className, entityName, idType, fields);
                case ALL:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void codeGenerator(Class<?>... targetClasses) {
        if (targetClasses != null && targetClasses.length > 0) {
            for (Class<?> targetClass : targetClasses) {
                codeGenerator(targetClass);
            }
        }
    }

    public static void codeGenerator(Collection<Class<?>> targetClasses) {
        if (targetClasses != null && !targetClasses.isEmpty()) {
            for (Class<?> targetClass : targetClasses) {
                codeGenerator(targetClass);
            }
        }
    }

    public static void codeGenerator(String... packagePaths) {
        if (packagePaths != null && packagePaths.length > 0) {
            codeGenerator(classMetaScan(Entity.class, packagePaths));
        }
    }

    public static void codeGenerator(Class<?> annotationType, String... packagePaths) {
        if (packagePaths != null && packagePaths.length > 0) {
            codeGenerator(classMetaScan(annotationType, packagePaths));
        }
    }

    private static Set<Class<?>> classMetaScan(Class<?> annotationType, String... packagePaths) {
        // Spring 컨테이너 안에서는 ApplicationContext.getResources( .. ) 형태로도 사용 가능
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] rs;
        Set<Class<?>> classes = new HashSet<>();
        if (packagePaths != null && packagePaths.length > 0) {
            for (String packagePath : packagePaths) {
                try {
                    rs = resolver.getResources(prefixPath + packagePath.replace(".", "/") + suffixPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (Resource r : rs) {
                    MetadataReader mr;
                    try {
                        mr = new SimpleMetadataReaderFactory().getMetadataReader(r);
                        boolean hasAnnotation = mr.getAnnotationMetadata().hasAnnotation(annotationType.getName());
                        if (hasAnnotation) {
                            classes.add(Class.forName(mr.getClassMetadata().getClassName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
        return classes;
    }

    private static void createDirecoty(String packagePath) throws Exception {
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/api");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private static void createRepository(String packagePath, String classPath, String className, String idType) throws Exception {
        String body = packagePath + ";\n" +
                "\n" +
                "import com.common.jpa.api.BaseJpaRepository;\n" +
                "\n" +
                "public interface " + className + "Repository extends BaseJpaRepository<" + className + ", " + idType + "> {\n" +
                "}";
        FileOutputStream fos = null;
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/" + className + "Repository.java");
        try {
            if (file.exists()) {
                // System.out.println(className + "Repository Exists!!");
                return;
            }
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.flush();
            System.out.println(className + "Repository Create!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void createRepositoryImpl(String packagePath, String classPath, String className, String entityName, String idType, Set<Field> fields) throws Exception {
        String body = packagePath + ";\n" +
                "\n" +
                "import java.util.Map;\n" +
                "import java.util.Set;\n" +
                "import com.common.exception.BadRequestException;\n" +
                "import com.common.jpa.api.BaseCustomRepository;\n" +
                "import com.common.jpa.api.BaseCustomRepositoryImpl;\n" +
                "import com.common.util.DataUtil;\n" +
                "import com.common.util.DateTimeUtil;\n" +
                "import com.common.util.StringUtil;\n" +
                "import com.querydsl.core.BooleanBuilder;\n" +
                "import com.querydsl.core.types.Expression;\n" +
                "import com.querydsl.core.types.Predicate;\n" +
                "\n" +
                "public class " + className + "RepositoryImpl extends BaseCustomRepositoryImpl<" + className + ", Q" + className + "> implements BaseCustomRepository  {\n" +
                "    \n" +
                "    public " + className + "RepositoryImpl() {\n" +
                "        super(Q" + className + "." + getQEntityName(entityName, fields) + ");\n" +
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    public Expression<?> getExpression(String field) {\n" +
                "        Expression<?> expression = null;\n" +
                "        switch (field) {\n" +
                createExpression(fields) +
                "            default:\n" +
                "                throw new BadRequestException(field + \" 해당 필드명을 찾을 수 없습니다.\");\n" +
                "        }\n" +
                "        return expression;\n" +
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    public BooleanBuilder makeWhereCondition(Map<String, Object> map) {\n" +
                "        BooleanBuilder builder = new BooleanBuilder();\n" +
                "        Set<String> keys = map.keySet();\n" +
                "        for (String key : keys) {\n" +
                "            if (DataUtil.isValidParam(map.get(key))) {\n" +
                "                switch (key) {\n " +
                createWhereCondition(fields) +
                "                }\r\n" +
                "                if (StringUtil.isOrSearch(key)) {\n" +
                "                    String[] feilds = StringUtil.split(key, \"|\");\n" +
                "                    builder.and(makeOrCondition(map.get(key), feilds));\n" +
                "                }\r\n" +
                "            }\r\n" +
                "        }\r\n" +
                "        return builder;\n" +
                "    }\n" +
                "    \n" +
                "    private Predicate makeOrCondition(Object value, String... feilds) {\n" +
                "        BooleanBuilder builder = new BooleanBuilder();\n" +
                "        for (String feild : feilds) {\n" +
                "            switch (feild) {\n " +
                createOrCondition(fields) +
                "            }\r\n" +
                "        }\r\n" +
                "        return builder;\n" +
                "    }\n" +
                "}";
        FileOutputStream fos = null;
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/" + className + "RepositoryImpl.java");
        try {
            // if (file.exists()) {
            // System.out.println(className + "RepositoryImpl Exists!!");
            // return;
            // }
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.flush();
            System.out.println(className + "RepositoryImpl Create!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void createService(String packagePath, String classPath, String className, String idType) throws Exception {
        String body = packagePath + ".api;\n" +
                "\n" +
                "import com.common.jpa.api.BaseService;\n" +
                "import " + classPath + ";\n" +
                "\n" +
                "public interface " + className + "Service extends BaseService<" + className + ", " + idType + "> {\n" +
                "}";
        FileOutputStream fos = null;
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/api/" + className + "Service.java");
        try {
            if (file.exists()) {
                // System.out.println(className + "Service Exists!!");
                return;
            }
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.flush();
            System.out.println(className + "Service Create!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void createServiceImpl(String packagePath, String classPath, String className, String entityName, String idType) throws Exception {
        String body = packagePath + ".api;\n" +
                "\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import com.common.jpa.api.BaseServiceImpl;\n" +
                "import " + classPath + ";\n" +
                "import " + classPath + "Repository;\n" +
                "\n" +
                "@Service(\"" + firstLowerCase(className) + "Service\")\n" +
                "public class " + className + "ServiceImpl extends BaseServiceImpl<" + className + ", " + idType + "> implements " + className + "Service {\n" +
                "    \n" +
                "    public " + className + "ServiceImpl() {\n" +
                "        super(" + className + ".class);\n" +
                "    }\n" +
                "    \n" +
                "    @Autowired\n" +
                "    private " + className + "Repository " + firstLowerCase(className) + "Repository;\n" +
                "    \n" +
                "}";
        FileOutputStream fos = null;
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/api/" + className + "ServiceImpl.java");
        try {
            if (file.exists()) {
                // System.out.println(className + "ServiceImpl Exists!!");
                return;
            }
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.flush();
            System.out.println(className + "ServiceImpl Create!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void createRestController(String packagePath, String classPath, String className, String idType) throws Exception {
        String body = packagePath + ".api;\n" +
                "\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.data.rest.webmvc.RepositoryRestController;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import com.common.jpa.api.BaseRestController;\n" +
                "import " + classPath + ";\n" +
                "\n" +
                "@RepositoryRestController\n" +
                "@RequestMapping(\"/" + getPlural(firstLowerCase(className)) + "\")\n" +
                "public class " + className + "RestController extends BaseRestController<" + className + ", " + idType + "> {\n" +
                "    \n" +
                "    @Autowired\n" +
                "    private " + className + "Service " + firstLowerCase(className) + "Service;\n" +
                "    \n" +
                "}";
        FileOutputStream fos = null;
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/api/" + className + "RestController.java");
        try {
            if (file.exists()) {
                // System.out.println(className + "RestController Exists!!");
                return;
            }
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.flush();
            System.out.println(className + "RestController Create!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void createSystemController(String packagePath, String classPath, String className, String idType) throws Exception {
        String body = packagePath + ".api;\n" +
                "\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.data.rest.webmvc.RepositoryRestController;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import com.common.jpa.api.SystemRestController;\n" +
                "import " + classPath + ";\n" +
                "\n" +
                "@RepositoryRestController\n" +
                "@RequestMapping(\"/" + getPlural(firstLowerCase(className)) + "\")\n" +
                "public class " + className + "RestController extends SystemRestController<" + className + ", " + idType + "> {\n" +
                "    \n" +
                "    @Autowired\n" +
                "    private " + className + "Service " + firstLowerCase(className) + "Service;\n" +
                "    \n" +
                "}";
        FileOutputStream fos = null;
        String filePath = packagePath.replace("package ", "").replace(".", "/");
        File file = new File("./src/main/java/" + filePath + "/api/" + className + "RestController.java");
        try {
            if (file.exists()) {
                // System.out.println(className + "RestController Exists!!");
                return;
            }
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.flush();
            System.out.println(className + "RestController Create!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static String firstLowerCase(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }

    private static Class<?> superClassCheck(Class<?> targetClass) {
        Class<?> result = targetClass;
        String simpleName = "";
        do {
            result = result.getSuperclass();
            simpleName = result.getSimpleName();
            // } while (result != Object.class && result != LogEntity.class && result != BasicEntity.class);
        } while (!"Object".equals(simpleName) && !"BasicEntity".equals(simpleName) && !"LogEntity".equals(simpleName));
        return result;
    }

    private static String getPlural(String singular) {
        String consonants = "bcdfghjklmnpqrstvwxz";
        switch (singular) {
            case "Person":
                return "People";
            case "Trash":
                return "Trash";
            case "Life":
                return "Lives";
            case "Man":
                return "Men";
            case "Woman":
                return "Women";
            case "Child":
                return "Children";
            case "Foot":
                return "Feet";
            case "Tooth":
                return "Teeth";
            case "Dozen":
                return "Dozen";
            case "Hundred":
                return "Hundred";
            case "Thousand":
                return "Thousand";
            case "Million":
                return "Million";
            case "Datum":
                return "Data";
            case "Criterion":
                return "Criteria";
            case "Analysis":
                return "Analyses";
            case "Fungus":
                return "Fungi";
            case "Index":
                return "Indices";
            case "Matrix":
                return "Matrices";
            case "Settings":
                return "Settings";
            case "UserSettings":
                return "UserSettings";
            default:
                // Handle ending with "o" (if preceeded by a consonant, end with -es, otherwise -s: Potatoes and Radios)
                if (singular.endsWith("o") && consonants.contains(String.valueOf(singular.charAt(singular.length() - 2)))) {
                    return singular + "es";
                }
                // Handle ending with "y" (if preceeded by a consonant, end with -ies, otherwise -s: Companies and Trays)
                if (singular.endsWith("y") && consonants.contains(String.valueOf(singular.charAt(singular.length() - 2)))) {
                    return singular.substring(0, singular.length() - 1) + "ies";
                }
                // Ends with a whistling sound: boxes, buzzes, churches, passes
                if (singular.endsWith("s") || singular.endsWith("sh") || singular.endsWith("ch") || singular.endsWith("x") || singular.endsWith("z")) {
                    return singular + "es";
                }
                return singular + "s";
        }
    }

    private static Set<Field> getFields(Class<?> targetClass) {
        Class<?> res = targetClass;
        Set<Field> fields = new HashSet<>();
        Set<String> fieldNames = new HashSet<>();
        do {
            for (Field field : res.getDeclaredFields()) {
                if (fieldNames.contains(field.getName())) {
                    continue;
                }
                if (field.getName().equals("id")) {
                }
                fieldNames.add(field.getName());
                fields.add(field);
            }
            // fields.addAll(Arrays.asList(res.getDeclaredFields()));
            res = res.getSuperclass();
        } while (res != null);
        return fields;
    }

    public static String createWhereCondition() {
        String result = null;
        try {
            StackTraceElement[] a = new Throwable().getStackTrace();
            String className = "";
            int i = 0;
            do {
                className = a[i++].getClassName();
            } while (CodeGenerator.class.getName().equals(className));
            System.out.println(className);
            Class<?> targetClass = Class.forName(className);
            result = createWhereCondition(targetClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    private static String getQEntityName(String entityName, Set<Field> fields) {
        String name = entityName;
        for (Field field : fields) {
            if (field.getName().equals(entityName)) {
                name = name + "1";
                break;
            }
        }
        return name;
    }

    private static String createWhereCondition(Class<?> targetClass) {
        return createWhereCondition(getFields(targetClass));
    }

    private static String createWhereCondition(Set<Field> fields) {
        String body = "";
        for (Field field : fields) {
            if (field.getAnnotation(Transient.class) != null) {
                continue;
            }
            String condition = "";
            if ("id".equals(field.getName())) {
                condition = idCondition(field.getType().getSimpleName().equals("Object") ? defaultIdType : field.getType().getSimpleName());
                body = body.concat(condition);
                continue;
            }
            switch (field.getType().getSimpleName()) {
                case "String":
                    condition = stringCondition(field.getName());
                    break;
                case "Integer":
                    condition = integerCondition(field.getName());
                    break;
                case "BigInteger":
                    condition = bigIntegerCondition(field.getName());
                    break;
                case "Double":
                    condition = doubleCondition(field.getName());
                    break;
                case "BigDecimal":
                    condition = bigDecimalCondition(field.getName());
                    break;
                case "Long":
                    condition = longCondition(field.getName());
                    break;
                case "Boolean":
                    condition = booleanCondition(field.getName());
                    break;
                case "LocalDateTime":
                    condition = localDateTimeCondition(field.getName());
                    break;
                case "LocalDate":
                    condition = localDateCondition(field.getName());
                    break;
                case "Date":
                    condition = dateCondition(field.getName());
                    break;
                case "Timestamp":
                    condition = timestampCondition(field.getName());
                    break;
                default:
                    break;
            }
            body = body.concat(condition);
        }
        return body;
    }

    private static String idCondition(String fieldType) {
        String body = null;
        body = "\t\t\t\t\tcase \"id\":\n"
                + "\t\t\t\t\tcase \"id_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity.id.eq(DataUtil." + fieldType.toLowerCase() + "Convert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"id_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity.id.ne(DataUtil." + fieldType.toLowerCase() + "Convert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"ids\":\n"
                + "\t\t\t\t\tcase \"ids_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity.id.in(DataUtil.listConvert(map.get(key), " + fieldType + ".class)));\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String stringCondition(String fieldName) {
        String body = null;
        if (StringUtil.in(fieldName, equalsCase)) {
            body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_mty\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".eq(\"\") : qEntity." + fieldName + ".ne(\"\"));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        } else if (StringUtil.in(fieldName, likeCase)) {
            body = "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_le\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_st\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like((String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_end\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_mty\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".eq(\"\") : qEntity." + fieldName + ".ne(\"\"));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        } else if (StringUtil.in(fieldName, startWithCase)) {
            body = "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_le\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_st\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like((String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_end\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_mty\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".eq(\"\") : qEntity." + fieldName + ".ne(\"\"));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        } else if (StringUtil.in(fieldName, inCase) || fieldName.equals("state") || fieldName.endsWith("Check")) {
            body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_mty\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".eq(\"\") : qEntity." + fieldName + ".ne(\"\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + getPlural(fieldName) + "\":\n"
                    + "\t\t\t\t\tcase \"" + getPlural(fieldName) + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".in(DataUtil.listConvert(map.get(key))));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        } else if (fieldName.endsWith("Uid") || fieldName.toLowerCase().endsWith("uuid") || fieldName.endsWith("Id")) {
            body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_mty\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".eq(\"\") : qEntity." + fieldName + ".ne(\"\"));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        } else if (fieldName.endsWith("DateTime") || fieldName.endsWith("Date") || fieldName.endsWith("Time")) {
            body = "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        } else {
            body = "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_le\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like((String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_st\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like((String) map.get(key) + \"%\"));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_end\":\n"
                    + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) map.get(key)));\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\t\tbreak;\n"
                    + "\t\t\t\t\tcase \"" + fieldName + "_mty\":\n"
                    + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".eq(\"\") : qEntity." + fieldName + ".ne(\"\"));\n"
                    + "\t\t\t\t\t\tbreak;\n";
        }
        return body;
    }

    private static String integerCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DataUtil.integerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne(DataUtil.integerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DataUtil.integerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DataUtil.integerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_gt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".gt(DataUtil.integerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_lt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".lt(DataUtil.integerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String bigIntegerCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DataUtil.bigintegerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne(DataUtil.bigintegerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DataUtil.bigintegerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DataUtil.bigintegerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_gt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".gt(DataUtil.bigintegerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_lt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".lt(DataUtil.bigintegerConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String doubleCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DataUtil.doubleConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne(DataUtil.doubleConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DataUtil.doubleConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DataUtil.doubleConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_gt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".gt(DataUtil.doubleConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_lt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".lt(DataUtil.doubleConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String bigDecimalCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DataUtil.bigDecimalConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne(DataUtil.bigDecimalConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DataUtil.bigDecimalConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DataUtil.bigDecimalConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_gt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".gt(DataUtil.bigDecimalConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_lt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".lt(DataUtil.bigDecimalConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String longCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DataUtil.longConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne(DataUtil.longConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DataUtil.longConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DataUtil.longConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_gt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".gt(DataUtil.longConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_lt\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".lt(DataUtil.longConvert(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String booleanCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DataUtil.isTrue(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne(DataUtil.isTrue(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                + "\t\t\t\t\t\tbuilder.and(DataUtil.isTrue(map.get(key)) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                + "\t\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String localDateCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq(DateTimeUtil.convertLocalDate(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateTimeUtil.minimizedLocalDate(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateTimeUtil.maximizedLocalDate(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String localDateTimeCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateTimeUtil.minimizedLocalDateTime(map.get(key))));\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateTimeUtil.maximizedLocalDateTime(map.get(key))));\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateTimeUtil.minimizedLocalDateTime(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateTimeUtil.maximizedLocalDateTime(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String dateCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateTimeUtil.minimized(map.get(key))));\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateTimeUtil.maximized(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateTimeUtil.minimized(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateTimeUtil.maximized(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String timestampCondition(String fieldName) {
        String body = "\t\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateUtil.minimizedToTimestamp(map.get(key))));\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateUtil.maximizedToTimestamp(map.get(key))));\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".goe(DateUtil.minimizedToTimestamp(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".loe(DateUtil.maximizedToTimestamp(map.get(key))));\n"
                + "\t\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String createOrCondition(Class<?> targetClass) {
        return createOrCondition(getFields(targetClass));
    }

    private static String createOrCondition(Set<Field> fields) {
        String body = "";
        for (Field field : fields) {
            if (field.getAnnotation(Transient.class) != null) {
                continue;
            }
            String condition = "";
            if ("id".equals(field.getName())) {
                condition = idOrCondition(field.getType().getSimpleName().equals("Object") ? defaultIdType : field.getType().getSimpleName());
                body = body.concat(condition);
                continue;
            }
            switch (field.getType().getSimpleName()) {
                case "String":
                    condition = stringOrCondition(field.getName());
                    break;
                case "Integer":
                    condition = integerOrCondition(field.getName());
                    break;
                case "Double":
                    condition = doubleOrCondition(field.getName());
                    break;
                case "Long":
                    condition = longOrCondition(field.getName());
                    break;
                case "Boolean":
                    condition = booleanOrCondition(field.getName());
                    break;
                case "LocalDateTime":
                    condition = localDateTimeOrCondition(field.getName());
                    break;
                case "LocalDate":
                    condition = localDateOrCondition(field.getName());
                    break;
                case "Date":
                    condition = dateOrCondition(field.getName());
                    break;
                case "Timestamp":
                    condition = timestampOrCondition(field.getName());
                    break;
                default:
                    break;
            }
            body = body.concat(condition);
        }
        return body;
    }

    private static String idOrCondition(String fieldType) {
        String body = null;
        body = "\t\t\t\tcase \"id\":\n"
                + "\t\t\t\tcase \"id_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity.id.eq(DataUtil." + fieldType.toLowerCase() + "Convert(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"ids\":\n"
                + "\t\t\t\tcase \"ids_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity.id.in(DataUtil.listConvert(value, " + fieldType + ".class)));\n"
                + "\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String stringOrCondition(String fieldName) {
        String body = null;
        if (StringUtil.in(fieldName, inCase) || fieldName.equals("state") || fieldName.endsWith("Check")) {
            body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) value));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                    + "\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".ne((String) value));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\tbuilder.and(DataUtil.isTrue(value) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + getPlural(fieldName) + "\":\n"
                    + "\t\t\t\tcase \"" + getPlural(fieldName) + "_eq\":\n"
                    + "\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".in(DataUtil.listConvert(value)));\n"
                    + "\t\t\t\t\tbreak;\n";
        } else if (fieldName.endsWith("Uid") || fieldName.endsWith("Id")) {
            body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".eq((String) value));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\tbuilder.and(qEntity." + fieldName + ".like(\"%\" + (String) value + \"%\"));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "	            case \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\tbuilder.and(DataUtil.isTrue(value) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\tbreak;\n";
        } else {
            body = "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                    + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".eq((String) value));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "\":\n"
                    + "\t\t\t\tcase \"" + fieldName + "_like\":\n"
                    + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".like(\"%\" + (String) value + \"%\"));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_le\":\n"
                    + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".like((String) value));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_st\":\n"
                    + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".like((String) value + \"%\"));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_end\":\n"
                    + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".like(\"%\" + (String) value));\n"
                    + "\t\t\t\t\tbreak;\n"
                    + "\t\t\t\tcase \"" + fieldName + "_nn\":\n"
                    + "\t\t\t\t\tbuilder.or(DataUtil.isTrue(value) ? qEntity." + fieldName + ".isNotNull() : qEntity." + fieldName + ".isNull());\n"
                    + "\t\t\t\t\tbreak;\n";
        }
        return body;
    }

    private static String integerOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".eq(DataUtil.integerConvert(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".ne(DataUtil.integerConvert(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String doubleOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".eq(DataUtil.doubleConvert(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".ne(DataUtil.doubleConvert(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String longOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".eq(DataUtil.longConvert(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".ne(DataUtil.longConvert(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String booleanOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".eq(DataUtil.isTrue(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_ne\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".ne(DataUtil.isTrue(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        return body;
    }

    private static String localDateOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".eq(DateTimeUtil.convertLocalDate(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateTimeUtil.minimizedLocalDate(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateTimeUtil.maximizedLocalDate(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String localDateTimeOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateTimeUtil.minimizedLocalDateTime(value)));\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateTimeUtil.maximizedLocalDateTime(value)));\n"
                + "\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateTimeUtil.minimizedLocalDateTime(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateTimeUtil.maximizedLocalDateTime(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String dateOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateTimeUtil.minimized(value)));\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateTimeUtil.maximized(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateTimeUtil.minimized(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateTimeUtil.maximized(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String timestampOrCondition(String fieldName) {
        String body = "\t\t\t\tcase \"" + fieldName + "\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_eq\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateUtil.minimizedToTimestamp(value)));\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateUtil.maximizedToTimestamp(value)));\n"
                + "\t\t\t\tcase \"" + fieldName + "_from\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_goe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".goe(DateUtil.minimizedToTimestamp(value)));\n"
                + "\t\t\t\t\tbreak;\n"
                + "\t\t\t\tcase \"" + fieldName + "_to\":\n"
                + "\t\t\t\tcase \"" + fieldName + "_loe\":\n"
                + "\t\t\t\t\tbuilder.or(qEntity." + fieldName + ".loe(DateUtil.maximizedToTimestamp(value)));\n"
                + "\t\t\t\t\tbreak;\n";
        ;
        return body;
    }

    private static String createExpression(Class<?> targetClass) {
        return createExpression(getFields(targetClass));
    }

    private static String createExpression(Set<Field> fields) {
        String body = "";
        for (Field field : fields) {
            if (field.getAnnotation(Transient.class) != null) {
                continue;
            }
            String expression = "";
            expression = expression.concat("\t\t\tcase \"" + field.getName() + "\":\n");
            expression = expression.concat("\t\t\t\texpression = qEntity." + field.getName() + ";\n");
            expression = expression.concat("\t\t\t\tbreak;\n");
            body = body.concat(expression);
        }
        return body;
    }
}