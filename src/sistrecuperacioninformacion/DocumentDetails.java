
package sistrecuperacioninformacion;

import java.util.ArrayList;

/**
 *
 * @author Aida Rosa
 */
public class DocumentDetails {
    
    private String nombre;
    private ArrayList<String> token;
    
    public DocumentDetails(String nombre, ArrayList<String> token) {
        this.nombre = nombre;
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<String> getToken() {
        return token;
    }

    public void setToken(ArrayList<String> token) {
        this.token = token;
    }
}
