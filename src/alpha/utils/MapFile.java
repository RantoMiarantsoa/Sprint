package alpha.utils;
import java.io.Byte; 
public class MapFile {
    String nomFichier;
    String extension;
    Byte[] bytes;


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
    public Bytes[] getBytes() {
        return bytes;
    }
    public void setBytes(Bytes[] bytes) {
        this.bytes = bytes;
    }
    
}
