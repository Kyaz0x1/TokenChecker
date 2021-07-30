package net.kyaz0x1.tokenchecker.files.type;

public enum FileExtensionType {

    TEXT_FILE(".txt");

    private String extension;

    FileExtensionType(String extension){
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

}