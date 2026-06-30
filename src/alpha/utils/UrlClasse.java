package alpha.utils;
import java.util.Objects;
public class UrlClasse {
    String  MethodeHttp;
    String url;



    public UrlClasse(String methodeHttp, String url) {
        MethodeHttp = methodeHttp;
        this.url = url;
    }

    @Override
   public boolean equals(Object obj) {
    UrlClasse urlClasse = (UrlClasse) obj;
    
    return this.url.equals(urlClasse.getUrl()) && this.MethodeHttp.equals(urlClasse.getMethodehttp());
}
   


@Override
public int hashCode(){
    return Objects.hash(url,MethodeHttp);
}
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethodehttp() {
        return MethodeHttp;
    }
    public void setMethodehttp(String methodeHttp) {
        MethodeHttp = methodeHttp;
    }
}
