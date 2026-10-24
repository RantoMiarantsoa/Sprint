package alpha.utils;

public class MapFile {
  
    String nomFichier;
    String extension;
    byte[] bytes;

  public MapFile(String nomFichier, String extension, byte[] bytes) {
        this.nomFichier = nomFichier;
        this.extension = extension;
        this.bytes = bytes;
    }
    public MapFile() {
    }
    public String getNomFichier() {
        return nomFichier;
    }
    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public byte[] getBytes() {
        return bytes;
    }
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
}
