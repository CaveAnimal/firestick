package com.codetalker.firestick.model;

import java.util.List;

public class FileInfo {
    private String filePath;
    private String packageName;
    private List<String> imports;
    private List<String> staticImports;
    private List<ClassInfo> classes;

    private FileInfo(Builder builder) {
        this.filePath = builder.filePath;
        this.packageName = builder.packageName;
        this.imports = builder.imports;
        this.staticImports = builder.staticImports;
        this.classes = builder.classes;
    }

    public static class Builder {
        private String filePath;
        private String packageName;
    private List<String> imports;
    private List<String> staticImports;
        private List<ClassInfo> classes;

        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder packageName(String packageName) { this.packageName = packageName; return this; }
        public Builder imports(List<String> imports) { this.imports = imports; return this; }
        public Builder staticImports(List<String> staticImports) { this.staticImports = staticImports; return this; }
        public Builder classes(List<ClassInfo> classes) { this.classes = classes; return this; }
        public FileInfo build() { return new FileInfo(this); }
    }

    public String getFilePath() { return filePath; }
    public String getPackageName() { return packageName; }
    public List<String> getImports() { return imports; }
    public List<String> getStaticImports() { return staticImports; }
    public List<ClassInfo> getClasses() { return classes; }

    @Override
    public String toString() {
        return "FileInfo{" +
                "filePath='" + filePath + '\'' +
                ", packageName='" + packageName + '\'' +
                ", imports=" + imports +
        ", staticImports=" + staticImports +
                ", classes=" + classes +
                '}';
    }
}
